package com.example.myvocabulary

import android.content.Context
import androidx.room.Entity
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary_words ORDER BY english COLLATE NOCASE ASC, cree COLLATE NOCASE ASC")
    fun observeAllWords(): Flow<List<VocabularyWord>>

    @Query("SELECT * FROM vocabulary_words ORDER BY english COLLATE NOCASE ASC, cree COLLATE NOCASE ASC")
    suspend fun getAllWords(): List<VocabularyWord>

    @Query("SELECT * FROM vocabulary_words WHERE id = :id LIMIT 1")
    suspend fun getWordById(id: String): VocabularyWord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(words: List<VocabularyWord>)

    @Query("DELETE FROM vocabulary_words")
    suspend fun clearAll()
}

@Entity(tableName = "dictionary_sync_state")
data class DictionarySyncState(
    @PrimaryKey val name: String,
    val isComplete: Boolean,
    val lastSyncedAtMillis: Long,
    val syncedWords: Int,
    val totalWords: Int
)

@Dao
interface DictionarySyncStateDao {
    @Query("SELECT * FROM dictionary_sync_state WHERE name = :name LIMIT 1")
    suspend fun getState(name: String): DictionarySyncState?

    @Query("SELECT * FROM dictionary_sync_state WHERE name = :name LIMIT 1")
    fun observeState(name: String): Flow<DictionarySyncState?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: DictionarySyncState)
}

@Database(
    entities = [VocabularyWord::class, DictionarySyncState::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(VocabularyConverters::class)
abstract class VocabularyDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun syncStateDao(): DictionarySyncStateDao

    companion object {
        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        fun getInstance(context: Context): VocabularyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    VocabularyDatabase::class.java,
                    "my_vocabulary.db"
                ).fallbackToDestructiveMigration(true).build().also { INSTANCE = it }
            }
        }
    }
}

class VocabularyConverters {
    private val morphologyRegex = Regex(
        "\"(stem|stemMeaning|suffix|suffixMeaning|grammaticalForm)\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\""
    )

    @TypeConverter
    fun subjectToString(subject: SubjectFilter): String = subject.name

    @TypeConverter
    fun stringToSubject(value: String): SubjectFilter {
        return runCatching { SubjectFilter.valueOf(value) }.getOrElse { SubjectFilter.All }
    }

    @TypeConverter
    fun listToJson(value: List<String>): String = JSONArray(value).toString()

    @TypeConverter
    fun jsonToList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(value)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optString(index).trim()
                    if (item.isNotBlank()) {
                        add(item)
                    }
                }
            }
        }.getOrElse { emptyList() }
    }

    @TypeConverter
    fun morphologyToJson(value: DetailedMorphology): String = JSONObject().apply {
        put("stem", value.stem)
        put("stemMeaning", value.stemMeaning)
        put("suffix", value.suffix)
        put("suffixMeaning", value.suffixMeaning)
        put("grammaticalForm", value.grammaticalForm)
    }.toString()

    @TypeConverter
    fun jsonToMorphology(value: String): DetailedMorphology {
        if (value.isBlank()) return DetailedMorphology()
        return runCatching {
            val json = JSONObject(value)
            DetailedMorphology(
                stem = json.optString("stem"),
                stemMeaning = json.optString("stemMeaning"),
                suffix = json.optString("suffix"),
                suffixMeaning = json.optString("suffixMeaning"),
                grammaticalForm = json.optString("grammaticalForm")
            )
        }.getOrElse {
            parseMorphologyFallback(value)
        }
    }

    private fun parseMorphologyFallback(value: String): DetailedMorphology {
        val matches = morphologyRegex.findAll(value).associate { match ->
            match.groupValues[1] to match.groupValues[2]
        }
        if (matches.isEmpty()) return DetailedMorphology()
        return DetailedMorphology(
            stem = matches["stem"].orEmpty().unescapeJsonText(),
            stemMeaning = matches["stemMeaning"].orEmpty().unescapeJsonText(),
            suffix = matches["suffix"].orEmpty().unescapeJsonText(),
            suffixMeaning = matches["suffixMeaning"].orEmpty().unescapeJsonText(),
            grammaticalForm = matches["grammaticalForm"].orEmpty().unescapeJsonText()
        )
    }

    private fun String.unescapeJsonText(): String {
        return replace("\\\\", "\\")
            .replace("\\\"", "\"")
            .replace("\\/", "/")
            .replace("\\b", "\b")
            .replace("\\f", "\u000C")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
    }
}
