package com.example.myvocabulary

enum class Screen(val route: String, val label: String) {
    Home("home", "Home"),
    Categories("categories", "Categories"),
    Search("search", "Search Results"),
    Details("details/{wordId}", "Word Details"),
    SemanticMap("semantic-map/{wordId}", "Semantic Map"),
    Settings("settings", "Settings"),
    Expert("expert", "Expert Mode"),
    RecentSearches("recent-searches", "Recent Searches");

    companion object {
        fun detailsRoute(wordId: String) = "details/$wordId"
        fun semanticMapRoute(wordId: String) = "semantic-map/$wordId"
    }
}

enum class SubjectFilter(val label: String) {
    All("All"),
    Animals("Animals"),
    Body("Body"),
    Weather("Weather"),
    Words("Words"),
    Foods("Foods"),
    Lands("Lands");

    companion object {
        val cycleOrder = entries
    }
}

enum class WordTypeFilter(val label: String) {
    All("All"),
    Noun("Noun"),
    Verb("Verb"),
    Adjective("Adjective"),
    Phrase("Phrase");

    companion object {
        val cycleOrder = entries
    }
}

enum class SortOption(val label: String) {
    Relevance("Relevance"),
    Alphabetical("A-Z"),
    Recent("Recent");

    companion object {
        val cycleOrder = entries
    }
}

enum class DisplayLanguage(val label: String) {
    Both("Both"),
    Cree("Cree"),
    English("English");

    companion object {
        val cycleOrder = entries
    }
}

data class VocabularyWord(
    val id: String,
    val cree: String,
    val english: String,
    val partOfSpeech: String,
    val subject: SubjectFilter,
    val categoryLabel: String,
    val pronunciationLabel: String,
    val exampleTitle: String,
    val exampleSentence: String,
    val relatedWordIds: List<String>,
    val morphology: String = "",
    val icon: String
)

data class CategoryCard(
    val title: String,
    val description: String,
    val subject: SubjectFilter,
    val count: Int,
    val icon: String
)

val vocabularyWords = listOf(
    VocabularyWord(
        id = "wapos",
        cree = "Wâpos",
        english = "rabbit",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Nîhka wâpos ôtâ. (I see a rabbit.)",
        relatedWordIds = listOf("waposis", "wapak", "wapit"),
        morphology = "Root form: wapos. Simple noun.",
        icon = "RB"
    ),
    VocabularyWord(
        id = "waposis",
        cree = "Wâposis",
        english = "small rabbit",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Wâposis nisto. (The small rabbit is near.)",
        relatedWordIds = listOf("wapos", "wapit", "misko"),
        morphology = "wapos + -is. Diminutive form meaning \"small rabbit\".",
        icon = "RBT"
    ),
    VocabularyWord(
        id = "wapak",
        cree = "Wâpâk",
        english = "hare",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Wâpâk nipâw. (The hare is resting.)",
        relatedWordIds = listOf("wapos", "waposis", "mistik"),
        morphology = "Root form: wapak. Simple noun.",
        icon = "HR"
    ),
    VocabularyWord(
        id = "wapit",
        cree = "Wâpitiw",
        english = "animal",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Wâpitiw kîsikâw. (The animal is outside.)",
        relatedWordIds = listOf("wapos", "waposis", "mako"),
        morphology = "Root form: wapitiw. Simple noun.",
        icon = "AN"
    ),
    VocabularyWord(
        id = "mosquito",
        cree = "Môswa",
        english = "mosquito",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Môswa papâs. (A mosquito is buzzing.)",
        relatedWordIds = listOf("wapos", "rain", "foods"),
        morphology = "Root form: moswa. Simple noun.",
        icon = "MSQ"
    ),
    VocabularyWord(
        id = "rain",
        cree = "Kisâw",
        english = "rain",
        partOfSpeech = "noun",
        subject = SubjectFilter.Weather,
        categoryLabel = "Weather",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Kisâw ohcihî. (It is raining.)",
        relatedWordIds = listOf("snow", "wind", "cloud"),
        morphology = "Root form: kisaw. Simple noun.",
        icon = "RN"
    ),
    VocabularyWord(
        id = "snow",
        cree = "Mistapêw",
        english = "snow",
        partOfSpeech = "noun",
        subject = SubjectFilter.Weather,
        categoryLabel = "Weather",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mistapêw osâw. (Snow is falling.)",
        relatedWordIds = listOf("rain", "wind", "cloud"),
        morphology = "Root form: mistapew. Simple noun.",
        icon = "SN"
    ),
    VocabularyWord(
        id = "wind",
        cree = "Miyôw",
        english = "wind",
        partOfSpeech = "noun",
        subject = SubjectFilter.Weather,
        categoryLabel = "Weather",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Miyôw kisêw. (The wind is cold.)",
        relatedWordIds = listOf("rain", "snow", "cloud"),
        morphology = "Root form: miyow. Simple noun.",
        icon = "WN"
    ),
    VocabularyWord(
        id = "cloud",
        cree = "Pîkiskwêw",
        english = "cloud",
        partOfSpeech = "noun",
        subject = SubjectFilter.Weather,
        categoryLabel = "Weather",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Pîkiskwêw kakîy. (Clouds gather.)",
        relatedWordIds = listOf("rain", "snow", "wind"),
        morphology = "Root form: pikiskwew. Simple noun.",
        icon = "CL"
    ),
    VocabularyWord(
        id = "heart",
        cree = "Mîyohtâw",
        english = "heart",
        partOfSpeech = "noun",
        subject = SubjectFilter.Body,
        categoryLabel = "Body",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mîyohtâw maskawis. (The heart is strong.)",
        relatedWordIds = listOf("body", "hands", "eyes"),
        morphology = "Root form: miyohtaw. Simple noun.",
        icon = "HT"
    ),
    VocabularyWord(
        id = "hands",
        cree = "Nîpîn",
        english = "hands",
        partOfSpeech = "noun",
        subject = SubjectFilter.Body,
        categoryLabel = "Body",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Nîpîn misko. (The hands are red.)",
        relatedWordIds = listOf("heart", "eyes", "feet"),
        morphology = "Root form: nipin. Simple noun.",
        icon = "HD"
    ),
    VocabularyWord(
        id = "eat",
        cree = "Mîciso",
        english = "eat",
        partOfSpeech = "verb",
        subject = SubjectFilter.Foods,
        categoryLabel = "Foods",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mîciso kisêw. (Eat the meal.)",
        relatedWordIds = listOf("bread", "berry", "water"),
        morphology = "Verb stem: miciso. Action verb meaning \"eat\".",
        icon = "FD"
    ),
    VocabularyWord(
        id = "bread",
        cree = "Pimîwâkan",
        english = "bread",
        partOfSpeech = "noun",
        subject = SubjectFilter.Foods,
        categoryLabel = "Foods",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Pimîwâkan nistam. (Bread first.)",
        relatedWordIds = listOf("eat", "berry", "water"),
        morphology = "Root form: pimiwaakan. Simple noun.",
        icon = "BR"
    ),
    VocabularyWord(
        id = "travel",
        cree = "Pimipahtâw",
        english = "travel",
        partOfSpeech = "verb",
        subject = SubjectFilter.Lands,
        categoryLabel = "Lands",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Pimipahtâw ohci. (Travel to the land.)",
        relatedWordIds = listOf("road", "home", "camp"),
        morphology = "Verb stem: pimipahtaw. Action verb meaning \"travel\".",
        icon = "TR"
    ),
    VocabularyWord(
        id = "road",
        cree = "Mîchif",
        english = "road",
        partOfSpeech = "noun",
        subject = SubjectFilter.Lands,
        categoryLabel = "Lands",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mîchif kahkiyaw. (The road is open.)",
        relatedWordIds = listOf("travel", "home", "camp"),
        morphology = "Root form: michif. Simple noun.",
        icon = "RD"
    ),
    VocabularyWord(
        id = "body",
        cree = "Nikaw",
        english = "body",
        partOfSpeech = "noun",
        subject = SubjectFilter.Body,
        categoryLabel = "Body",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Nikaw maskawis. (The body is strong.)",
        relatedWordIds = listOf("heart", "hands", "eyes"),
        morphology = "Root form: nikaw. Simple noun.",
        icon = "BD"
    ),
    VocabularyWord(
        id = "eyes",
        cree = "Mitaw",
        english = "eyes",
        partOfSpeech = "noun",
        subject = SubjectFilter.Body,
        categoryLabel = "Body",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mitaw kisikaw. (The eyes are open.)",
        relatedWordIds = listOf("heart", "hands", "feet"),
        morphology = "Root form: mitaw. Simple noun.",
        icon = "EY"
    ),
    VocabularyWord(
        id = "feet",
        cree = "Mitapan",
        english = "feet",
        partOfSpeech = "noun",
        subject = SubjectFilter.Body,
        categoryLabel = "Body",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mitapan pakam. (The feet are moving.)",
        relatedWordIds = listOf("hands", "eyes", "body"),
        morphology = "Root form: mitapan. Simple noun.",
        icon = "FT"
    ),
    VocabularyWord(
        id = "berry",
        cree = "Mimin",
        english = "berry",
        partOfSpeech = "noun",
        subject = SubjectFilter.Foods,
        categoryLabel = "Foods",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mimin mIciso. (Eat the berries.)",
        relatedWordIds = listOf("eat", "bread", "water"),
        morphology = "Root form: mimin. Simple noun.",
        icon = "BY"
    ),
    VocabularyWord(
        id = "water",
        cree = "Nipiy",
        english = "water",
        partOfSpeech = "noun",
        subject = SubjectFilter.Foods,
        categoryLabel = "Foods",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Nipiy kisikaw. (Water is clear.)",
        relatedWordIds = listOf("eat", "bread", "berry"),
        morphology = "Root form: nipiy. Simple noun.",
        icon = "WT"
    ),
    VocabularyWord(
        id = "home",
        cree = "Kisik",
        english = "home",
        partOfSpeech = "noun",
        subject = SubjectFilter.Lands,
        categoryLabel = "Lands",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Kisik nistam. (Home first.)",
        relatedWordIds = listOf("travel", "road", "camp"),
        morphology = "Root form: kisik. Simple noun.",
        icon = "HM"
    ),
    VocabularyWord(
        id = "camp",
        cree = "Miskotahk",
        english = "camp",
        partOfSpeech = "noun",
        subject = SubjectFilter.Lands,
        categoryLabel = "Lands",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Miskotahk nipaw. (The camp is quiet.)",
        relatedWordIds = listOf("travel", "road", "home"),
        morphology = "Root form: miskotahk. Simple noun.",
        icon = "CP"
    ),
    VocabularyWord(
        id = "misko",
        cree = "Miskow",
        english = "red",
        partOfSpeech = "adjective",
        subject = SubjectFilter.Words,
        categoryLabel = "Words",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Miskow mina. (The berry is red.)",
        relatedWordIds = listOf("wapos", "waposis", "berry"),
        morphology = "Root form: miskow. Descriptive adjective.",
        icon = "RD"
    ),
    VocabularyWord(
        id = "mistik",
        cree = "Mistikw",
        english = "tree",
        partOfSpeech = "noun",
        subject = SubjectFilter.Lands,
        categoryLabel = "Lands",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mistikw kihcik. (The tree is tall.)",
        relatedWordIds = listOf("road", "home", "camp"),
        morphology = "Root form: mistikw. Simple noun.",
        icon = "TR"
    ),
    VocabularyWord(
        id = "mako",
        cree = "Mako",
        english = "bear",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mako minis. (The bear is nearby.)",
        relatedWordIds = listOf("wapos", "wapit", "misko"),
        morphology = "Root form: mako. Simple noun.",
        icon = "BR"
    ),
    VocabularyWord(
        id = "deer",
        cree = "Atik",
        english = "deer",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Atik miyo. (The deer is calm.)",
        relatedWordIds = listOf("wapos", "mako", "fish"),
        morphology = "Root form: atik. Simple noun.",
        icon = "DR"
    ),
    VocabularyWord(
        id = "sun",
        cree = "Pisim",
        english = "sun",
        partOfSpeech = "noun",
        subject = SubjectFilter.Weather,
        categoryLabel = "Weather",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Pisim kisikaw. (The sun is bright.)",
        relatedWordIds = listOf("rain", "cloud", "wind"),
        morphology = "Root form: pisim. Simple noun.",
        icon = "SU"
    ),
    VocabularyWord(
        id = "lake",
        cree = "Sakahikan",
        english = "lake",
        partOfSpeech = "noun",
        subject = SubjectFilter.Lands,
        categoryLabel = "Lands",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Sakahikan mino. (The lake is peaceful.)",
        relatedWordIds = listOf("water", "home", "camp"),
        morphology = "Root form: sakahikan. Simple noun.",
        icon = "LK"
    ),
    VocabularyWord(
        id = "fish",
        cree = "Kinoo",
        english = "fish",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Kinoo nipiy. (The fish is in the water.)",
        relatedWordIds = listOf("water", "eat", "berry"),
        morphology = "Root form: kinoo. Simple noun.",
        icon = "FH"
    ),
    VocabularyWord(
        id = "soup",
        cree = "Micim",
        english = "soup",
        partOfSpeech = "noun",
        subject = SubjectFilter.Foods,
        categoryLabel = "Foods",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Micim mino. (The soup is good.)",
        relatedWordIds = listOf("eat", "bread", "water"),
        morphology = "Root form: micim. Simple noun.",
        icon = "SP"
    ),
    VocabularyWord(
        id = "friend",
        cree = "Nitis",
        english = "friend",
        partOfSpeech = "noun",
        subject = SubjectFilter.Words,
        categoryLabel = "Words",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Nitis kwayask. (A friend is nearby.)",
        relatedWordIds = listOf("hello", "good", "home"),
        morphology = "Root form: nitis. Simple noun.",
        icon = "FR"
    ),
    VocabularyWord(
        id = "hello",
        cree = "Tansi",
        english = "hello",
        partOfSpeech = "phrase",
        subject = SubjectFilter.Words,
        categoryLabel = "Words",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Tansi, nitis! (Hello, friend!)",
        relatedWordIds = listOf("friend", "home", "camp"),
        morphology = "Greeting phrase used to say hello.",
        icon = "HI"
    ),
    VocabularyWord(
        id = "good",
        cree = "Miyo",
        english = "good",
        partOfSpeech = "adjective",
        subject = SubjectFilter.Words,
        categoryLabel = "Words",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Miyo nitis. (A good friend.)",
        relatedWordIds = listOf("misko", "friend", "hello"),
        morphology = "Descriptive adjective meaning \"good\".",
        icon = "GD"
    )
)

fun morphologyForWord(word: VocabularyWord): String = when (word.id) {
    "wapos" -> "Root form: wapos. Simple noun."
    "waposis" -> "wapos + -is. Diminutive form meaning \"small rabbit\"."
    "wapak" -> "Root form: wapak. Simple noun."
    "wapit" -> "Root form: wapitiw. Simple noun."
    "mosquito" -> "Root form: moswa. Simple noun."
    "rain" -> "Root form: kisaw. Simple noun."
    "snow" -> "Root form: mistapew. Simple noun."
    "wind" -> "Root form: miyow. Simple noun."
    "cloud" -> "Root form: pikiskwew. Simple noun."
    "heart" -> "Root form: miyohtaw. Simple noun."
    "hands" -> "Root form: nipin. Simple noun."
    "eat" -> "Verb stem: miciso. Action verb meaning \"eat\"."
    "bread" -> "Root form: pimiwaakan. Simple noun."
    "travel" -> "Verb stem: pimipahtaw. Action verb meaning \"travel\"."
    "road" -> "Root form: michif. Simple noun."
    "body" -> "Root form: nikaw. Simple noun."
    "eyes" -> "Root form: mitaw. Simple noun."
    "feet" -> "Root form: mitapan. Simple noun."
    "berry" -> "Root form: mimin. Simple noun."
    "water" -> "Root form: nipiy. Simple noun."
    "home" -> "Root form: kisik. Simple noun."
    "camp" -> "Root form: miskotahk. Simple noun."
    "misko" -> "Root form: miskow. Descriptive adjective."
    "mistik" -> "Root form: mistikw. Simple noun."
    "mako" -> "Root form: mako. Simple noun."
    "deer" -> "Root form: atik. Simple noun."
    "sun" -> "Root form: pisim. Simple noun."
    "lake" -> "Root form: sakahikan. Simple noun."
    "fish" -> "Root form: kinoo. Simple noun."
    "soup" -> "Root form: micim. Simple noun."
    "friend" -> "Root form: nitis. Simple noun."
    "hello" -> "Greeting phrase used to say hello."
    "good" -> "Descriptive adjective meaning \"good\"."
    else -> "Morphology not available."
}

val suggestedCategories = listOf(
    CategoryCard("Animals", "Common animal words", SubjectFilter.Animals, 8, "AN"),
    CategoryCard("Body", "Words associated with the body", SubjectFilter.Body, 5, "BD"),
    CategoryCard("Weather", "Learn vocabulary for the weather", SubjectFilter.Weather, 5, "WE"),
    CategoryCard("Words", "Useful words and phrases", SubjectFilter.Words, 4, "WR"),
    CategoryCard("Foods", "Food and cooking terms", SubjectFilter.Foods, 5, "FD"),
    CategoryCard("Lands", "Travel and place names", SubjectFilter.Lands, 6, "LD")
)

val wordOfDayIds = listOf("wapos", "rain", "heart")

fun VocabularyWord.matches(query: String, subject: SubjectFilter, wordType: WordTypeFilter): Boolean {
    val subjectMatches = subject == SubjectFilter.All || this.subject == subject
    val typeMatches = wordType == WordTypeFilter.All ||
        partOfSpeech.equals(wordType.label, ignoreCase = true)
    val queryMatches = query.isBlank() || searchScore(query) > 0

    return queryMatches && subjectMatches && typeMatches
}

fun sortWords(words: List<VocabularyWord>, sortOption: SortOption): List<VocabularyWord> = when (sortOption) {
    SortOption.Relevance -> words
    SortOption.Alphabetical -> words.sortedBy { it.english.lowercase() }
    SortOption.Recent -> words.sortedByDescending { it.id }
}

fun searchWords(
    words: List<VocabularyWord>,
    query: String,
    subject: SubjectFilter,
    wordType: WordTypeFilter,
    sortOption: SortOption
): List<VocabularyWord> {
    val filtered = words.filter { it.matches(query, subject, wordType) }
    return when (sortOption) {
        SortOption.Relevance -> filtered.sortedByDescending { it.searchScore(query) }
        else -> sortWords(filtered, sortOption)
    }
}

private fun VocabularyWord.searchScore(query: String): Int {
    val normalizedQuery = query.trim().lowercase()
    if (normalizedQuery.isBlank()) return 0

    val tokens = normalizedQuery.split(Regex("\\s+")).filter { it.isNotBlank() }
    val searchableFields = listOf(
        id.lowercase(),
        cree.lowercase(),
        english.lowercase(),
        partOfSpeech.lowercase(),
        categoryLabel.lowercase(),
        exampleTitle.lowercase(),
        exampleSentence.lowercase(),
        morphology.lowercase(),
        relatedWordIds.joinToString(" ").lowercase()
    )
    val searchable = searchableFields.joinToString(" ")

    var score = 0
    if (searchable.contains(normalizedQuery)) score += 20
    if (cree.equals(normalizedQuery, ignoreCase = true)) score += 100
    if (english.equals(normalizedQuery, ignoreCase = true)) score += 90
    if (id.equals(normalizedQuery, ignoreCase = true)) score += 80
    if (categoryLabel.equals(normalizedQuery, ignoreCase = true)) score += 50
    if (partOfSpeech.equals(normalizedQuery, ignoreCase = true)) score += 40
    if (morphology.contains(normalizedQuery, ignoreCase = true)) score += 30
    if (exampleSentence.contains(normalizedQuery, ignoreCase = true)) score += 15

    tokens.forEach { token ->
        when {
            cree.contains(token, ignoreCase = true) -> score += 8
            english.contains(token, ignoreCase = true) -> score += 8
            id.contains(token, ignoreCase = true) -> score += 7
            categoryLabel.contains(token, ignoreCase = true) -> score += 5
            partOfSpeech.contains(token, ignoreCase = true) -> score += 4
            morphology.contains(token, ignoreCase = true) -> score += 4
        }
    }
    return score
}

fun previousCycleValue(
    current: Enum<*>,
    cycle: List<out Enum<*>>
): Enum<*> {
    val index = cycle.indexOfFirst { it == current }
    return if (index <= 0) cycle.last() else cycle[index - 1]
}

fun nextCycleValue(
    current: Enum<*>,
    cycle: List<out Enum<*>>
): Enum<*> {
    val index = cycle.indexOfFirst { it == current }
    return if (index == -1 || index == cycle.lastIndex) cycle.first() else cycle[index + 1]
}
