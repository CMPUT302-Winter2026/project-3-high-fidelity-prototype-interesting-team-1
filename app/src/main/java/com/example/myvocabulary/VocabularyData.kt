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

enum class SubjectFilter(val label: String, val creeLabel: String) {
    All("All", "Kahkiyaw"),
    Animals("Animals", "Pisiskiwak"),
    Body("Body", "Miyaw"),
    Weather("Weather", "Kîsikâw"),
    Words("Words", "Pîkiskwêwina"),
    Foods("Foods", "Mîciwina"),
    Lands("Lands", "Askiy");

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
    AlphabeticalAZ("A-Z"),
    AlphabeticalZA("Z-A");

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

data class DetailedMorphology(
    val stem: String = "",
    val stemMeaning: String = "",
    val suffix: String = "",
    val suffixMeaning: String = "",
    val grammaticalForm: String = ""
)

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
    val relatedSemanticRelationLabels: List<String> = emptyList(),
    val morphology: String = "",
    val detailedMorphology: DetailedMorphology = DetailedMorphology(),
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
        relatedWordIds = listOf("waposis", "wapak", "deer", "fox", "wolf"),
        relatedSemanticRelationLabels = listOf(
            "DIMINUTIVE OF",
            "RELATED ANIMAL",
            "RELATED ANIMAL",
            "RELATED ANIMAL",
            "RELATED ANIMAL"
        ),
        morphology = "Root form: wapos. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "wâpos-",
            stemMeaning = "rabbit",
            suffix = "",
            suffixMeaning = "",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "wâpos-",
            stemMeaning = "rabbit",
            suffix = "-is",
            suffixMeaning = "diminutive (small)",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "wâpâk-",
            stemMeaning = "hare",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "wâpitiw-",
            stemMeaning = "animal",
            grammaticalForm = "noun • animate • singular"
        ),
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
        relatedWordIds = listOf("water", "rain", "fish", "bird", "cloud"),
        morphology = "Root form: moswa. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "môsw-",
            stemMeaning = "mosquito",
            suffix = "-a",
            suffixMeaning = "animate plural",
            grammaticalForm = "noun • animate • plural"
        ),
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
        relatedWordIds = listOf("snow", "wind", "cloud", "thunder", "cold"),
        morphology = "Root form: kisaw. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "kisâw-",
            stemMeaning = "rain",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "mistapêw-",
            stemMeaning = "snow",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "miyô-",
            stemMeaning = "good / fair",
            suffix = "-w",
            suffixMeaning = "inanimate third person",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "pîkiskwê-",
            stemMeaning = "speak / word",
            suffix = "-w",
            suffixMeaning = "agentive suffix",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "mîyo-",
            stemMeaning = "good",
            suffix = "-htâw",
            suffixMeaning = "internal body part",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "nîp-",
            stemMeaning = "hand",
            suffix = "-în",
            suffixMeaning = "plural inanimate",
            grammaticalForm = "noun • inanimate • plural"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "mîci-",
            stemMeaning = "eat something",
            suffix = "-so",
            suffixMeaning = "reflexive (self)",
            grammaticalForm = "verb • animate intransitive • imperative"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "pimi-",
            stemMeaning = "fat / oil",
            suffix = "-wâkan",
            suffixMeaning = "tool / instrument",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "pimi-",
            stemMeaning = "along",
            suffix = "-pahtâw",
            suffixMeaning = "run / move quickly",
            grammaticalForm = "verb • animate intransitive • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "mîchif-",
            stemMeaning = "Michif / road",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "ni-",
            stemMeaning = "my",
            suffix = "-kaw",
            suffixMeaning = "body",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "mi-",
            stemMeaning = "someone's / indefinite",
            suffix = "-taw",
            suffixMeaning = "eye",
            grammaticalForm = "noun • inanimate • plural"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "mi-",
            stemMeaning = "someone's / indefinite",
            suffix = "-tapan",
            suffixMeaning = "foot / leg",
            grammaticalForm = "noun • inanimate • plural"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "mi-",
            stemMeaning = "fruit / berry",
            suffix = "-min",
            suffixMeaning = "small round object",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        relatedWordIds = listOf("fish", "lake", "river", "tea", "soup"),
        morphology = "Root form: nipiy. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "nipiy-",
            stemMeaning = "water",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "kîsik-",
            stemMeaning = "sky / day / home",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "misk-",
            stemMeaning = "find",
            suffix = "-otahk",
            suffixMeaning = "place of dwelling",
            grammaticalForm = "noun • inanimate • locative"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "misko-",
            stemMeaning = "red / blood",
            suffix = "-w",
            suffixMeaning = "stative suffix",
            grammaticalForm = "adjective • descriptive"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "mistik-",
            stemMeaning = "wood / tree",
            suffix = "-w",
            suffixMeaning = "singular marker",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "mask-",
            stemMeaning = "bear",
            grammaticalForm = "noun • animate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "atikw-",
            stemMeaning = "caribou / deer",
            grammaticalForm = "noun • animate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "pîsim-",
            stemMeaning = "sun / moon / month",
            grammaticalForm = "noun • animate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "sak-",
            stemMeaning = "tight / together",
            suffix = "-ahikan",
            suffixMeaning = "artificial / body of water",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "kinosêw-",
            stemMeaning = "fish",
            grammaticalForm = "noun • animate • singular"
        ),
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
        detailedMorphology = DetailedMorphology(
            stem = "mîci-",
            stemMeaning = "eat",
            suffix = "-m",
            suffixMeaning = "nominalizer (thing)",
            grammaticalForm = "noun • inanimate • singular"
        ),
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
        relatedWordIds = listOf("hello", "thanks", "good", "yes", "no"),
        morphology = "Root form: nitis. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "ni-",
            stemMeaning = "my",
            suffix = "-tîm",
            suffixMeaning = "companion / horse",
            grammaticalForm = "noun • animate • singular"
        ),
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
        relatedWordIds = listOf("friend", "thanks", "yes", "home", "camp"),
        morphology = "Greeting phrase used to say hello.",
        detailedMorphology = DetailedMorphology(
            stem = "tân-",
            stemMeaning = "how",
            suffix = "-si",
            suffixMeaning = "manner suffix",
            grammaticalForm = "phrase • greeting"
        ),
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
        relatedWordIds = listOf("misko", "big", "friend", "thanks", "hello"),
        morphology = "Descriptive adjective meaning \"good\".",
        detailedMorphology = DetailedMorphology(
            stem = "mîyo-",
            stemMeaning = "good / well",
            grammaticalForm = "adjective • particle"
        ),
        icon = "GD"
    ),
    VocabularyWord(
        id = "fox",
        cree = "Mahkêsis",
        english = "fox",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mahkêsis pimohtew. (The fox is walking.)",
        relatedWordIds = listOf("wapos", "deer", "wolf", "bird", "forest"),
        morphology = "Root form: mahkêsis. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "mahkêsis-",
            stemMeaning = "fox",
            grammaticalForm = "noun • animate • singular"
        ),
        icon = "FX"
    ),
    VocabularyWord(
        id = "wolf",
        cree = "Mahihkan",
        english = "wolf",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mahihkan mahkatew. (The wolf is moving.)",
        relatedWordIds = listOf("fox", "deer", "wapos", "forest", "travel"),
        morphology = "Root form: mahihkan. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "mahihkan-",
            stemMeaning = "wolf",
            grammaticalForm = "noun • animate • singular"
        ),
        icon = "WF"
    ),
    VocabularyWord(
        id = "beaver",
        cree = "Amisk",
        english = "beaver",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Amisk nipiyihk ayâw. (The beaver is in the water.)",
        relatedWordIds = listOf("water", "river", "lake", "mistik", "fish"),
        morphology = "Root form: amisk. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "amisk-",
            stemMeaning = "beaver",
            grammaticalForm = "noun • animate • singular"
        ),
        icon = "BV"
    ),
    VocabularyWord(
        id = "bird",
        cree = "Piyêsis",
        english = "bird",
        partOfSpeech = "noun",
        subject = SubjectFilter.Animals,
        categoryLabel = "Animals",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Piyêsis pîhtokêw. (The bird flies in.)",
        relatedWordIds = listOf("fox", "mistik", "sun", "cloud", "forest"),
        morphology = "Root form: piyêsis. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "piyêsis-",
            stemMeaning = "bird",
            grammaticalForm = "noun • animate • singular"
        ),
        icon = "BDR"
    ),
    VocabularyWord(
        id = "head",
        cree = "Mistikwan",
        english = "head",
        partOfSpeech = "noun",
        subject = SubjectFilter.Body,
        categoryLabel = "Body",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mistikwan kîyâm. (The head is still.)",
        relatedWordIds = listOf("body", "eyes", "nose", "mouth", "hands"),
        morphology = "Root form: mistikwan. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "mistikwan-",
            stemMeaning = "head",
            grammaticalForm = "noun • inanimate • singular"
        ),
        icon = "HD2"
    ),
    VocabularyWord(
        id = "nose",
        cree = "Mikot",
        english = "nose",
        partOfSpeech = "noun",
        subject = SubjectFilter.Body,
        categoryLabel = "Body",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mikot mîna. (The nose is small.)",
        relatedWordIds = listOf("head", "eyes", "mouth", "body", "cold"),
        morphology = "Root form: mikot. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "mikot-",
            stemMeaning = "nose",
            grammaticalForm = "noun • inanimate • singular"
        ),
        icon = "NS"
    ),
    VocabularyWord(
        id = "mouth",
        cree = "Mîtôn",
        english = "mouth",
        partOfSpeech = "noun",
        subject = SubjectFilter.Body,
        categoryLabel = "Body",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mîtôn piko. (The mouth is open.)",
        relatedWordIds = listOf("head", "nose", "eat", "water", "friend"),
        morphology = "Root form: mîtôn. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "mîtôn-",
            stemMeaning = "mouth",
            grammaticalForm = "noun • inanimate • singular"
        ),
        icon = "MT"
    ),
    VocabularyWord(
        id = "moon",
        cree = "Tipiskâwipîsim",
        english = "moon",
        partOfSpeech = "noun",
        subject = SubjectFilter.Weather,
        categoryLabel = "Weather",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Tipiskâwipîsim wâpan. (The moon is visible.)",
        relatedWordIds = listOf("sun", "cloud", "snow", "cold", "bird"),
        morphology = "Compound form: tipiskâw + pîsim. Night-sun / moon.",
        detailedMorphology = DetailedMorphology(
            stem = "tipiskâwi-pîsim-",
            stemMeaning = "moon / night sun",
            grammaticalForm = "noun • animate • singular"
        ),
        icon = "MN"
    ),
    VocabularyWord(
        id = "thunder",
        cree = "Animikii",
        english = "thunder",
        partOfSpeech = "noun",
        subject = SubjectFilter.Weather,
        categoryLabel = "Weather",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Animikii pîhtwâw. (Thunder is sounding.)",
        relatedWordIds = listOf("rain", "cloud", "wind", "sun", "cold"),
        morphology = "Root form: animikii. Weather noun.",
        detailedMorphology = DetailedMorphology(
            stem = "animikii-",
            stemMeaning = "thunder",
            grammaticalForm = "noun • animate • singular"
        ),
        icon = "TH"
    ),
    VocabularyWord(
        id = "cold",
        cree = "Kisêw",
        english = "cold",
        partOfSpeech = "adjective",
        subject = SubjectFilter.Weather,
        categoryLabel = "Weather",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Kisêw kisikâw. (It is cold outside.)",
        relatedWordIds = listOf("snow", "wind", "moon", "rain", "water"),
        morphology = "Descriptive adjective meaning \"cold\".",
        detailedMorphology = DetailedMorphology(
            stem = "kisêw-",
            stemMeaning = "cold",
            grammaticalForm = "adjective • descriptive"
        ),
        icon = "CD"
    ),
    VocabularyWord(
        id = "meat",
        cree = "Wiyâs",
        english = "meat",
        partOfSpeech = "noun",
        subject = SubjectFilter.Foods,
        categoryLabel = "Foods",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Wiyâs mîciso. (Eat the meat.)",
        relatedWordIds = listOf("eat", "soup", "water", "mako", "deer"),
        morphology = "Root form: wiyâs. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "wiyâs-",
            stemMeaning = "meat",
            grammaticalForm = "noun • inanimate • singular"
        ),
        icon = "MT2"
    ),
    VocabularyWord(
        id = "tea",
        cree = "Maskihkîwâpoy",
        english = "tea",
        partOfSpeech = "noun",
        subject = SubjectFilter.Foods,
        categoryLabel = "Foods",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Maskihkîwâpoy minwâsin. (The tea is good.)",
        relatedWordIds = listOf("water", "bread", "soup", "friend", "thanks"),
        morphology = "Compound form for tea / medicine drink.",
        detailedMorphology = DetailedMorphology(
            stem = "maskihkî-wâpoy-",
            stemMeaning = "tea / medicine drink",
            grammaticalForm = "noun • inanimate • singular"
        ),
        icon = "TE"
    ),
    VocabularyWord(
        id = "milk",
        cree = "Totôsâpoy",
        english = "milk",
        partOfSpeech = "noun",
        subject = SubjectFilter.Foods,
        categoryLabel = "Foods",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Totôsâpoy minikwê. (Drink the milk.)",
        relatedWordIds = listOf("bread", "tea", "water", "eat", "berry"),
        morphology = "Root form: totôsâpoy. Drink noun.",
        detailedMorphology = DetailedMorphology(
            stem = "totôsâpoy-",
            stemMeaning = "milk",
            grammaticalForm = "noun • inanimate • singular"
        ),
        icon = "MK"
    ),
    VocabularyWord(
        id = "river",
        cree = "Sîpiy",
        english = "river",
        partOfSpeech = "noun",
        subject = SubjectFilter.Lands,
        categoryLabel = "Lands",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Sîpiy pimohtew. (The river keeps moving.)",
        relatedWordIds = listOf("water", "lake", "fish", "travel", "camp"),
        morphology = "Root form: sîpiy. Simple noun.",
        detailedMorphology = DetailedMorphology(
            stem = "sîpiy-",
            stemMeaning = "river",
            grammaticalForm = "noun • inanimate • singular"
        ),
        icon = "RV"
    ),
    VocabularyWord(
        id = "forest",
        cree = "Paskwâw",
        english = "forest",
        partOfSpeech = "noun",
        subject = SubjectFilter.Lands,
        categoryLabel = "Lands",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Paskwâw nîsoy. (The forest is quiet.)",
        relatedWordIds = listOf("mistik", "camp", "river", "fox", "wolf"),
        morphology = "Root form: paskwâw. Place noun.",
        detailedMorphology = DetailedMorphology(
            stem = "paskwâw-",
            stemMeaning = "forest / wooded place",
            grammaticalForm = "noun • inanimate • singular"
        ),
        icon = "FRS"
    ),
    VocabularyWord(
        id = "house",
        cree = "Wâskahikan",
        english = "house",
        partOfSpeech = "noun",
        subject = SubjectFilter.Lands,
        categoryLabel = "Lands",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Wâskahikan nisto. (The house is nearby.)",
        relatedWordIds = listOf("home", "camp", "road", "friend", "travel"),
        morphology = "Root form: wâskahikan. Place noun.",
        detailedMorphology = DetailedMorphology(
            stem = "wâskahikan-",
            stemMeaning = "house",
            grammaticalForm = "noun • inanimate • singular"
        ),
        icon = "HS"
    ),
    VocabularyWord(
        id = "yes",
        cree = "Êha",
        english = "yes",
        partOfSpeech = "phrase",
        subject = SubjectFilter.Words,
        categoryLabel = "Words",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Êha, nitis. (Yes, friend.)",
        relatedWordIds = listOf("no", "hello", "friend", "thanks", "good"),
        morphology = "Response particle meaning \"yes\".",
        detailedMorphology = DetailedMorphology(
            stem = "êha-",
            stemMeaning = "yes / affirmation",
            grammaticalForm = "phrase • response"
        ),
        icon = "YS"
    ),
    VocabularyWord(
        id = "no",
        cree = "Namôya",
        english = "no",
        partOfSpeech = "phrase",
        subject = SubjectFilter.Words,
        categoryLabel = "Words",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Namôya, namoya nîsta. (No, not me.)",
        relatedWordIds = listOf("yes", "hello", "friend", "thanks", "good"),
        morphology = "Response particle meaning \"no\".",
        detailedMorphology = DetailedMorphology(
            stem = "namôya-",
            stemMeaning = "no / negation",
            grammaticalForm = "phrase • response"
        ),
        icon = "NO"
    ),
    VocabularyWord(
        id = "thanks",
        cree = "Kinanâskomitin",
        english = "thank you",
        partOfSpeech = "phrase",
        subject = SubjectFilter.Words,
        categoryLabel = "Words",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Kinanâskomitin, nitis. (Thank you, friend.)",
        relatedWordIds = listOf("friend", "hello", "good", "yes", "home"),
        morphology = "Polite phrase used to give thanks.",
        detailedMorphology = DetailedMorphology(
            stem = "kinanâskomit-",
            stemMeaning = "thank / be grateful",
            suffix = "-in",
            suffixMeaning = "first person object",
            grammaticalForm = "phrase • politeness"
        ),
        icon = "TY"
    ),
    VocabularyWord(
        id = "big",
        cree = "Mistahi",
        english = "big",
        partOfSpeech = "adjective",
        subject = SubjectFilter.Words,
        categoryLabel = "Words",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Mistahi mistikw. (The tree is big.)",
        relatedWordIds = listOf("mako", "mistik", "house", "head", "good"),
        morphology = "Descriptive adjective meaning \"big\".",
        detailedMorphology = DetailedMorphology(
            stem = "mistahi-",
            stemMeaning = "big / large",
            grammaticalForm = "adjective • descriptive"
        ),
        icon = "BG"
    ),
    VocabularyWord(
        id = "small",
        cree = "Apisîs",
        english = "small",
        partOfSpeech = "adjective",
        subject = SubjectFilter.Words,
        categoryLabel = "Words",
        pronunciationLabel = "Play Audio",
        exampleTitle = "Cree Sentence",
        exampleSentence = "Apisîs wâposis. (The small rabbit is tiny.)",
        relatedWordIds = listOf("waposis", "wapos", "berry", "bird", "good"),
        morphology = "Descriptive adjective meaning \"small\".",
        detailedMorphology = DetailedMorphology(
            stem = "apisîs-",
            stemMeaning = "small / little",
            grammaticalForm = "adjective • descriptive"
        ),
        icon = "SM"
    )
)

fun morphologyForWord(word: VocabularyWord): String = word.morphology.ifBlank { "Morphology not available." }

val suggestedCategories = listOf(
    CategoryCard("Animals", "Common animal words", SubjectFilter.Animals, vocabularyWords.count { it.subject == SubjectFilter.Animals }, "AN"),
    CategoryCard("Body", "Words associated with the body", SubjectFilter.Body, vocabularyWords.count { it.subject == SubjectFilter.Body }, "BD"),
    CategoryCard("Weather", "Learn vocabulary for the weather", SubjectFilter.Weather, vocabularyWords.count { it.subject == SubjectFilter.Weather }, "WE"),
    CategoryCard("Words", "Useful words and phrases", SubjectFilter.Words, vocabularyWords.count { it.subject == SubjectFilter.Words }, "WR"),
    CategoryCard("Foods", "Food and cooking terms", SubjectFilter.Foods, vocabularyWords.count { it.subject == SubjectFilter.Foods }, "FD"),
    CategoryCard("Lands", "Travel and place names", SubjectFilter.Lands, vocabularyWords.count { it.subject == SubjectFilter.Lands }, "LD")
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
    SortOption.AlphabeticalAZ -> words.sortedBy { it.english.lowercase() }
    SortOption.AlphabeticalZA -> words.sortedByDescending { it.english.lowercase() }
}

fun searchWords(
    words: List<VocabularyWord>,
    query: String,
    subject: SubjectFilter,
    wordType: WordTypeFilter,
    sortOption: SortOption
): List<VocabularyWord> {
    val filtered = words.filter { it.matches(query, subject, wordType) }
    return when {
        sortOption == SortOption.Relevance && query.isNotBlank() ->
            filtered.sortedByDescending { it.searchScore(query) }
        sortOption == SortOption.Relevance ->
            sortWords(filtered, SortOption.AlphabeticalAZ)
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
