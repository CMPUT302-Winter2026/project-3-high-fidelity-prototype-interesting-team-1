package com.example.myvocabulary

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.myvocabulary.ui.theme.Accent
import com.example.myvocabulary.ui.theme.AccentDark

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    wordOfDayPages: List<VocabularyWord>,
    recentSearches: List<String>,
    onRecentSearchClick: (String) -> Unit,
    onDeleteRecentSearch: (String) -> Unit,
    onSeeAllRecentSearches: () -> Unit,
    categories: List<CategoryCard>,
    onCategoryClick: (SubjectFilter) -> Unit,
    onWordClick: (String) -> Unit,
    showEntryCounts: Boolean
) {
    val pagerState = rememberPagerState(pageCount = { wordOfDayPages.size })
    var searchToDelete by remember { mutableStateOf<String?>(null) }

    if (searchToDelete != null) {
        AlertDialog(
            onDismissRequest = { searchToDelete = null },
            title = { Text("Remove from history?") },
            text = { Text("Do you want to remove '${searchToDelete}' from your search history?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        searchToDelete?.let { onDeleteRecentSearch(it) }
                        searchToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            textContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    VocabularyScreenSurface {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Vocabulary Explorer",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SearchField(
                        value = query,
                        onValueChange = onQueryChange,
                        placeholder = "Search Cree or English",
                        onSearch = onSearch
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    SectionTitle("Word of the Day")
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) { page ->
                        WordOfDayCard(
                            word = wordOfDayPages[page],
                            onClick = { onWordClick(wordOfDayPages[page].id) }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(wordOfDayPages.size) { index ->
                            val selected = index == pagerState.currentPage
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp, vertical = 10.dp)
                                    .size(if (selected) 10.dp else 6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    )
                            )
                        }
                    }
                }
            }
            item {
                SectionTitle("Suggested Categories")
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    categories.forEach { category ->
                        CategoryGridCard(
                            category = category,
                            modifier = Modifier.width(150.dp).height(150.dp),
                            showDescription = false,
                            showEntryCount = showEntryCounts,
                            onClick = { onCategoryClick(category.subject) }
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                SectionTitle("Recent Searches")
                Spacer(modifier = Modifier.height(16.dp))
                if (recentSearches.isEmpty()) {
                    Text(
                        text = "No recent searches yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            recentSearches.take(10).forEach { term ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                    modifier = Modifier.combinedClickable(
                                        onClick = { onRecentSearchClick(term) },
                                        onLongClick = { searchToDelete = term }
                                    )
                                ) {
                                    Text(
                                        text = term,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Hold a word to remove it.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onSeeAllRecentSearches, modifier = Modifier.fillMaxWidth()) {
                    Text("See All")
                }
            }
            item {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
fun RecentSearchesScreen(
    recentSearches: List<String>,
    onRecentSearchClick: (String) -> Unit,
    onDeleteSelectedSearches: (List<String>) -> Unit,
    onBack: () -> Unit
) {
    var selectedSearches by remember { mutableStateOf(setOf<String>()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete selected?") },
            text = { Text("Are you sure you want to remove ${selectedSearches.size} selected items from your history?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteSelectedSearches(selectedSearches.toList())
                        selectedSearches = emptySet()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    VocabularyScreenSurface {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            ScreenHeader(title = "Recent Searches", onBack = onBack)
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        if (selectedSearches.size == recentSearches.size) {
                            selectedSearches = emptySet()
                        } else {
                            selectedSearches = recentSearches.toSet()
                        }
                    }
                ) {
                    Text(if (selectedSearches.size == recentSearches.size) "Deselect All" else "Select All")
                }
                
                IconButton(
                    onClick = { if (selectedSearches.isNotEmpty()) showDeleteConfirmation = true },
                    enabled = selectedSearches.isNotEmpty()
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete Selected",
                        tint = if (selectedSearches.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(recentSearches) { search ->
                    Surface(
                        onClick = { onRecentSearchClick(search) },
                        modifier = Modifier.fillMaxWidth(),
                        color = androidx.compose.ui.graphics.Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = search in selectedSearches,
                                onCheckedChange = { checked ->
                                    selectedSearches = if (checked) {
                                        selectedSearches + search
                                    } else {
                                        selectedSearches - search
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = search,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun SearchResultsScreen(
    query: String,
    subjectFilter: SubjectFilter,
    wordTypeFilter: WordTypeFilter,
    sortOption: SortOption,
    inlineTranslations: Boolean,
    onQueryChange: (String) -> Unit,
    onSubjectChange: (SubjectFilter) -> Unit,
    onWordTypeChange: (WordTypeFilter) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onResetFilters: () -> Unit,
    onBack: () -> Unit,
    onWordClick: (String) -> Unit,
    onSubmitSearch: (String) -> Unit
) {
    val filteredWords = remember(query, subjectFilter, wordTypeFilter, sortOption) {
        searchWords(vocabularyWords, query, subjectFilter, wordTypeFilter, sortOption)
    }

    VocabularyScreenSurface {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ScreenHeader(title = "Search Results", onBack = onBack) }
            item {
                SectionLabel("Search Vocabulary")
                SearchField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = "Search for a word",
                    onSearch = { onSubmitSearch(query) }
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Query: ${query.ifBlank { "All words" }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                SectionLabel("Results Count")
                Text(
                    text = "${filteredWords.size} results found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                SectionLabel("Filters")
                FilterRow(
                    subject = subjectFilter,
                    wordType = wordTypeFilter,
                    sort = sortOption,
                    onSubjectChange = onSubjectChange,
                    onWordTypeChange = onWordTypeChange,
                    onSortChange = onSortChange,
                    onReset = onResetFilters
                )
            }
            item {
                SectionTitle("Cree Words")
                Text(
                    text = "Explore the Vocabulary",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (filteredWords.isEmpty()) {
                item {
                    EmptyState(
                        title = "No matches found",
                        body = "Try a different search term or clear the filters."
                    )
                }
            } else {
                items(filteredWords, key = { it.id }) { word ->
                    SearchResultItem(
                        word = word,
                        showInlineTranslation = inlineTranslations,
                        onClick = { onWordClick(word.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriesScreen(
    query: String,
    subjectFilter: SubjectFilter,
    wordTypeFilter: WordTypeFilter,
    sortOption: SortOption,
    onQueryChange: (String) -> Unit,
    onSubjectChange: (SubjectFilter) -> Unit,
    onWordTypeChange: (WordTypeFilter) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onResetFilters: () -> Unit,
    onBackToHome: () -> Unit,
    onCategoryClick: (CategoryCard) -> Unit,
    showEntryCounts: Boolean
) {
    val normalizedQuery = query.trim().lowercase()
    val queryTokens = normalizedQuery.split(Regex("\\s+")).filter { it.isNotBlank() }
    val categoryResults = remember(query) {
        suggestedCategories.filter {
            normalizedQuery.isBlank() ||
                it.title.contains(normalizedQuery, ignoreCase = true) ||
                it.description.contains(normalizedQuery, ignoreCase = true) ||
                queryTokens.all { token ->
                    it.title.contains(token, ignoreCase = true) ||
                        it.description.contains(token, ignoreCase = true)
                }
        }
    }

    BackHandler(onBack = onBackToHome)

    VocabularyScreenSurface {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { ScreenHeader(title = "Categories", onBack = onBackToHome) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionLabel("Search")
                    SearchField(
                        value = query,
                        onValueChange = onQueryChange,
                        placeholder = "Search categories or words",
                        onSearch = { }
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionLabel("Filters")
                    FilterRow(
                        subject = subjectFilter,
                        wordType = wordTypeFilter,
                        sort = sortOption,
                        onSubjectChange = onSubjectChange,
                        onWordTypeChange = onWordTypeChange,
                        onSortChange = onSortChange,
                        onReset = onResetFilters
                    )
                }
            }
            item { SectionTitle("Categories") }
            items(categoryResults.chunked(3), key = { row -> row.first().title }) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { category ->
                        CategoryGridCard(
                            category = category,
                            modifier = Modifier
                                .width(180.dp)
                                .height(220.dp),
                            showEntryCount = showEntryCounts,
                            onClick = { onCategoryClick(category) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WordDetailsScreen(
    word: VocabularyWord,
    relatedWords: List<VocabularyWord>,
    showMorphology: Boolean,
    onBack: () -> Unit,
    onWordClick: (String) -> Unit,
    onConnectionsClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var playbackMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(playbackMessage) {
        playbackMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            playbackMessage = null
        }
    }

    VocabularyScreenSurface {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ScreenHeader(title = "Word Details", onBack = onBack) }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = word.cree,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = word.english,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item {
                SectionTitle("Pronunciation")
                ActionRow(
                    icon = Icons.Filled.VolumeUp,
                    label = word.pronunciationLabel,
                    onClick = { playbackMessage = "Playing audio for ${word.cree}" }
                )
            }
            item {
                SectionTitle("Part of Speech")
                ActionRow(
                    icon = Icons.Filled.MenuBook,
                    label = word.partOfSpeech.replaceFirstChar { it.uppercase() }
                )
            }
            item {
                SectionTitle("Example")
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = word.exampleTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Highlight: ${word.cree.lowercase()}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = word.exampleSentence,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (showMorphology) {
                item {
                    MorphologyPreviewCard(
                        word = word,
                        modifier = Modifier.padding(start = 12.dp, top = 8.dp, end = 12.dp)
                    )
                }
            }
            item {
                SectionTitle("Related Words")
                relatedWords.forEach { relatedWord ->
                    RelatedWordItem(word = relatedWord, onClick = { onWordClick(relatedWord.id) })
                }
            }
            item {
                Button(onClick = onConnectionsClick, modifier = Modifier.fillMaxWidth()) {
                    Text("View Word Connections")
                }
            }
        }
    }
}

@Composable
fun SemanticMapScreen(
    word: VocabularyWord,
    relatedWords: List<VocabularyWord>,
    showFullSemanticMap: Boolean,
    onShowFullSemanticMapChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onWordClick: (String) -> Unit
) {
    VocabularyScreenSurface {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ScreenHeader(title = "Semantic Map", onBack = onBack) }
            item {
                WordSemanticMapCard(
                    word = word,
                    relatedWords = relatedWords,
                    showEnglishMeanings = showFullSemanticMap,
                    onWordClick = onWordClick
                )
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View Full Semantic Map",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = showFullSemanticMap,
                            onCheckedChange = onShowFullSemanticMapChange,
                            colors = appSwitchColors()
                        )
                    }
                }
            }
            item {
                Text(
                    text = "Tap any node to open that word.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    primaryLanguage: DisplayLanguage,
    onPrimaryLanguageClick: () -> Unit,
    inlineTranslations: Boolean,
    onInlineTranslationsChange: (Boolean) -> Unit,
    onOpenExpertMode: () -> Unit,
    onSeeRecentSearches: () -> Unit
) {
    VocabularyScreenSurface {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ScreenHeader(title = "Settings") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsRow(
                        title = "Expert Mode",
                        subtitle = "Advanced language tools",
                        onClick = onOpenExpertMode,
                        trailing = { Text("›", style = MaterialTheme.typography.headlineSmall) }
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("General Settings")
                    SettingsRow(
                        title = "Primary Language",
                        subtitle = "Choose your primary language for vocabulary.",
                        trailing = {
                            CycleChip(
                                label = "Primary Language",
                                value = primaryLanguage.label,
                                onClick = onPrimaryLanguageClick
                            )
                        }
                    )
                    SettingsRow(
                        title = "Inline Translations",
                        subtitle = "Show translations inline next to vocabulary.",
                        trailing = {
                            Switch(
                                checked = inlineTranslations,
                                onCheckedChange = onInlineTranslationsChange,
                                colors = appSwitchColors()
                            )
                        }
                    )
                    SettingsRow(
                        title = "Recent Searches",
                        subtitle = "View and manage your search history",
                        onClick = onSeeRecentSearches,
                        trailing = { Text("›", style = MaterialTheme.typography.headlineSmall) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExpertModeScreen(
    showSemanticLabels: Boolean,
    onShowSemanticLabelsChange: (Boolean) -> Unit,
    showMorphology: Boolean,
    onShowMorphologyChange: (Boolean) -> Unit,
    showEntryCounts: Boolean,
    onShowEntryCountsChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val previewWord = remember { vocabularyWords.firstOrNull { it.id == "wapos" } ?: vocabularyWords.first() }

    VocabularyScreenSurface {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ScreenHeader(title = "Expert Mode", onBack = onBack) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle("Advanced Tools")
                    Text(
                        text = "Advanced language tools",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsRow(
                        title = "Show Entry Counts",
                        subtitle = "Display number of vocabulary entries.",
                        trailing = {
                            Switch(
                                checked = showEntryCounts,
                                onCheckedChange = onShowEntryCountsChange,
                                colors = appSwitchColors()
                            )
                        }
                    )
                    SettingsRow(
                        title = "Show Morphology",
                        subtitle = "Display the morphology preview card.",
                        trailing = {
                            Switch(
                                checked = showMorphology,
                                onCheckedChange = onShowMorphologyChange,
                                colors = appSwitchColors()
                            )
                        }
                    )
                    MorphologyPreviewCard(word = previewWord, modifier = Modifier.padding(start = 16.dp))
                    
                    SettingsRow(
                        title = "Full Semantic Map",
                        subtitle = "Show English meanings below map nodes.",
                        trailing = {
                            Switch(
                                checked = showSemanticLabels,
                                onCheckedChange = onShowSemanticLabelsChange,
                                colors = appSwitchColors()
                            )
                        }
                    )
                    SemanticMapPreviewCard(showSemanticLabels = showSemanticLabels, modifier = Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

@Composable
fun VocabularyScreenSurface(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            androidx.compose.ui.graphics.Color.Transparent
                        ),
                        center = Offset(120f, 140f),
                        radius = 1100f
                    )
                )
        )
        content()
    }
}

@Composable
fun ScreenHeader(title: String, onBack: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.primary
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
        )
    }
}

@Composable
fun SectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onSearch: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Accent,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            cursorColor = Accent
        )
    )
}

@Composable
fun WordOfDayCard(word: VocabularyWord, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Column {
                Text(
                    text = word.cree,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = word.english,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CategoryListItem(category: CategoryCard, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(shape = RoundedCornerShape(999.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(
                    text = category.icon.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SearchResultItem(word: VocabularyWord, showInlineTranslation: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(999.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(
                    text = word.icon.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.cree,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (showInlineTranslation) {
                    Text(
                        text = word.english.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = word.partOfSpeech,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CategoryGridCard(
    category: CategoryCard,
    modifier: Modifier = Modifier,
    showDescription: Boolean = true,
    showEntryCount: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            if (showEntryCount) {
                Text(
                    text = category.count.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.icon.uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (showDescription) {
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RelatedWordItem(word: VocabularyWord, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(999.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(
                    text = word.icon.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.cree,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = word.english,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FilterRow(
    subject: SubjectFilter,
    wordType: WordTypeFilter,
    sort: SortOption,
    onSubjectChange: (SubjectFilter) -> Unit,
    onWordTypeChange: (WordTypeFilter) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onReset: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DropdownFilterField(
                label = "Subject",
                selectedValue = subject.label,
                options = SubjectFilter.entries,
                optionLabel = { it.label },
                onSelected = onSubjectChange,
                modifier = Modifier.width(180.dp)
            )
            DropdownFilterField(
                label = "Word Type",
                selectedValue = wordType.label,
                options = WordTypeFilter.entries,
                optionLabel = { it.label },
                onSelected = onWordTypeChange,
                modifier = Modifier.width(180.dp)
            )
            DropdownFilterField(
                label = "Sort",
                selectedValue = sort.label,
                options = SortOption.entries,
                optionLabel = { it.label },
                onSelected = onSortChange,
                modifier = Modifier.width(180.dp)
            )
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .width(150.dp)
                    .height(56.dp)
            ) {
                Text("Reset filters")
            }
        }
    }
}

@Composable
fun CycleChip(label: String, value: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = if (value.isBlank()) label else "$label: $value",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}

@Composable
private fun <T> DropdownFilterField(
    label: String,
    selectedValue: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedValue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.72f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            trailing?.invoke()
        }
    }
}

@Composable
private fun appSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
    checkedTrackColor = MaterialTheme.colorScheme.primary,
    checkedBorderColor = MaterialTheme.colorScheme.primary,
    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
    uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant,
    uncheckedBorderColor = MaterialTheme.colorScheme.outline,
    disabledCheckedThumbColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
    disabledCheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
    disabledCheckedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
    disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f),
    disabledUncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
    disabledUncheckedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.42f)
)

@Composable
fun MorphologyPreviewCard(word: VocabularyWord? = null, modifier: Modifier = Modifier) {
    val morphologyText = word?.morphology?.ifBlank { morphologyForWord(word) }
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Morphology Preview",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = word?.cree ?: "Morphology Preview",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (word != null) {
                Text(
                    text = word.english,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = morphologyText ?: "Tap a word to view its morphology.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (word != null) {
                        MorphologyRow(label = "word:", value = word.cree, description = word.english)
                        MorphologyRow(label = "analysis:", value = morphologyText.orEmpty(), description = "")
                    } else {
                        MorphologyRow(label = "tip:", value = "Tap any word", description = "to inspect its morphology.")
                        MorphologyRow(label = "mode:", value = "Expert setting", description = "controls whether this preview appears.")
                    }
                }
            }
        }
    }
}

@Composable
fun MorphologyRow(label: String, value: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(110.dp)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SemanticMapPreviewCard(
    showSemanticLabels: Boolean = false,
    modifier: Modifier = Modifier,
    onWordClick: (String) -> Unit = {}
) {
    val previewWord = vocabularyWords.firstOrNull { it.id == "wapos" } ?: vocabularyWords.first()
    val relatedWords = previewWord.relatedWordIds.mapNotNull { id ->
        vocabularyWords.firstOrNull { it.id == id }
    }
    Box(modifier = modifier) {
        WordSemanticMapCard(
            word = previewWord,
            relatedWords = relatedWords,
            showEnglishMeanings = showSemanticLabels,
            title = "Semantic Map Preview",
            onWordClick = onWordClick
        )
    }
}

@Composable
fun WordSemanticMapCard(
    word: VocabularyWord,
    relatedWords: List<VocabularyWord>,
    showEnglishMeanings: Boolean,
    title: String = "Semantic Map",
    onWordClick: (String) -> Unit
) {
    val centerAccent = Accent
    val hubSize = androidx.compose.ui.unit.DpSize(160.dp, 68.dp)
    val relationSize = androidx.compose.ui.unit.DpSize(150.dp, 72.dp)
    val hubPosition = SemanticMapNodePosition(200.dp, 112.dp)
    val mapSize = androidx.compose.ui.unit.DpSize(560.dp, 360.dp)
    val minZoom = 0.6f
    val maxZoom = 2.2f
    var zoom by remember { mutableStateOf(0.82f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        zoom = (zoom * zoomChange).coerceIn(minZoom, maxZoom)
        offset += panChange
    }
    val relations = remember(word.id, relatedWords) {
        relatedWords.mapIndexed { index, relatedWord ->
            SemanticRelationNode(
                word = relatedWord,
                position = semanticMapPosition(index, relatedWords.size),
                accent = semanticMapAccent(index)
            )
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = word.cree,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = word.english,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tap a node to open that exact word.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zoom: ${(zoom * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { zoom = (zoom * 0.85f).coerceAtLeast(minZoom) }) {
                        Icon(Icons.Filled.Remove, contentDescription = "Zoom out")
                    }
                    IconButton(onClick = { zoom = (zoom * 1.15f).coerceAtMost(maxZoom) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Zoom in")
                    }
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(mapSize.width, mapSize.height)
                            .padding(18.dp)
                            .transformable(transformState)
                            .graphicsLayer(
                                scaleX = zoom,
                                scaleY = zoom,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    ) {
                        SemanticConnectionLayer(
                            hubPosition = hubPosition,
                            hubSize = hubSize,
                            relations = relations,
                            relationSize = relationSize
                        )
                        SemanticHub(
                            label = word.cree,
                            subtitle = word.english,
                            showSubtitle = showEnglishMeanings,
                            modifier = Modifier
                                .offset(x = hubPosition.x, y = hubPosition.y)
                                .size(hubSize.width, hubSize.height),
                            accent = centerAccent
                        )
                        relations.forEach { relation ->
                            SemanticRelationCard(
                                word = relation.word,
                                accent = relation.accent,
                                showEnglishMeanings = showEnglishMeanings,
                                modifier = Modifier
                                    .offset(x = relation.position.x, y = relation.position.y)
                                    .size(relationSize.width, relationSize.height),
                                onClick = { onWordClick(relation.word.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SemanticHub(
    label: String,
    subtitle: String,
    showSubtitle: Boolean,
    modifier: Modifier = Modifier,
    accent: androidx.compose.ui.graphics.Color
) {
    Surface(
        color = accent.copy(alpha = 0.18f),
        shape = RoundedCornerShape(999.dp),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = accent
            )
            if (showSubtitle) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SemanticRelationCard(
    word: VocabularyWord,
    accent: androidx.compose.ui.graphics.Color,
    showEnglishMeanings: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 0.5.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = accent.copy(alpha = 0.16f)
            ) {
                Text(
                    text = "->",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = accent
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = word.cree,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (showEnglishMeanings) {
                    Text(
                        text = word.english,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private data class SemanticRelationNode(
    val word: VocabularyWord,
    val position: SemanticMapNodePosition,
    val accent: androidx.compose.ui.graphics.Color
)

private data class SemanticMapNodePosition(
    val x: androidx.compose.ui.unit.Dp,
    val y: androidx.compose.ui.unit.Dp
)

private val semanticMapAccentPalette = listOf(
    AccentDark,
    androidx.compose.ui.graphics.Color(0xFFD97706),
    androidx.compose.ui.graphics.Color(0xFF7C3AED),
    androidx.compose.ui.graphics.Color(0xFF0EA5E9),
    androidx.compose.ui.graphics.Color(0xFFDB2777),
    androidx.compose.ui.graphics.Color(0xFF2563EB)
)

private fun semanticMapPosition(index: Int, total: Int): SemanticMapNodePosition {
    val positions = listOf(
        SemanticMapNodePosition(44.dp, 24.dp),
        SemanticMapNodePosition(318.dp, 24.dp),
        SemanticMapNodePosition(16.dp, 110.dp),
        SemanticMapNodePosition(338.dp, 112.dp),
        SemanticMapNodePosition(52.dp, 190.dp),
        SemanticMapNodePosition(288.dp, 190.dp)
    )
    return positions[index % positions.size]
}

private fun semanticMapAccent(index: Int): androidx.compose.ui.graphics.Color {
    return semanticMapAccentPalette[index % semanticMapAccentPalette.size]
}

@Composable
private fun SemanticConnectionLayer(
    hubPosition: SemanticMapNodePosition,
    hubSize: androidx.compose.ui.unit.DpSize,
    relations: List<SemanticRelationNode>,
    relationSize: androidx.compose.ui.unit.DpSize
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val hubCenter = Offset(
            x = hubPosition.x.toPx() + hubSize.width.toPx() / 2f,
            y = hubPosition.y.toPx() + hubSize.height.toPx() / 2f
        )

        relations.forEach { relation ->
            val relationCenter = Offset(
                x = relation.position.x.toPx() + relationSize.width.toPx() / 2f,
                y = relation.position.y.toPx() + relationSize.height.toPx() / 2f
            )
            drawArrowConnection(
                start = hubCenter,
                end = relationCenter,
                color = relation.accent
            )
        }
    }
}

private fun DrawScope.drawArrowConnection(
    start: Offset,
    end: Offset,
    color: androidx.compose.ui.graphics.Color
) {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val distance = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(1f)
    val startInset = 40f
    val endInset = 44f

    val startPoint = Offset(
        x = start.x + (dx / distance) * startInset,
        y = start.y + (dy / distance) * startInset
    )
    val endPoint = Offset(
        x = end.x - (dx / distance) * endInset,
        y = end.y - (dy / distance) * endInset
    )

    drawLine(
            color = color.copy(alpha = 0.5f),
            start = startPoint,
            end = endPoint,
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )

    val angle = kotlin.math.atan2(dy, dx)
    val arrowLength = 15f
    val arrowAngle = kotlin.math.PI / 7f
    val tip = endPoint
    val left = Offset(
        x = tip.x - arrowLength * kotlin.math.cos(angle - arrowAngle).toFloat(),
        y = tip.y - arrowLength * kotlin.math.sin(angle - arrowAngle).toFloat()
    )
    val right = Offset(
        x = tip.x - arrowLength * kotlin.math.cos(angle + arrowAngle).toFloat(),
        y = tip.y - arrowLength * kotlin.math.sin(angle + arrowAngle).toFloat()
    )

    drawLine(
        color = color,
        start = tip,
        end = left,
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = color,
        start = tip,
        end = right,
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )
}

@Composable
fun EmptyState(title: String, body: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
