package com.example.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.Bookmark
import com.example.data.model.HistoryItem
import com.example.data.model.UserScript
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE folder = :folder ORDER BY createdAt DESC")
    fun getBookmarksByFolder(folder: String): Flow<List<Bookmark>>

    @Query("SELECT DISTINCT folder FROM bookmarks ORDER BY folder ASC")
    fun getAllFolders(): Flow<List<String>>

    @Query("SELECT * FROM bookmarks WHERE title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchBookmarks(query: String): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE url = :url LIMIT 1")
    suspend fun getBookmarkByUrl(url: String): Bookmark?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarks(bookmarks: List<Bookmark>)

    @Update
    suspend fun updateBookmark(bookmark: Bookmark)

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)

    @Query("DELETE FROM bookmarks")
    suspend fun clearAllBookmarks()

    @Query("SELECT COUNT(*) FROM bookmarks")
    fun getBookmarkCount(): Flow<Int>
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history WHERE isIncognito = 0 ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryItem>>

    @Query("SELECT * FROM history WHERE isIncognito = 0 AND (title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%') ORDER BY timestamp DESC LIMIT 50")
    fun searchHistory(query: String): Flow<List<HistoryItem>>

    @Query("SELECT * FROM history WHERE isIncognito = 0 ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<HistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryItem): Long

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM history WHERE isIncognito = 0")
    suspend fun clearAllHistory()
}

@Dao
interface UserScriptDao {
    @Query("SELECT * FROM userscripts ORDER BY id ASC")
    fun getAllScripts(): Flow<List<UserScript>>

    @Query("SELECT * FROM userscripts WHERE isEnabled = 1")
    fun getActiveScripts(): Flow<List<UserScript>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: UserScript): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScripts(scripts: List<UserScript>)

    @Update
    suspend fun updateScript(script: UserScript)

    @Delete
    suspend fun deleteScript(script: UserScript)

    @Query("DELETE FROM userscripts WHERE id = :id")
    suspend fun deleteScriptById(id: Long)

    @Query("SELECT COUNT(*) FROM userscripts")
    suspend fun getScriptCount(): Int
}

@Database(
    entities = [Bookmark::class, HistoryItem::class, UserScript::class],
    version = 1,
    exportSchema = false
)
abstract class ChronionDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun userScriptDao(): UserScriptDao

    companion object {
        @Volatile
        private var INSTANCE: ChronionDatabase? = null

        fun getDatabase(context: Context): ChronionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChronionDatabase::class.java,
                    "chronion_browser_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
