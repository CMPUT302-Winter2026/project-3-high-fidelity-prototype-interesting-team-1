package com.example.myvocabulary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myvocabulary.ui.theme.MyVocabularyTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VocabularyApp() {
    var homeQuery by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchSubject by rememberSaveable { mutableStateOf(SubjectFilter.All) }
    var searchWordType by rememberSaveable { mutableStateOf(WordTypeFilter.All) }
    var searchSort by rememberSaveable { mutableStateOf(SortOption.Relevance) }
    var categoryQuery by rememberSaveable { mutableStateOf("") }
    var categorySubject by rememberSaveable { mutableStateOf(SubjectFilter.All) }
    var categoryWordType by rememberSaveable { mutableStateOf(WordTypeFilter.All) }
    var categorySort by rememberSaveable { mutableStateOf(SortOption.Relevance) }
    var recentSearches by rememberSaveable {
        mutableStateOf(listOf("Wâpos", "Mosquito", "Rain", "Wind", "Cloud", "Snow", "Lake", "Fish", "Soup", "Heart"))
    }
    var primaryLanguage by rememberSaveable { mutableStateOf(DisplayLanguage.Both) }
    var inlineTranslations by rememberSaveable { mutableStateOf(false) }
    var showEntryCounts by rememberSaveable { mutableStateOf(true) }
    var showSemanticLabels by rememberSaveable { mutableStateOf(true) }
    var showMorphology by rememberSaveable { mutableStateOf(true) }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Screen.Home.route
    val currentBottomDestination = when {
        currentRoute == Screen.Categories.route -> Screen.Categories
        currentRoute == Screen.Settings.route -> Screen.Settings
        currentRoute == Screen.Expert.route -> Screen.Settings
        currentRoute.startsWith("semantic-map/") -> Screen.Settings
        else -> Screen.Home
    }

    val openSearch: (String, Boolean) -> Unit = { query, rememberRecent ->
        val normalizedQuery = query.trim()
        searchQuery = normalizedQuery
        if (rememberRecent && normalizedQuery.isNotBlank()) {
            recentSearches = listOf(normalizedQuery) + recentSearches.filterNot {
                it.equals(normalizedQuery, ignoreCase = true)
            }
        }
        navController.navigate(Screen.Search.route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
        }
    }

    val openWord: (String) -> Unit = { wordId ->
        navController.navigate(Screen.detailsRoute(wordId))
    }

    val onRecentClick: (String) -> Unit = { term ->
        val matchedWord = vocabularyWords.firstOrNull {
            it.cree.equals(term, ignoreCase = true) ||
            it.english.equals(term, ignoreCase = true) ||
            it.id.equals(term, ignoreCase = true)
        }
        if (matchedWord != null) {
            openWord(matchedWord.id)
        } else {
            openSearch(term, false)
        }
    }

    val openTopLevel: (Screen) -> Unit = { destination ->
        val popped = navController.popBackStack(destination.route, inclusive = false)
        if (!popped) {
            navController.navigate(destination.route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
            }
        }
    }

    MyVocabularyTheme(darkTheme = false, dynamicColor = false) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                AppBottomBar(
                    currentDestination = currentBottomDestination,
                    onDestinationSelected = openTopLevel
                )
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
                            openSearch(homeQuery.ifBlank { vocabularyWords.first().cree }, true)
                        },
                        wordOfDayPages = wordOfDayIds.mapNotNull { id ->
                            vocabularyWords.firstOrNull { it.id == id }
                        },
                        recentSearches = recentSearches,
                        onRecentSearchClick = onRecentClick,
                        onDeleteRecentSearch = { search ->
                            recentSearches = recentSearches.filterNot { it == search }
                        },
                        onSeeAllRecentSearches = {
                            navController.navigate(Screen.RecentSearches.route)
                        },
                        categories = suggestedCategories,
                        onCategoryClick = { subject ->
                            searchSubject = subject
                            searchWordType = WordTypeFilter.All
                            searchSort = SortOption.Relevance
                            searchQuery = subject.label
                            openTopLevel(Screen.Search)
                        },
                        onWordClick = openWord,
                        showEntryCounts = showEntryCounts
                    )
                }

                composable(Screen.RecentSearches.route) {
                    RecentSearchesScreen(
                        recentSearches = recentSearches,
                        onRecentSearchClick = onRecentClick,
                        onDeleteSelectedSearches = { selected ->
                            recentSearches = recentSearches.filterNot { it in selected }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Categories.route) {
                    CategoriesScreen(
                        query = categoryQuery,
                        subjectFilter = categorySubject,
                        wordTypeFilter = categoryWordType,
                        sortOption = categorySort,
                        onQueryChange = { categoryQuery = it },
                        onSubjectChange = { categorySubject = it },
                        onWordTypeChange = { categoryWordType = it },
                        onSortChange = { categorySort = it },
                        onResetFilters = {
                            categoryQuery = ""
                            categorySubject = SubjectFilter.All
                            categoryWordType = WordTypeFilter.All
                            categorySort = SortOption.Relevance
                        },
                        onBackToHome = {
                            navController.popBackStack(Screen.Home.route, inclusive = false)
                        },
                        onCategoryClick = { category ->
                            searchSubject = category.subject
                            searchWordType = WordTypeFilter.All
                            searchSort = SortOption.Relevance
                            searchQuery = category.title
                            openTopLevel(Screen.Search)
                        },
                        showEntryCounts = showEntryCounts
                    )
                }

                composable(Screen.Search.route) {
                    SearchResultsScreen(
                        query = searchQuery,
                        subjectFilter = searchSubject,
                        wordTypeFilter = searchWordType,
                        sortOption = searchSort,
                        inlineTranslations = inlineTranslations,
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
                        onSubmitSearch = { query -> openSearch(query, true) }
                    )
                }

                composable(
                    route = "details/{wordId}",
                    arguments = listOf(navArgument("wordId") { type = NavType.StringType })
                ) { entry ->
                    val wordId = entry.arguments?.getString("wordId")
                    val word = vocabularyWords.firstOrNull { it.id == wordId } ?: vocabularyWords.first()

                    WordDetailsScreen(
                        word = word,
                        relatedWords = word.relatedWordIds.mapNotNull { id ->
                            vocabularyWords.firstOrNull { it.id == id }
                        },
                        showMorphology = showMorphology,
                        onBack = { navController.popBackStack() },
                        onWordClick = openWord,
                        onConnectionsClick = {
                            navController.navigate(Screen.semanticMapRoute(word.id))
                        }
                    )
                }

                composable(
                    route = Screen.SemanticMap.route,
                    arguments = listOf(navArgument("wordId") { type = NavType.StringType })
                ) { entry ->
                    val wordId = entry.arguments?.getString("wordId")
                    val word = vocabularyWords.firstOrNull { it.id == wordId } ?: vocabularyWords.first()

                    SemanticMapScreen(
                        word = word,
                        relatedWords = word.relatedWordIds.mapNotNull { id ->
                            vocabularyWords.firstOrNull { it.id == id }
                        },
                        showFullSemanticMap = showSemanticLabels,
                        onShowFullSemanticMapChange = { showSemanticLabels = it },
                        onBack = { navController.popBackStack() },
                        onWordClick = openWord
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        primaryLanguage = primaryLanguage,
                        onPrimaryLanguageClick = {
                            primaryLanguage = nextCycleValue(primaryLanguage, DisplayLanguage.cycleOrder) as DisplayLanguage
                        },
                        inlineTranslations = inlineTranslations,
                        onInlineTranslationsChange = { inlineTranslations = it },
                        onOpenExpertMode = {
                            navController.navigate(Screen.Expert.route)
                        },
                        onSeeRecentSearches = {
                            navController.navigate(Screen.RecentSearches.route)
                        }
                    )
                }

                composable(Screen.Expert.route) {
                    ExpertModeScreen(
                        showSemanticLabels = showSemanticLabels,
                        onShowSemanticLabelsChange = { showSemanticLabels = it },
                        showMorphology = showMorphology,
                        onShowMorphologyChange = { showMorphology = it },
                        showEntryCounts = showEntryCounts,
                        onShowEntryCountsChange = { showEntryCounts = it },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppBottomBar(
    currentDestination: Screen,
    onDestinationSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        windowInsets = WindowInsets.safeDrawing
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
