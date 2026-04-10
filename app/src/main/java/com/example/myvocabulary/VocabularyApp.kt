package com.example.myvocabulary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myvocabulary.ui.theme.Accent
import com.example.myvocabulary.ui.theme.AccentDark
import com.example.myvocabulary.ui.theme.MyVocabularyTheme
import kotlinx.coroutines.withContext

private data class SearchCacheKey(
    val query: String,
    val subject: SubjectFilter,
    val wordType: WordTypeFilter,
    val sort: SortOption
)

private fun VocabularyWord.needsRemoteSemanticFetch(): Boolean {
    return remoteUuid.isNotBlank() && relatedSemanticRelationLabels.isEmpty()
}

private data class WordDetailUiState(
    val bundle: CreeDictionaryRepository.WordDetailBundle,
    val isLoading: Boolean
)

@Composable
private fun rememberWordDetailState(
    word: VocabularyWord,
    vocabularyWordsById: Map<String, VocabularyWord>,
    vocabularyDao: VocabularyDao
): androidx.compose.runtime.State<WordDetailUiState> =
    produceState(
        initialValue = WordDetailUiState(
            bundle = CreeDictionaryRepository.WordDetailBundle(
                word = word,
                relatedWords = word.relatedWordIds.mapNotNull { id -> vocabularyWordsById[id.lowercase()] }
            ),
            isLoading = word.needsRemoteSemanticFetch()
        ),
        word.id,
        word.remoteUuid,
        word.relatedWordIds,
        word.relatedSemanticRelationLabels
    ) {
        if (!word.needsRemoteSemanticFetch()) {
            value = WordDetailUiState(value.bundle, isLoading = false)
            return@produceState
        }

        val cachedBundle = withContext(kotlinx.coroutines.Dispatchers.IO) {
            CreeDictionaryRepository.cacheWordDetail(vocabularyDao, word)
        }
        value = WordDetailUiState(cachedBundle ?: value.bundle, isLoading = false)
    }

data class DictionaryStartupSnapshot(
    val cachedWords: List<VocabularyWord>,
    val syncState: DictionarySyncState?,
    val hasFullCache: Boolean
)

suspend fun loadDictionaryStartupSnapshot(context: android.content.Context): DictionaryStartupSnapshot {
    val database = VocabularyDatabase.getInstance(context)
    val vocabularyDao = database.vocabularyDao()
    val syncStateDao = database.syncStateDao()
    val cachedWords = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        vocabularyDao.getAllWords()
    }
    val currentState = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        syncStateDao.getState("entries")
    }
    val hasFullCache = cachedWords.isNotEmpty() &&
        currentState?.isComplete == true &&
        cachedWords.size >= currentState.totalWords
    return DictionaryStartupSnapshot(
        cachedWords = cachedWords,
        syncState = currentState,
        hasFullCache = hasFullCache
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VocabularyApp(startupSnapshot: DictionaryStartupSnapshot? = null) {
    val seedWords = remember { seedVocabularyWords() }
    val context = LocalContext.current
    val database = remember(context) { VocabularyDatabase.getInstance(context) }
    val vocabularyDao = remember(database) { database.vocabularyDao() }
    val syncStateDao = remember(database) { database.syncStateDao() }
    val dbWords by vocabularyDao.observeAllWords().collectAsState(initial = startupSnapshot?.cachedWords ?: emptyList())
    val syncState by syncStateDao.observeState("entries").collectAsState(initial = startupSnapshot?.syncState)
    val searchCache = remember { mutableStateMapOf<SearchCacheKey, List<VocabularyWord>>() }
    val startupCachedWords = startupSnapshot?.takeIf { it.hasFullCache }?.cachedWords
    var bootstrapWords by remember { mutableStateOf(startupCachedWords) }
    var bootstrapComplete by remember { mutableStateOf(startupSnapshot?.hasFullCache == true) }
    var homeQuery by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchSubject by rememberSaveable { mutableStateOf(SubjectFilter.All) }
    var searchWordType by rememberSaveable { mutableStateOf(WordTypeFilter.All) }
    var searchSort by rememberSaveable { mutableStateOf(SortOption.Relevance) }
    var categoryQuery by rememberSaveable { mutableStateOf("") }
    var categoriesResetKey by rememberSaveable { mutableStateOf(0) }
    var recentSearches by rememberSaveable {
        mutableStateOf(listOf("wapos", "mosquito", "rain", "wind", "cloud", "snow", "lake", "fish", "soup", "heart"))
    }
    var primaryLanguage by rememberSaveable { mutableStateOf(DisplayLanguage.Both) }
    var inlineTranslations by rememberSaveable { mutableStateOf(false) }
    var showEntryCounts by rememberSaveable { mutableStateOf(true) }
    var showSemanticRelationLabels by rememberSaveable { mutableStateOf(false) }
    var showMorphology by rememberSaveable { mutableStateOf(false) }
    var wordScopedMorphologyIds by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var wordScopedSemanticLabelIds by rememberSaveable { mutableStateOf(emptyList<String>()) }

    val effectiveInlineTranslations = primaryLanguage == DisplayLanguage.Both || inlineTranslations

    var wordOfDayPages by rememberSaveable { mutableStateOf(emptyList<String>()) }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Screen.Home.route
    LaunchedEffect(startupSnapshot?.hasFullCache) {
        if (startupSnapshot?.hasFullCache == true) return@LaunchedEffect
        val cachedWords = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            vocabularyDao.getAllWords()
        }
        val currentState = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            syncStateDao.getState("entries")
        }
        val hasFullCache = cachedWords.isNotEmpty() &&
            currentState?.isComplete == true &&
            cachedWords.size >= currentState.totalWords
        if (hasFullCache) {
            bootstrapWords = cachedWords
            bootstrapComplete = true
            return@LaunchedEffect
        }

        try {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                CreeDictionaryRepository.syncAllEntriesIntoRoom(vocabularyDao, syncStateDao)
            }
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
        }

        bootstrapWords = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            vocabularyDao.getAllWords().ifEmpty { cachedWords }
        }
        bootstrapComplete = true
    }
    val activeWords = when {
        bootstrapComplete -> dbWords.ifEmpty { bootstrapWords ?: seedWords }
        bootstrapWords != null -> bootstrapWords!!
        dbWords.isNotEmpty() -> dbWords
        else -> emptyList()
    }
    SideEffect {
        VocabularyCatalog.words = activeWords
    }
    val vocabularyWordsById = remember(activeWords) {
        buildMap {
            activeWords.forEach { word ->
                put(word.id.lowercase(), word)
                put(word.cree.lowercase(), word)
                put(word.english.lowercase(), word)
            }
        }
    }
    LaunchedEffect(activeWords, bootstrapComplete) {
        if (!bootstrapComplete) return@LaunchedEffect
        recentSearches = recentSearches.mapNotNull { term ->
            vocabularyWordsById[term.lowercase()]?.id
        }.distinct()
    }
    androidx.compose.runtime.LaunchedEffect(currentRoute, activeWords, bootstrapComplete) {
        if (bootstrapComplete && currentRoute == Screen.Home.route && activeWords.isNotEmpty()) {
            wordOfDayPages = activeWords.shuffled().take(3).map { it.id }
        }
    }
    val wordOfDayWords = remember(wordOfDayPages) {
        wordOfDayPages.mapNotNull { id -> vocabularyWordsById[id.lowercase()] }
            .ifEmpty { activeWords.take(3) }
    }
    val currentSearchKey = SearchCacheKey(searchQuery, searchSubject, searchWordType, searchSort)
    LaunchedEffect(activeWords) {
        searchCache.clear()
    }
    val currentSearchWords by produceState(
        initialValue = searchCache[currentSearchKey] ?: emptyList(),
        currentSearchKey,
        activeWords
    ) {
        searchCache[currentSearchKey]?.let { cached ->
            value = cached
            return@produceState
        }

        val results = withContext(kotlinx.coroutines.Dispatchers.Default) {
            searchWords(activeWords, searchQuery, searchSubject, searchWordType, searchSort)
        }
        searchCache[currentSearchKey] = results
        value = results
    }

    val shouldShowLoading = !bootstrapComplete

    if (shouldShowLoading) {
        DictionaryLoadingScreen(
            syncedWords = syncState?.syncedWords ?: 0,
            totalWords = syncState?.totalWords ?: 0
        )
        return
    }
    val currentBottomDestination = when (currentRoute) {
        Screen.Home.route -> Screen.Home
        Screen.Categories.route -> Screen.Categories
        Screen.Settings.route, Screen.Expert.route -> Screen.Settings
        else -> null
    }
    val currentWordId = backStackEntry?.arguments?.getString("wordId")?.lowercase()
    val activeExpertSettings = remember(
        currentWordId,
        showEntryCounts,
        showMorphology,
        showSemanticRelationLabels,
        wordScopedMorphologyIds,
        wordScopedSemanticLabelIds
    ) {
        buildList {
            if (showEntryCounts) add("Entry Count")
            if (showMorphology || (currentWordId != null && currentWordId in wordScopedMorphologyIds)) {
                add("Show Morphology")
            }
            if (showSemanticRelationLabels || (currentWordId != null && currentWordId in wordScopedSemanticLabelIds)) {
                add("Semantic Relation Labels")
            }
        }
    }
    val navigateToRoute: (String) -> Unit = { route ->
        if (currentRoute != route) {
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    val openSearch: (String) -> Unit = { query ->
        val normalizedQuery = query.trim()
        searchQuery = normalizedQuery
        navigateToRoute(Screen.Search.route)
    }

    val openWord: (String) -> Unit = { wordId ->
        recentSearches = listOf(wordId) + recentSearches.filterNot {
            it.equals(wordId, ignoreCase = true)
        }
        val route = Screen.detailsRoute(wordId)
        val currentWordId = backStackEntry?.arguments?.getString("wordId")
        if (currentRoute != Screen.Details.route || currentWordId != wordId) {
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    val openSemanticMap: (String) -> Unit = { wordId ->
        val route = Screen.semanticMapRoute(wordId)
        val currentWordId = backStackEntry?.arguments?.getString("wordId")
        if (currentRoute != Screen.SemanticMap.route || currentWordId != wordId) {
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    val onRecentClick: (String) -> Unit = { term ->
        val matchedWord = vocabularyWordsById[term.lowercase()]
        if (matchedWord != null) {
            openWord(matchedWord.id)
        } else {
            openSearch(term)
        }
    }

    val openTopLevel: (Screen) -> Unit = { destination ->
        if (destination == Screen.Categories) {
            categoryQuery = ""
            categoriesResetKey += 1
        }
        if (currentRoute != destination.route) {
            navController.navigate(destination.route) {
                launchSingleTop = true
            }
        }
    }

    MyVocabularyTheme(darkTheme = false, dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    Column {
                        ExpertModeIndicatorBar(activeSettings = activeExpertSettings)
                        AppBottomBar(
                            currentDestination = currentBottomDestination,
                            onDestinationSelected = openTopLevel
                        )
                    }
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            query = homeQuery,
                            onQueryChange = { homeQuery = it },
                            onSearch = {
                                val normalizedHomeQuery = homeQuery.trim()
                                if (normalizedHomeQuery.isBlank()) {
                                    searchSubject = SubjectFilter.All
                                    searchWordType = WordTypeFilter.All
                                    searchSort = SortOption.AlphabeticalAZ
                                    searchQuery = ""
                                    openSearch("")
                                } else {
                                    searchSort = SortOption.Relevance
                                    openSearch(normalizedHomeQuery)
                                }
                            },
                            wordOfDayPages = wordOfDayWords,
                            recentSearches = recentSearches,
                            onRecentSearchClick = onRecentClick,
                            onDeleteRecentSearch = { search ->
                                recentSearches = recentSearches.filterNot { it == search }
                            },
                            onSeeAllRecentSearches = {
                                navigateToRoute(Screen.RecentSearches.route)
                            },
                            categories = suggestedCategories,
                            onCategoryClick = { subject ->
                                searchSubject = subject
                                searchWordType = WordTypeFilter.All
                                searchSort = SortOption.AlphabeticalAZ
                                searchQuery = subject.label
                                openSearch(subject.label)
                            },
                            onWordClick = openWord,
                            showEntryCounts = showEntryCounts,
                            primaryLanguage = primaryLanguage,
                            inlineTranslations = effectiveInlineTranslations
                        )
                    }

                    composable(Screen.RecentSearches.route) {
                        RecentSearchesScreen(
                            recentSearches = recentSearches,
                            onRecentSearchClick = onRecentClick,
                            onDeleteSelectedSearches = { selected ->
                                recentSearches = recentSearches.filterNot { it in selected }
                            },
                            onBack = { navController.popBackStack() },
                            primaryLanguage = primaryLanguage,
                            inlineTranslations = effectiveInlineTranslations
                        )
                    }

                    composable(Screen.Categories.route) {
                        CategoriesScreen(
                            resetKey = categoriesResetKey,
                            query = categoryQuery,
                            onQueryChange = { categoryQuery = it },
                            onBackToHome = {
                                navController.popBackStack()
                            },
                            onCategoryClick = { category, selectedWordType, carriedQuery ->
                                searchSubject = category.subject
                                searchWordType = selectedWordType
                                searchSort = if (carriedQuery.isNotBlank()) {
                                    SortOption.Relevance
                                } else {
                                    SortOption.AlphabeticalAZ
                                }
                                searchQuery = carriedQuery
                                openSearch(carriedQuery)
                            },
                            showEntryCounts = showEntryCounts,
                            primaryLanguage = primaryLanguage,
                            inlineTranslations = effectiveInlineTranslations
                        )
                    }

                    composable(Screen.Search.route) {
                        SearchResultsScreen(
                            query = searchQuery,
                            filteredWords = currentSearchWords,
                            subjectFilter = searchSubject,
                            wordTypeFilter = searchWordType,
                            sortOption = searchSort,
                            inlineTranslations = effectiveInlineTranslations,
                            primaryLanguage = primaryLanguage,
                            onQueryChange = { searchQuery = it },
                            onSubjectChange = { searchSubject = it },
                            onWordTypeChange = { searchWordType = it },
                            onSortChange = { searchSort = it },
                            onResetFilters = {
                                searchSubject = SubjectFilter.All
                                searchWordType = WordTypeFilter.All
                                searchSort = SortOption.Relevance
                            },
                            onBack = { navController.popBackStack() },
                            onWordClick = openWord,
                            onSubmitSearch = { query -> openSearch(query) }
                        )
                    }

                    composable(
                        route = "details/{wordId}",
                        arguments = listOf(navArgument("wordId") { type = NavType.StringType })
                    ) { entry ->
                        val wordId = entry.arguments?.getString("wordId")
                        val baseWord = wordId?.let { vocabularyWordsById[it.lowercase()] } ?: vocabularyWords.first()
                        val detailBundleState = rememberWordDetailState(
                            word = baseWord,
                            vocabularyWordsById = vocabularyWordsById,
                            vocabularyDao = vocabularyDao
                        )
                        val detailState = detailBundleState.value
                        val normalizedWordId = detailState.bundle.word.id.lowercase()
                        val localMorphologyEnabled = normalizedWordId in wordScopedMorphologyIds
                        val localSemanticLabelsEnabled = normalizedWordId in wordScopedSemanticLabelIds

                        WordDetailsScreen(
                            word = detailState.bundle.word,
                            relatedWords = detailState.bundle.relatedWords,
                            showMorphology = showMorphology || localMorphologyEnabled,
                            primaryLanguage = primaryLanguage,
                            inlineTranslations = effectiveInlineTranslations,
                            isMorphologyManagedGlobally = showMorphology,
                            onShowMorphologyChange = { enabled ->
                                if (!showMorphology) {
                                    wordScopedMorphologyIds = if (enabled) {
                                        (wordScopedMorphologyIds + normalizedWordId).distinct()
                                    } else {
                                        wordScopedMorphologyIds.filterNot { it == normalizedWordId }
                                    }
                                }
                            },
                            onBack = { navController.popBackStack() },
                            onWordClick = openWord,
                            onConnectionsClick = {
                                openSemanticMap(detailState.bundle.word.id)
                            }
                        )
                    }

                    composable(
                        route = Screen.SemanticMap.route,
                        arguments = listOf(navArgument("wordId") { type = NavType.StringType })
                    ) { entry ->
                        val wordId = entry.arguments?.getString("wordId")
                        val baseWord = wordId?.let { vocabularyWordsById[it.lowercase()] } ?: vocabularyWords.first()
                        val detailBundleState = rememberWordDetailState(
                            word = baseWord,
                            vocabularyWordsById = vocabularyWordsById,
                            vocabularyDao = vocabularyDao
                        )
                        val detailState = detailBundleState.value
                        val normalizedWordId = detailState.bundle.word.id.lowercase()

                        SemanticMapScreen(
                            word = detailState.bundle.word,
                            relatedWords = detailState.bundle.relatedWords,
                            showSemanticRelationLabels = showSemanticRelationLabels || normalizedWordId in wordScopedSemanticLabelIds,
                            primaryLanguage = primaryLanguage,
                            inlineTranslations = effectiveInlineTranslations,
                            isSemanticRelationLabelsManagedGlobally = showSemanticRelationLabels,
                            onShowSemanticRelationLabelsChange = { enabled ->
                                if (!showSemanticRelationLabels) {
                                    wordScopedSemanticLabelIds = if (enabled) {
                                        (wordScopedSemanticLabelIds + normalizedWordId).distinct()
                                    } else {
                                        wordScopedSemanticLabelIds.filterNot { it == normalizedWordId }
                                    }
                                }
                            },
                            onBack = { navController.popBackStack() },
                            onWordClick = openWord
                        )
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            primaryLanguage = primaryLanguage,
                            onPrimaryLanguageChange = { newLang ->
                                primaryLanguage = newLang
                            },
                            inlineTranslations = inlineTranslations,
                            onInlineTranslationsChange = { inlineTranslations = it },
                            onOpenExpertMode = {
                                navigateToRoute(Screen.Expert.route)
                            },
                            onSeeRecentSearches = {
                                navigateToRoute(Screen.RecentSearches.route)
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Expert.route) {
                        ExpertModeScreen(
                            showSemanticRelationLabels = showSemanticRelationLabels,
                            onShowSemanticRelationLabelsChange = { showSemanticRelationLabels = it },
                            showMorphology = showMorphology,
                            onShowMorphologyChange = { showMorphology = it },
                            showEntryCounts = showEntryCounts,
                            onShowEntryCountsChange = { showEntryCounts = it },
                            primaryLanguage = primaryLanguage,
                            inlineTranslations = effectiveInlineTranslations,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpertModeIndicatorBar(activeSettings: List<String>) {
    if (activeSettings.isEmpty()) return

    val indicatorBrush = Brush.horizontalGradient(
        colors = listOf(
            AccentDark.copy(alpha = 0.96f),
            Accent.copy(alpha = 0.92f),
            Accent.copy(alpha = 0.78f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(indicatorBrush)
            .padding(PaddingValues(horizontal = 16.dp, vertical = 6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "You are using Expert Mode (${activeSettings.joinToString(", ")})",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.surface,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AppBottomBar(
    currentDestination: Screen?,
    onDestinationSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentDestination == item.screen,
                onClick = { onDestinationSelected(item.screen) },
                icon = { Icon(item.icon, contentDescription = item.screen.label) },
                label = { Text(item.screen.label, maxLines = 1) }
            )
        }
    }
}

private data class BottomNavItem(
    val screen: Screen,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Filled.Home),
    BottomNavItem(Screen.Categories, Icons.Filled.Category),
    BottomNavItem(Screen.Settings, Icons.Filled.Settings)
)

private fun mergeVocabularyWords(
    existing: List<VocabularyWord>,
    additions: List<VocabularyWord>
): List<VocabularyWord> {
    val merged = linkedMapOf<String, VocabularyWord>()
    existing.forEach { merged[it.id] = it }
    additions.forEach { merged[it.id] = it }
    return merged.values.toList()
}
