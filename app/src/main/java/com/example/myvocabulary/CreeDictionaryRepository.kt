package com.example.myvocabulary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object CreeDictionaryRepository {
    private const val baseUrl = "https://dictionary.plainscree.atlas-ling.ca"
    private val browseControllers = listOf("entries", "keywords", "themes", "morphemes", "ps")
    private const val browseLimit = 100

    suspend fun seedWords(words: List<VocabularyWord>): List<VocabularyWord> = coroutineScope {
        words.map { word ->
            async(Dispatchers.IO) {
                runCatching {
                    resolveSeedWord(word)
                }.getOrElse { error ->
                    if (error is CancellationException) throw error
                    null
                } ?: word
            }
        }.awaitAll()
    }

    suspend fun search(query: String): List<VocabularyWord> = withContext(Dispatchers.IO) {
        val cleaned = query.trim()
        if (cleaned.isBlank()) return@withContext emptyList()

        try {
            val root = fetchJsonObject("$baseUrl/entries/search/${encode(cleaned)}.json")
            val entries = root.optJSONArray("entries") ?: return@withContext emptyList()

            entries.toVocabularyWords()
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            emptyList()
        }
    }

    suspend fun loadBrowseSections(): List<BrowseSection> = coroutineScope {
        browseControllers.map { controller ->
            async(Dispatchers.IO) {
                runCatching {
                    fetchBrowseSection(controller, limit = browseLimit)
                }.getOrElse { error ->
                    if (error is CancellationException) throw error
                    emptyBrowseSection(controller)
                }
            }
        }.awaitAll()
    }

    suspend fun loadAllEntryWords(): List<VocabularyWord> = coroutineScope {
        val firstPage = fetchBrowseSection("entries", page = 1, limit = browseLimit)
        val firstWords = firstPage.items.mapNotNull { it.vocabularyWord }
        val pageCount = firstPage.pageCount
        if (pageCount <= 1) {
            return@coroutineScope firstWords
        }

        val remainingWords = ((2..pageCount).toList())
            .chunked(6)
            .flatMap { chunk ->
                chunk.map { page ->
                    async(Dispatchers.IO) {
                        runCatching {
                            fetchBrowseSection("entries", page = page, limit = browseLimit)
                                .items
                                .mapNotNull { it.vocabularyWord }
                        }.getOrElse { error ->
                            if (error is CancellationException) throw error
                            emptyList()
                        }
                    }
                }.awaitAll().flatten()
            }

        (firstWords + remainingWords)
            .distinctBy { it.id }
    }

    suspend fun syncAllEntriesIntoRoom(
        vocabularyDao: VocabularyDao,
        syncStateDao: DictionarySyncStateDao
    ) {
        val firstPage = fetchBrowseSection("entries", page = 1, limit = browseLimit)
        val totalPages = firstPage.pageCount.coerceAtLeast(1)
        var syncedWords = 0

        suspend fun store(section: BrowseSection) {
            val words = section.items.mapNotNull { it.vocabularyWord }
            if (words.isNotEmpty()) {
                vocabularyDao.upsertAll(words)
                syncedWords += words.size
                syncStateDao.upsert(
                    DictionarySyncState(
                        name = "entries",
                        isComplete = false,
                        lastSyncedAtMillis = System.currentTimeMillis(),
                        syncedWords = syncedWords,
                        totalWords = firstPage.count
                    )
                )
            }
        }

        store(firstPage)

        if (totalPages > 1) {
            for (chunk in (2..totalPages).chunked(6)) {
                val fetchedPages = coroutineScope {
                    chunk.map { page ->
                        async(Dispatchers.IO) {
                            runCatching {
                                fetchBrowseSection("entries", page = page, limit = browseLimit)
                            }.getOrElse { error ->
                                if (error is CancellationException) throw error
                                null
                            }
                        }
                    }.awaitAll()
                }

                chunk.zip(fetchedPages).forEach { (_, section) ->
                    if (section != null) {
                        store(section)
                    }
                }
            }
        }

        syncStateDao.upsert(
            DictionarySyncState(
                name = "entries",
                isComplete = true,
                lastSyncedAtMillis = System.currentTimeMillis(),
                syncedWords = syncedWords,
                totalWords = firstPage.count
            )
        )
    }

    suspend fun fetchBrowseSection(controller: String, page: Int = 1, limit: Int = browseLimit): BrowseSection = withContext(Dispatchers.IO) {
        val normalizedController = controller.trim().lowercase()
        val meta = browseMeta(normalizedController)
        val root = fetchJsonObject("$baseUrl/$normalizedController/index/page:$page/limit:$limit.json")
        val paging = root.optJSONObject("paging")?.optJSONObject(meta.pagingKey)
        val count = paging?.optInt("count") ?: 0
        val pageCount = paging?.optInt("pageCount") ?: 0
        val tiles = root.optJSONArray("tiles").toStringList()

        val items = when (normalizedController) {
            "entries" -> root.optJSONArray("entries").toBrowseItems()
            "keywords" -> root.optJSONArray("keywords").toBrowseItems()
            "themes" -> root.optJSONArray("themes").toBrowseItems()
            "morphemes" -> root.optJSONArray("morphemes").toBrowseItems()
            "ps" -> root.optJSONArray("ps").toBrowseItems()
            else -> emptyList()
        }

        BrowseSection(
            controller = normalizedController,
            title = meta.title,
            count = count,
            pageCount = pageCount,
            tiles = tiles,
            items = items
        )
    }

    suspend fun resolveSeedWord(seed: VocabularyWord): VocabularyWord? = withContext(Dispatchers.IO) {
        try {
            val exact = search(seed.cree).firstOrNull { it.cree.equals(seed.cree, ignoreCase = true) }
                ?: search(seed.english).firstOrNull {
                    it.english.equals(seed.english, ignoreCase = true) ||
                        it.cree.equals(seed.cree, ignoreCase = true)
                }

            if (exact == null) {
                return@withContext null
            }

            loadWordDetail(
                remoteUuid = exact.remoteUuid.ifBlank { exact.id },
                appId = seed.id,
                iconFallback = seed.icon
            ) ?: exact.copy(
                id = seed.id,
                remoteUuid = exact.remoteUuid.ifBlank { exact.id },
                icon = seed.icon
            )
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            null
        }
    }

    suspend fun loadWordDetail(remoteUuid: String, appId: String? = null, iconFallback: String = ""): VocabularyWord? =
        withContext(Dispatchers.IO) {
            try {
                val root = fetchJsonObject("$baseUrl/entries/view/$remoteUuid.json")
                val entry = root.optJSONObject("entry") ?: return@withContext null
                val entryData = entry.optJSONObject("Entry") ?: return@withContext null
                val psData = entry.optJSONObject("Ps")
                val stemData = entry.optJSONObject("Stem")
                val baseWord = entryData.toVocabularyWord(
                    appId = appId ?: remoteUuid,
                    remoteUuid = remoteUuid,
                    psData = psData,
                    stemData = stemData,
                    iconFallback = iconFallback
                )

                val relatedWords = loadRelatedWords(entry.optJSONArray("Keyword"), remoteUuid)
                if (relatedWords.isEmpty()) {
                    return@withContext baseWord
                }

                baseWord.copy(
                    relatedWordIds = relatedWords.map { it.id },
                    relatedSemanticRelationLabels = relatedWords.map { "RELATED WORD" }
                )
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                null
            }
        }

    private suspend fun loadRelatedWords(keywordArray: JSONArray?, currentRemoteUuid: String): List<VocabularyWord> {
        if (keywordArray == null || keywordArray.length() == 0) return emptyList()

        val related = linkedMapOf<String, VocabularyWord>()
        for (index in 0 until keywordArray.length()) {
            val keyword = keywordArray.optJSONObject(index) ?: continue
            val keywordUuid = keyword.optString("uuid").trim()
            if (keywordUuid.isBlank()) continue

            val entries = try {
                val keywordRoot = fetchJsonObject("$baseUrl/keywords/view/$keywordUuid.json")
                keywordRoot.optJSONObject("keyword")?.optJSONArray("Entry")
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                null
            } ?: continue
            for (entryIndex in 0 until entries.length()) {
                val entry = entries.optJSONObject(entryIndex) ?: continue
                val remoteUuid = entry.optString("uuid").trim()
                if (remoteUuid.isBlank() || remoteUuid == currentRemoteUuid) continue

                val mapped = entry.toVocabularyWord(
                    appId = remoteUuid,
                    remoteUuid = remoteUuid,
                    psData = entry.optJSONObject("Ps"),
                    stemData = null,
                    iconFallback = deriveIcon(entry.optString("definition_en"), entry.optString("roman"))
                )
                related[remoteUuid] = mapped
            }
        }

        return related.values.toList()
    }

    private fun JSONArray.toVocabularyWords(): List<VocabularyWord> {
        val words = mutableListOf<VocabularyWord>()
        for (index in 0 until length()) {
            val result = optJSONObject(index) ?: continue
            val entry = result.optJSONObject("Entry") ?: continue
            words += entry.toVocabularyWord(
                appId = entry.optString("uuid"),
                remoteUuid = entry.optString("uuid"),
                psData = result.optJSONObject("Ps"),
                stemData = result.optJSONObject("Stem"),
                iconFallback = deriveIcon(entry.optString("definition_en"), entry.optString("roman"))
            )
        }
        return words
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        val values = mutableListOf<String>()
        for (index in 0 until length()) {
            val value = optString(index).trim()
            if (value.isNotBlank()) {
                values += value
            }
        }
        return values
    }

    private fun JSONArray?.toBrowseItems(): List<BrowseItem> {
        if (this == null) return emptyList()
        val items = mutableListOf<BrowseItem>()
        for (index in 0 until length()) {
            val raw = optJSONObject(index) ?: continue
            when {
                raw.has("Entry") -> {
                    val entry = raw.optJSONObject("Entry") ?: continue
                    val psData = raw.optJSONObject("Ps")
                    val stemData = raw.optJSONObject("Stem")
                    val word = entry.toVocabularyWord(
                        appId = entry.optString("uuid"),
                        remoteUuid = entry.optString("uuid"),
                        psData = psData,
                        stemData = stemData,
                        iconFallback = deriveIcon(entry.optString("definition_en"), entry.optString("roman"))
                    )
                    items += BrowseItem(
                        id = word.id,
                        title = word.cree.ifBlank { word.english },
                        subtitle = word.english,
                        detail = word.partOfSpeech,
                        icon = word.icon,
                        entryCount = 0,
                        remoteUuid = word.remoteUuid,
                        vocabularyWord = word
                    )
                }
                raw.has("Keyword") -> {
                    val keyword = raw.optJSONObject("Keyword") ?: continue
                    items += BrowseItem(
                        id = keyword.optString("uuid").ifBlank { keyword.optString("keyword") },
                        title = keyword.optString("keyword").ifBlank { keyword.optString("public_note") },
                        subtitle = "${keyword.optString("entry_count")} entries",
                        detail = keyword.optString("public_note"),
                        icon = deriveIcon(keyword.optString("keyword"), keyword.optString("public_note")),
                        entryCount = keyword.optInt("entry_count"),
                        remoteUuid = keyword.optString("uuid")
                    )
                }
                raw.has("Morpheme") -> {
                    val morpheme = raw.optJSONObject("Morpheme") ?: continue
                    items += BrowseItem(
                        id = morpheme.optString("uuid").ifBlank { morpheme.optString("roman") },
                        title = morpheme.optString("roman").ifBlank { morpheme.optString("syllabic") },
                        subtitle = morpheme.optString("definition_en").ifBlank { "Morpheme" },
                        detail = morpheme.optString("public_note_en"),
                        icon = deriveIcon(morpheme.optString("roman"), morpheme.optString("definition_en")),
                        entryCount = morpheme.optInt("entry_count"),
                        remoteUuid = morpheme.optString("uuid")
                    )
                }
                raw.has("Ps") -> {
                    val ps = raw.optJSONObject("Ps") ?: continue
                    items += BrowseItem(
                        id = ps.optString("uuid").ifBlank { ps.optString("ps") },
                        title = ps.optString("ps").ifBlank { ps.optString("public_note_en") },
                        subtitle = "${ps.optString("entry_count")} entries",
                        detail = ps.optString("public_note_en"),
                        icon = deriveIcon(ps.optString("ps"), ps.optString("public_note_en")),
                        entryCount = ps.optInt("entry_count"),
                        remoteUuid = ps.optString("uuid")
                    )
                }
                raw.has("Theme") -> {
                    val theme = raw.optJSONObject("Theme") ?: continue
                    items += BrowseItem(
                        id = theme.optString("uuid").ifBlank { theme.optString("translation_en") },
                        title = theme.optString("translation_en").ifBlank { theme.optString("public_note_en") },
                        subtitle = "${theme.optString("entry_count")} entries",
                        detail = theme.optString("public_note_en"),
                        icon = deriveIcon(theme.optString("translation_en"), theme.optString("public_note_en")),
                        entryCount = theme.optInt("entry_count"),
                        remoteUuid = theme.optString("uuid")
                    )
                }
            }
        }
        return items
    }

    private fun browseMeta(controller: String): BrowseMeta = when (controller) {
        "entries" -> BrowseMeta("Entries", "Entry")
        "keywords" -> BrowseMeta("Keywords", "Keyword")
        "themes" -> BrowseMeta("Themes", "Theme")
        "morphemes" -> BrowseMeta("Morphemes", "Morpheme")
        "ps" -> BrowseMeta("Parts of Speech", "Ps")
        else -> BrowseMeta(controller.replaceFirstChar { it.uppercase() }, controller.replaceFirstChar { it.uppercase() })
    }

    private fun emptyBrowseSection(controller: String): BrowseSection {
        val meta = browseMeta(controller)
        return BrowseSection(
            controller = controller,
            title = meta.title,
            count = 0,
            pageCount = 0,
            tiles = emptyList(),
            items = emptyList()
        )
    }

    private data class BrowseMeta(
        val title: String,
        val pagingKey: String
    )

    private fun JSONObject.toVocabularyWord(
        appId: String,
        remoteUuid: String,
        psData: JSONObject?,
        stemData: JSONObject?,
        iconFallback: String
    ): VocabularyWord {
        val cree = optString("roman").ifBlank { optString("entry_search") }.trim()
        val english = optString("definition_en").ifBlank {
            optString("definition_idiomatic").ifBlank {
                optString("definition_literal").ifBlank { cree }
            }
        }.trim()
        val partOfSpeech = psData?.optString("ps").orEmpty().trim().ifBlank {
            psData?.optString("public_note_en").orEmpty().trim().ifBlank { "word" }
        }
        val stemName = stemData?.optString("name_en").orEmpty().trim()
        val stemMeaning = stemData?.optString("definition_en").orEmpty().trim()
        val stemNote = psData?.optString("public_note_en").orEmpty().trim()
        val morphology = buildString {
            if (stemName.isNotBlank()) {
                append("Stem: ")
                append(stemName)
            }
            if (stemMeaning.isNotBlank()) {
                if (isNotEmpty()) append(" - ")
                append(stemMeaning)
            }
            if (stemNote.isNotBlank()) {
                if (isNotEmpty()) append(" - ")
                append(stemNote)
            }
            if (isEmpty()) {
                append("Real dictionary entry.")
            }
        }
        val subject = inferSubject(cree, english, partOfSpeech, stemNote)
        return VocabularyWord(
            id = appId,
            cree = cree,
            english = english,
            partOfSpeech = partOfSpeech,
            subject = subject,
            categoryLabel = subject.label,
            pronunciationLabel = if ((optJSONArray("Media")?.length() ?: 0) > 0) "Play Audio" else "Play Audio",
            exampleTitle = "Dictionary Sense",
            exampleSentence = english,
            relatedWordIds = emptyList(),
            morphology = morphology,
            detailedMorphology = DetailedMorphology(
                stem = stemName,
                stemMeaning = stemMeaning,
                grammaticalForm = stemNote.ifBlank { partOfSpeech }
            ),
            icon = iconFallback.ifBlank { deriveIcon(english, cree) },
            remoteUuid = remoteUuid
        )
    }

    private suspend fun fetchJsonObject(url: String): JSONObject = withContext(Dispatchers.IO) {
        val body = httpGet(url)
        val jsonText = extractJsonBody(body)
        JSONObject(jsonText)
    }

    private fun extractJsonBody(response: String): String {
        val trimmed = response.trim()
        val jsonStart = Regex("""\{\s*"(entry|keyword|entries|keywords|theme|themes|morpheme|morphemes|ps)"\s*:""", RegexOption.DOT_MATCHES_ALL)
            .find(trimmed)
            ?.range
            ?.first

        if (jsonStart != null) {
            return trimmed.substring(jsonStart)
        }

        val fallbackStart = trimmed.lastIndexOf('{')
        require(fallbackStart >= 0) { "Unable to locate JSON payload." }
        return trimmed.substring(fallbackStart)
    }

    private fun httpGet(url: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 30000
            setRequestProperty("Accept", "application/json,text/html")
            setRequestProperty("User-Agent", "Mozilla/5.0")
        }

        return try {
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            stream?.bufferedReader()?.use { reader -> reader.readText() }.orEmpty()
        } finally {
            connection.disconnect()
        }
    }

    private fun encode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8.name())

    private fun deriveIcon(primary: String, secondary: String): String {
        val source = primary.ifBlank { secondary }
        val parts = source.split(Regex("\\s+")).filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "CR"
            parts.size == 1 -> parts.first().take(2).uppercase()
            else -> parts.take(2).joinToString("") { it.first().uppercaseChar().toString() }.take(3)
        }
    }

    private fun inferSubject(cree: String, english: String, partOfSpeech: String, note: String): SubjectFilter {
        val text = listOf(cree, english, partOfSpeech, note).joinToString(" ").lowercase()
        return when {
            text.anyContains("rabbit", "deer", "fox", "wolf", "bird", "beaver", "fish", "animal", "mosquito") ->
                SubjectFilter.Animals
            text.anyContains("heart", "hand", "hands", "eye", "eyes", "head", "nose", "mouth", "foot", "feet", "body") ->
                SubjectFilter.Body
            text.anyContains("rain", "snow", "wind", "cloud", "thunder", "moon", "sun", "cold", "weather") ->
                SubjectFilter.Weather
            text.anyContains("bread", "berry", "water", "meat", "tea", "milk", "eat", "soup") ->
                SubjectFilter.Foods
            text.anyContains("road", "river", "lake", "home", "camp", "forest", "house", "travel", "land") ->
                SubjectFilter.Lands
            else -> SubjectFilter.Words
        }
    }

    private fun String.anyContains(vararg needles: String): Boolean {
        return needles.any { contains(it, ignoreCase = true) }
    }
}
