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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import android.graphics.Paint as AndroidPaint
import android.graphics.Typeface
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.tooling.preview.Preview
import com.example.myvocabulary.ui.theme.Accent
import com.example.myvocabulary.ui.theme.AccentDark
import com.example.myvocabulary.ui.theme.MyVocabularyTheme

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
    showEntryCounts: Boolean,
    primaryLanguage: DisplayLanguage,
    inlineTranslations: Boolean
) {
    val wordsForPager = remember(wordOfDayPages) {
        if (wordOfDayPages.isEmpty()) listOf(vocabularyWords.first()) else wordOfDayPages
    }
    val pagerState = rememberPagerState(pageCount = { wordsForPager.size })
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
                            word = wordsForPager[page],
                            primaryLanguage = primaryLanguage,
                            inlineTranslations = inlineTranslations,
                            onClick = { onWordClick(wordsForPager[page].id) }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(wordsForPager.size) { index ->
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
                            primaryLanguage = primaryLanguage,
                            inlineTranslations = inlineTranslations,
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
                                val matchedWord = remember(term) {
                                    vocabularyWords.firstOrNull {
                                        it.cree.equals(term, ignoreCase = true) ||
                                        it.english.equals(term, ignoreCase = true) ||
                                        it.id.equals(term, ignoreCase = true)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                    modifier = Modifier.combinedClickable(
                                        onClick = { onRecentSearchClick(term) },
                                        onLongClick = { searchToDelete = term }
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (matchedWord != null) {
                                            val primary = if (primaryLanguage == DisplayLanguage.English) matchedWord.english.replaceFirstChar { it.uppercase() } else matchedWord.cree
                                            val secondary = if (primaryLanguage == DisplayLanguage.English) matchedWord.cree else matchedWord.english
                                            Text(
                                                text = primary,
                                                style = MaterialTheme.typography.labelLarge,
                                                maxLines = 1
                                            )
                                            if (inlineTranslations) {
                                                Text(
                                                    text = secondary,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                    maxLines = 1
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = term,
                                                style = MaterialTheme.typography.labelLarge,
                                                maxLines = 1
                                            )
                                        }
                                    }
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
    onBack: () -> Unit,
    primaryLanguage: DisplayLanguage = DisplayLanguage.Both,
    inlineTranslations: Boolean = false
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
                        selectedSearches = if (selectedSearches.size == recentSearches.size) {
                            emptySet()
                        } else {
                            recentSearches.toSet()
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
                items(recentSearches) { term ->
                    val matchedWord = remember(term) {
                        vocabularyWords.firstOrNull {
                            it.cree.equals(term, ignoreCase = true) ||
                            it.english.equals(term, ignoreCase = true) ||
                            it.id.equals(term, ignoreCase = true)
                        }
                    }
                    
                    Surface(
                        onClick = { onRecentSearchClick(term) },
                        modifier = Modifier.fillMaxWidth(),
                        color = androidx.compose.ui.graphics.Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = term in selectedSearches,
                                onCheckedChange = { checked ->
                                    selectedSearches = if (checked) {
                                        selectedSearches + term
                                    } else {
                                        selectedSearches - term
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (matchedWord != null) {
                                    val primary = if (primaryLanguage == DisplayLanguage.English) matchedWord.english.replaceFirstChar { it.uppercase() } else matchedWord.cree
                                    val secondary = if (primaryLanguage == DisplayLanguage.English) matchedWord.cree else matchedWord.english
                                    Text(
                                        text = primary,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    if (inlineTranslations) {
                                        Text(
                                            text = secondary,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = term,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
    primaryLanguage: DisplayLanguage,
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
                        primaryLanguage = primaryLanguage,
                        showInlineTranslation = inlineTranslations,
                        onClick = { onWordClick(word.id) }
                    )
                }
            }
        }
    }
}

enum class CategorySort {
    AlphabeticalAZ,
    AlphabeticalZA
}

@Composable
fun CategoriesScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackToHome: () -> Unit,
    onCategoryClick: (CategoryCard) -> Unit,
    showEntryCounts: Boolean,
    primaryLanguage: DisplayLanguage,
    inlineTranslations: Boolean
) {
    var sortOrder by remember { mutableStateOf(CategorySort.AlphabeticalAZ) }
    var wordTypeFilter by remember { mutableStateOf(WordTypeFilter.All) }
    
    val categoryResults = remember(query, sortOrder, primaryLanguage) {
        val normalizedQuery = query.trim().lowercase()
        val queryTokens = normalizedQuery.split(Regex("\\s+")).filter { it.isNotBlank() }
        
        val filtered = suggestedCategories.filter {
            normalizedQuery.isBlank() ||
                it.title.contains(normalizedQuery, ignoreCase = true) ||
                it.description.contains(normalizedQuery, ignoreCase = true) ||
                queryTokens.all { token ->
                    it.title.contains(token, ignoreCase = true) ||
                        it.description.contains(token, ignoreCase = true)
                }
        }
        
        when (sortOrder) {
            CategorySort.AlphabeticalAZ -> filtered.sortedBy { if (primaryLanguage == DisplayLanguage.English) it.title else it.subject.creeLabel }
            CategorySort.AlphabeticalZA -> filtered.sortedByDescending { if (primaryLanguage == DisplayLanguage.English) it.title else it.subject.creeLabel }
        }
    }

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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DropdownFilterField(
                            label = "Sort By",
                            selectedValue = if (sortOrder == CategorySort.AlphabeticalAZ) "A - Z" else "Z - A",
                            options = listOf(CategorySort.AlphabeticalAZ, CategorySort.AlphabeticalZA),
                            optionLabel = { if (it == CategorySort.AlphabeticalAZ) "A - Z" else "Z - A" },
                            onSelected = { sortOrder = it },
                            modifier = Modifier.width(180.dp)
                        )
                        DropdownFilterField(
                            label = "Word Type",
                            selectedValue = wordTypeFilter.label,
                            options = WordTypeFilter.entries,
                            optionLabel = { it.label },
                            onSelected = { wordTypeFilter = it },
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
            }
            item { SectionTitle("Categories") }
            
            items(categoryResults.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowItems.forEach { category ->
                        CategoryGridCard(
                            category = category,
                            modifier = Modifier.weight(1f).height(180.dp),
                            showEntryCount = showEntryCounts,
                            primaryLanguage = primaryLanguage,
                            inlineTranslations = inlineTranslations,
                            onClick = { onCategoryClick(category) }
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
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
    primaryLanguage: DisplayLanguage,
    inlineTranslations: Boolean,
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
                        val primaryText = if (primaryLanguage == DisplayLanguage.English) word.english.replaceFirstChar { it.uppercase() } else word.cree
                        val secondaryText = if (primaryLanguage == DisplayLanguage.English) word.cree else word.english
                        Text(
                            text = primaryText,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (inlineTranslations) {
                            Text(
                                text = secondaryText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            item {
                SectionTitle("Pronunciation")
                ActionRow(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    label = word.pronunciationLabel,
                    onClick = { playbackMessage = "Playing audio for ${word.cree}" }
                )
            }
            item {
                SectionTitle("Part of Speech")
                ActionRow(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
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
                item { SectionTitle("Morphology") }
                item {
                    MorphologyCard(
                        word = word,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp, end = 12.dp)
                    )
                }
            }
            item {
                SectionTitle("Related Words")
                relatedWords.forEach { relatedWord ->
                    RelatedWordItem(
                        word = relatedWord,
                        primaryLanguage = primaryLanguage,
                        inlineTranslations = inlineTranslations,
                        onClick = { onWordClick(relatedWord.id) }
                    )
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
    showSemanticRelationLabels: Boolean,
    primaryLanguage: DisplayLanguage,
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
                    showSemanticRelationLabels = showSemanticRelationLabels,
                    showSecondaryMeanings = true,
                    primaryLanguage = primaryLanguage,
                    expandedMapLayout = false,
                    mapViewportMaxHeight = 300.dp,
                    onWordClick = onWordClick
                )
            }
            item {
                Text(
                    text = "Tap any node to open that word. Line labels follow Settings → Expert Mode.",
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
    onPrimaryLanguageChange: (DisplayLanguage) -> Unit,
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
                    SectionTitle("General Settings")
                    SettingsRow(
                        title = "Primary Language",
                        subtitle = "Choose your primary language for vocabulary.",
                        trailing = {
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                Surface(
                                    onClick = { expanded = true },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = primaryLanguage.label,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                                    }
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DisplayLanguage.entries.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option.label) },
                                            onClick = {
                                                onPrimaryLanguageChange(option)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    )
                    SettingsRow(
                        title = "Inline Translations",
                        subtitle = "Show translations inline next to vocabulary.",
                        trailing = {
                            Switch(
                                checked = if (primaryLanguage == DisplayLanguage.Both) true else inlineTranslations,
                                onCheckedChange = onInlineTranslationsChange,
                                enabled = primaryLanguage != DisplayLanguage.Both,
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
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Expert Settings")
                    SettingsRow(
                        title = "Expert Mode",
                        subtitle = "Advanced language tools",
                        onClick = onOpenExpertMode,
                        trailing = { Text("›", style = MaterialTheme.typography.headlineSmall) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExpertModeScreen(
    showSemanticRelationLabels: Boolean,
    onShowSemanticRelationLabelsChange: (Boolean) -> Unit,
    showMorphology: Boolean,
    onShowMorphologyChange: (Boolean) -> Unit,
    showEntryCounts: Boolean,
    onShowEntryCountsChange: (Boolean) -> Unit,
    primaryLanguage: DisplayLanguage,
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
                        subtitle = "Display the morphology card on details.",
                        trailing = {
                            Switch(
                                checked = showMorphology,
                                onCheckedChange = onShowMorphologyChange,
                                colors = appSwitchColors()
                            )
                        }
                    )
                    MorphologyCard(
                        word = previewWord,
                        title = "Morphology Preview",
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    
                    SettingsRow(
                        title = "Show Semantic Relation labels",
                        subtitle = "Display relationship labels along the lines in the semantic map.",
                        trailing = {
                            Switch(
                                checked = showSemanticRelationLabels,
                                onCheckedChange = onShowSemanticRelationLabelsChange,
                                colors = appSwitchColors()
                            )
                        }
                    )
                    SemanticMapPreviewCard(
                        showSemanticRelationLabels = showSemanticRelationLabels,
                        primaryLanguage = primaryLanguage,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VocabularyScreenSurface(content: @Composable () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val backgroundBrush = remember(colorScheme.background, colorScheme.surface) {
        Brush.verticalGradient(
            listOf(
                colorScheme.background,
                colorScheme.surface.copy(alpha = 0.96f),
                colorScheme.background
            )
        )
    }
    val radialBrush = remember(colorScheme.primary) {
        Brush.radialGradient(
            colors = listOf(
                colorScheme.primary.copy(alpha = 0.08f),
                androidx.compose.ui.graphics.Color.Transparent
            ),
            center = Offset(120f, 140f),
            radius = 1100f
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(radialBrush)
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
fun WordOfDayCard(
    word: VocabularyWord,
    primaryLanguage: DisplayLanguage,
    inlineTranslations: Boolean,
    onClick: () -> Unit
) {
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
                val primaryText = if (primaryLanguage == DisplayLanguage.English) word.english.replaceFirstChar { it.uppercase() } else word.cree
                val secondaryText = if (primaryLanguage == DisplayLanguage.English) word.cree else word.english
                
                Text(
                    text = primaryText,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (inlineTranslations) {
                    Text(
                        text = secondaryText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    word: VocabularyWord,
    primaryLanguage: DisplayLanguage,
    showInlineTranslation: Boolean,
    onClick: () -> Unit
) {
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
                val primaryText = if (primaryLanguage == DisplayLanguage.English) word.english.replaceFirstChar { it.uppercase() } else word.cree
                val secondaryText = if (primaryLanguage == DisplayLanguage.English) word.cree else word.english.replaceFirstChar { it.uppercase() }

                Text(
                    text = primaryText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (showInlineTranslation) {
                    Text(
                        text = secondaryText,
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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
    primaryLanguage: DisplayLanguage = DisplayLanguage.Both,
    inlineTranslations: Boolean = false,
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
                val unit = if (category.count == 1) "entry" else "entries"
                Text(
                    text = "${category.count} $unit",
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
            val primaryText = if (primaryLanguage == DisplayLanguage.English) category.title else category.subject.creeLabel
            val secondaryText = if (primaryLanguage == DisplayLanguage.English) category.subject.creeLabel else category.title

            Text(
                text = primaryText,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (inlineTranslations) {
                Text(
                    text = secondaryText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
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
fun RelatedWordItem(
    word: VocabularyWord,
    primaryLanguage: DisplayLanguage,
    inlineTranslations: Boolean,
    onClick: () -> Unit
) {
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
                val primaryText = if (primaryLanguage == DisplayLanguage.English) word.english.replaceFirstChar { it.uppercase() } else word.cree
                val secondaryText = if (primaryLanguage == DisplayLanguage.English) word.cree else word.english

                Text(
                    text = primaryText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (inlineTranslations) {
                    Text(
                        text = secondaryText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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
fun MorphologyCard(
    word: VocabularyWord,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (word.detailedMorphology.stem.isNotBlank()) {
                        MorphologyRow(
                            label = "stem:",
                            value = word.detailedMorphology.stem,
                            description = if (word.detailedMorphology.stemMeaning.isNotBlank()) "“${word.detailedMorphology.stemMeaning}”" else ""
                        )
                    }
                    if (word.detailedMorphology.suffix.isNotBlank()) {
                        MorphologyRow(
                            label = "suffix:",
                            value = word.detailedMorphology.suffix,
                            description = if (word.detailedMorphology.suffixMeaning.isNotBlank()) "“${word.detailedMorphology.suffixMeaning}”" else ""
                        )
                    }
                    if (word.detailedMorphology.grammaticalForm.isNotBlank()) {
                        MorphologyRow(
                            label = "grammatical form:",
                            value = word.detailedMorphology.grammaticalForm,
                            description = ""
                        )
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
            modifier = Modifier.width(130.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun SemanticMapPreviewCard(
    showSemanticRelationLabels: Boolean = false,
    primaryLanguage: DisplayLanguage,
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
            showSemanticRelationLabels = showSemanticRelationLabels,
            showSecondaryMeanings = true,
            primaryLanguage = primaryLanguage,
            title = "Semantic Map Preview",
            expandedMapLayout = false,
            mapViewportMaxHeight = 260.dp,
            onWordClick = onWordClick
        )
    }
}

private data class SemanticMapLayout(
    val mapWidth: androidx.compose.ui.unit.Dp,
    val mapHeight: androidx.compose.ui.unit.Dp,
    val hubPosition: SemanticMapNodePosition,
    val hubSize: DpSize,
    val relationSize: DpSize,
    val relationPositions: List<SemanticMapNodePosition>
)

@Composable
private fun rememberSemanticMapLayout(
    relationCount: Int,
    expandedMapLayout: Boolean,
    showSemanticRelationLabels: Boolean
): SemanticMapLayout {
    val density = LocalDensity.current
    val hubSize = DpSize(136.dp, 54.dp)
    val relationSize = DpSize(118.dp, 54.dp)
    val baseMapWidth = if (expandedMapLayout) 720.dp else 640.dp
    val baseMapHeight = if (expandedMapLayout) 520.dp else 460.dp
    return remember(
        relationCount,
        expandedMapLayout,
        showSemanticRelationLabels,
        baseMapWidth,
        baseMapHeight,
        hubSize,
        relationSize,
        density
    ) {
        val hubWidth = hubSize.width
        val hubHeight = hubSize.height
        val nodeWidth = relationSize.width
        val nodeHeight = relationSize.height
        val (relationPositions, mapWidth, mapHeight) = if (relationCount == 0) {
            Triple(emptyList(), baseMapWidth, baseMapHeight)
        } else {
            with(density) {
                val hubRadiusPx = max(hubWidth.toPx(), hubHeight.toPx()) / 2f
                val nodeRadiusPx = max(nodeWidth.toPx(), nodeHeight.toPx()) / 2f
                val radialGapPx = if (showSemanticRelationLabels) 88.dp.toPx() else 32.dp.toPx()
                val lateralGapPx = if (showSemanticRelationLabels) 44.dp.toPx() else 26.dp.toPx()
                val chordNeededPx = nodeWidth.toPx() + lateralGapPx
                val n = relationCount
                val rFromHub = hubRadiusPx + nodeRadiusPx + radialGapPx
                val rFromChord = if (n <= 1) {
                    rFromHub
                } else {
                    chordNeededPx / (2f * sin(PI / n).toFloat())
                }
                val rPx = max(rFromHub, rFromChord)
                val edgeMarginPx = 24.dp.toPx()
                val minSidePx = 2f * (rPx + nodeRadiusPx + edgeMarginPx)
                val sidePx = maxOf(baseMapWidth.toPx(), baseMapHeight.toPx(), minSidePx)
                val mapW = sidePx.toDp()
                val mapH = sidePx.toDp()
                val hubLeft = (mapW - hubWidth) / 2
                val hubTop = (mapH - hubHeight) / 2
                val hubCx = hubLeft + hubWidth / 2
                val hubCy = hubTop + hubHeight / 2
                val positions = (0 until relationCount).map { index ->
                    val angle = -Math.PI / 2 + index * (2 * Math.PI / relationCount)
                    val cxPx = hubCx.toPx() + rPx * kotlin.math.cos(angle).toFloat()
                    val cyPx = hubCy.toPx() + rPx * kotlin.math.sin(angle).toFloat()
                    SemanticMapNodePosition(
                        x = (cxPx - nodeWidth.toPx() / 2f).toDp(),
                        y = (cyPx - nodeHeight.toPx() / 2f).toDp()
                    )
                }
                Triple(positions, mapW, mapH)
            }
        }
        val hubLeft = (mapWidth - hubSize.width) / 2
        val hubTop = (mapHeight - hubSize.height) / 2
        val hubPosition = SemanticMapNodePosition(hubLeft, hubTop)
        SemanticMapLayout(
            mapWidth = mapWidth,
            mapHeight = mapHeight,
            hubPosition = hubPosition,
            hubSize = hubSize,
            relationSize = relationSize,
            relationPositions = relationPositions
        )
    }
}

@Composable
fun WordSemanticMapCard(
    word: VocabularyWord,
    relatedWords: List<VocabularyWord>,
    showSemanticRelationLabels: Boolean,
    showSecondaryMeanings: Boolean = true,
    primaryLanguage: DisplayLanguage,
    title: String = "Semantic Map",
    expandedMapLayout: Boolean = false,
    mapViewportMaxHeight: Dp? = null,
    onWordClick: (String) -> Unit
) {
    val centerAccent = Accent
    val layout = rememberSemanticMapLayout(
        relationCount = relatedWords.size,
        expandedMapLayout = expandedMapLayout,
        showSemanticRelationLabels = showSemanticRelationLabels
    )
    val mapSize = androidx.compose.ui.unit.DpSize(layout.mapWidth, layout.mapHeight)
    val viewportHeight = mapViewportMaxHeight?.let { layout.mapHeight.coerceAtMost(it) }
        ?: layout.mapHeight
    val minZoom = 0.45f
    val maxZoom = 2.4f
    val initialZoom = when {
        showSemanticRelationLabels && expandedMapLayout -> 0.8f
        showSemanticRelationLabels -> 0.72f
        expandedMapLayout -> 0.92f
        else -> 0.82f
    }
    var zoom by remember(word.id, expandedMapLayout, showSemanticRelationLabels) { mutableStateOf(initialZoom) }
    var offset by remember(word.id, expandedMapLayout, showSemanticRelationLabels) { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        zoom = (zoom * zoomChange).coerceIn(minZoom, maxZoom)
        offset += panChange
    }
    val relationLabelByTargetId = remember(word.id, word.relatedWordIds, word.relatedSemanticRelationLabels) {
        word.relatedWordIds.mapIndexed { index, id ->
            id to word.relatedSemanticRelationLabels.getOrElse(index) { "" }
        }.toMap()
    }
    val relations = remember(word.id, relatedWords, layout, relationLabelByTargetId) {
        relatedWords.mapIndexed { index, relatedWord ->
            val label = relationLabelByTargetId[relatedWord.id]
                ?.takeIf { it.isNotBlank() }
                ?: inferredSemanticRelationLabel(word, relatedWord)
            val dashed = label.contains("HABITAT", ignoreCase = true) ||
                label.contains("ACTION", ignoreCase = true) ||
                label.contains("ASSOCIATED", ignoreCase = true)
            val position = layout.relationPositions.getOrElse(index) {
                SemanticMapNodePosition(0.dp, 0.dp)
            }
            SemanticRelationNode(
                word = relatedWord,
                position = position,
                accent = semanticMapAccent(index),
                relationLabel = label,
                strokeDashed = dashed
            )
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val hubPrimary = if (primaryLanguage == DisplayLanguage.English) word.english.replaceFirstChar { it.uppercase() } else word.cree
            val hubSecondary = if (primaryLanguage == DisplayLanguage.English) word.cree else word.english

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = hubPrimary,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = hubSecondary,
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
                        .height(viewportHeight)
                        .clip(RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(mapSize.width, mapSize.height)
                            .transformable(transformState)
                            .graphicsLayer(
                                scaleX = zoom,
                                scaleY = zoom,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    ) {
                        key(showSemanticRelationLabels) {
                            SemanticConnectionLayer(
                                hubPosition = layout.hubPosition,
                                hubSize = layout.hubSize,
                                relations = relations,
                                relationSize = layout.relationSize,
                                showSemanticRelationLabels = showSemanticRelationLabels
                            )
                        }
                        SemanticHub(
                            label = hubPrimary,
                            subtitle = hubSecondary,
                            showSubtitle = showSecondaryMeanings,
                            modifier = Modifier
                                .offset(x = layout.hubPosition.x, y = layout.hubPosition.y)
                                .size(layout.hubSize.width, layout.hubSize.height),
                            accent = centerAccent
                        )
                        relations.forEach { relation ->
                            SemanticRelationCard(
                                word = relation.word,
                                accent = relation.accent,
                                showSecondaryMeanings = showSecondaryMeanings,
                                primaryLanguage = primaryLanguage,
                                modifier = Modifier
                                    .offset(x = relation.position.x, y = relation.position.y)
                                    .size(layout.relationSize.width, layout.relationSize.height),
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
        border = BorderStroke(1.5.dp, accent),
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
    showSecondaryMeanings: Boolean,
    primaryLanguage: DisplayLanguage,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val primaryText = if (primaryLanguage == DisplayLanguage.English) {
        word.english.replaceFirstChar { it.uppercase() }
    } else {
        word.cree
    }
    val secondaryText = if (primaryLanguage == DisplayLanguage.English) {
        word.cree
    } else {
        word.english.replaceFirstChar { it.uppercase() }
    }
    Surface(
        color = accent.copy(alpha = 0.14f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.5.dp, accent),
        shadowElevation = 0.5.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = primaryText,
                style = MaterialTheme.typography.titleSmall,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showSecondaryMeanings) {
                Text(
                    text = secondaryText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private data class SemanticRelationNode(
    val word: VocabularyWord,
    val position: SemanticMapNodePosition,
    val accent: androidx.compose.ui.graphics.Color,
    val relationLabel: String,
    val strokeDashed: Boolean
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

private fun semanticMapAccent(index: Int): androidx.compose.ui.graphics.Color {
    return semanticMapAccentPalette[index % semanticMapAccentPalette.size]
}

private fun inferredSemanticRelationLabel(
    word: VocabularyWord,
    relatedWord: VocabularyWord
): String {
    if (word.partOfSpeech.equals("adjective", ignoreCase = true) &&
        relatedWord.partOfSpeech.equals("noun", ignoreCase = true)
    ) {
        return "DESCRIBES"
    }
    if (word.partOfSpeech.equals("noun", ignoreCase = true) &&
        relatedWord.partOfSpeech.equals("adjective", ignoreCase = true)
    ) {
        return "DESCRIBED BY"
    }
    if (word.partOfSpeech.equals("verb", ignoreCase = true) ||
        relatedWord.partOfSpeech.equals("verb", ignoreCase = true)
    ) {
        return "RELATED ACTION"
    }
    if (word.partOfSpeech.equals("phrase", ignoreCase = true) ||
        relatedWord.partOfSpeech.equals("phrase", ignoreCase = true)
    ) {
        return "RELATED PHRASE"
    }

    if (word.subject == relatedWord.subject) {
        return when (word.subject) {
            SubjectFilter.Animals -> "RELATED ANIMAL"
            SubjectFilter.Body -> "BODY TERM"
            SubjectFilter.Weather -> "WEATHER TERM"
            SubjectFilter.Foods -> "FOOD TERM"
            SubjectFilter.Lands -> "PLACE TERM"
            SubjectFilter.Words, SubjectFilter.All -> "RELATED WORD"
        }
    }

    return "ASSOCIATED TERM"
}

@Composable
private fun SemanticConnectionLayer(
    hubPosition: SemanticMapNodePosition,
    hubSize: androidx.compose.ui.unit.DpSize,
    relations: List<SemanticRelationNode>,
    relationSize: androidx.compose.ui.unit.DpSize,
    showSemanticRelationLabels: Boolean
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
                color = relation.accent,
                dashed = relation.strokeDashed,
                relationLabel = relation.relationLabel,
                showLabel = showSemanticRelationLabels && relation.relationLabel.isNotBlank()
            )
        }
    }
}

private fun DrawScope.drawArrowConnection(
    start: Offset,
    end: Offset,
    color: androidx.compose.ui.graphics.Color,
    dashed: Boolean,
    relationLabel: String,
    showLabel: Boolean
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

    val pathEffect = if (dashed) {
        PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
    } else {
        null
    }

    drawLine(
        color = color.copy(alpha = 0.55f),
        start = startPoint,
        end = endPoint,
        strokeWidth = 4f,
        cap = StrokeCap.Round,
        pathEffect = pathEffect
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

    if (showLabel) {
        drawRelationLabel(
            lineStart = startPoint,
            lineEnd = endPoint,
            color = color,
            label = relationLabel
        )
    }
}

private fun DrawScope.drawRelationLabel(
    lineStart: Offset,
    lineEnd: Offset,
    color: androidx.compose.ui.graphics.Color,
    label: String
) {
    if (label.isBlank()) return
    val midX = (lineStart.x + lineEnd.x) / 2f
    val midY = (lineStart.y + lineEnd.y) / 2f
    val dx = lineEnd.x - lineStart.x
    val dy = lineEnd.y - lineStart.y
    val len = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(1f)
    val perpX = -dy / len * 16f
    val perpY = dx / len * 16f
    val textX = midX + perpX
    val textY = midY + perpY
    var angleDeg = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble()))
    if (angleDeg > 90) angleDeg -= 180
    if (angleDeg < -90) angleDeg += 180

    val textSizePx = 10f * density * fontScale
    val paint = AndroidPaint().apply {
        isAntiAlias = true
        this.color = color.toArgb()
        this.textSize = textSizePx
        textAlign = AndroidPaint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    drawContext.canvas.nativeCanvas.apply {
        save()
        rotate(angleDeg.toFloat(), textX, textY)
        val textBaseline = textY - (paint.descent() + paint.ascent()) / 2f
        drawText(label.uppercase(), textX, textBaseline, paint)
        restore()
    }
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

@Preview(showBackground = true)
@Composable
fun WordDetailsScreenPreview() {
    MyVocabularyTheme {
        val word = vocabularyWords.first { it.id == "wapos" }
        val relatedWords = word.relatedWordIds.mapNotNull { id ->
            vocabularyWords.find { it.id == id }
        }
        WordDetailsScreen(
            word = word,
            relatedWords = relatedWords,
            showMorphology = true,
            primaryLanguage = DisplayLanguage.English,
            inlineTranslations = true,
            onBack = {},
            onWordClick = {},
            onConnectionsClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MyVocabularyTheme {
        HomeScreen(
            query = "",
            onQueryChange = {},
            onSearch = {},
            wordOfDayPages = vocabularyWords.take(3),
            recentSearches = listOf("Wâpos", "Rain", "Miko"),
            onRecentSearchClick = {},
            onDeleteRecentSearch = {},
            onSeeAllRecentSearches = {},
            categories = suggestedCategories,
            onCategoryClick = {},
            onWordClick = {},
            showEntryCounts = true,
            primaryLanguage = DisplayLanguage.English,
            inlineTranslations = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecentSearchesScreenPreview() {
    MyVocabularyTheme {
        RecentSearchesScreen(
            recentSearches = listOf("Wâpos", "Rain", "Heart", "Miko"),
            onRecentSearchClick = {},
            onDeleteSelectedSearches = {},
            onBack = {},
            primaryLanguage = DisplayLanguage.English,
            inlineTranslations = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultsScreenPreview() {
    MyVocabularyTheme {
        SearchResultsScreen(
            query = "wapos",
            subjectFilter = SubjectFilter.All,
            wordTypeFilter = WordTypeFilter.All,
            sortOption = SortOption.Relevance,
            inlineTranslations = true,
            primaryLanguage = DisplayLanguage.Both,
            onQueryChange = {},
            onSubjectChange = {},
            onWordTypeChange = {},
            onSortChange = {},
            onResetFilters = {},
            onBack = {},
            onWordClick = {},
            onSubmitSearch = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesScreenPreview() {
    MyVocabularyTheme {
        CategoriesScreen(
            query = "",
            onQueryChange = {},
            onBackToHome = {},
            onCategoryClick = {},
            showEntryCounts = true,
            primaryLanguage = DisplayLanguage.English,
            inlineTranslations = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SemanticMapScreenPreview() {
    MyVocabularyTheme {
        val word = vocabularyWords.first { it.id == "wapos" }
        val relatedWords = word.relatedWordIds.mapNotNull { id ->
            vocabularyWords.find { it.id == id }
        }
        SemanticMapScreen(
            word = word,
            relatedWords = relatedWords,
            showSemanticRelationLabels = true,
            primaryLanguage = DisplayLanguage.English,
            onBack = {},
            onWordClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MyVocabularyTheme {
        SettingsScreen(
            primaryLanguage = DisplayLanguage.English,
            onPrimaryLanguageChange = {},
            inlineTranslations = true,
            onInlineTranslationsChange = {},
            onOpenExpertMode = {},
            onSeeRecentSearches = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExpertModeScreenPreview() {
    MyVocabularyTheme {
        ExpertModeScreen(
            showSemanticRelationLabels = true,
            onShowSemanticRelationLabelsChange = {},
            showMorphology = true,
            onShowMorphologyChange = {},
            showEntryCounts = true,
            onShowEntryCountsChange = {},
            primaryLanguage = DisplayLanguage.English,
            onBack = {}
        )
    }
}
