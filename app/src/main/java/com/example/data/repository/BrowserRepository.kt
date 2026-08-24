package com.example.data.repository

import com.example.data.db.BookmarkDao
import com.example.data.db.HistoryDao
import com.example.data.db.UserScriptDao
import com.example.data.model.Bookmark
import com.example.data.model.CloudSyncAccount
import com.example.data.model.ExtensionCatalog
import com.example.data.model.HistoryItem
import com.example.data.model.UserScript
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BrowserRepository(
    private val bookmarkDao: BookmarkDao,
    private val historyDao: HistoryDao,
    private val userScriptDao: UserScriptDao
) {
    val allBookmarks: Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks()
    val allFolders: Flow<List<String>> = bookmarkDao.getAllFolders()
    val allHistory: Flow<List<HistoryItem>> = historyDao.getAllHistory()
    val allScripts: Flow<List<UserScript>> = userScriptDao.getAllScripts()
    val activeScripts: Flow<List<UserScript>> = userScriptDao.getActiveScripts()
    val bookmarkCount: Flow<Int> = bookmarkDao.getBookmarkCount()

    suspend fun initDefaultDataIfNeeded() {
        val scriptCount = userScriptDao.getScriptCount()
        if (scriptCount == 0) {
            userScriptDao.insertScripts(ExtensionCatalog.DEFAULT_SCRIPTS)
        }

        // Check if bookmarks exist, if not seed some helpful bookmarks
        val currentBookmarks = bookmarkDao.getAllBookmarks().first()
        if (currentBookmarks.isEmpty()) {
            val defaults = listOf(
                Bookmark(
                    title = "Chronioñ GitHub Repository",
                    url = "https://github.com/anyelo888ra-ux/chronion-browser",
                    folder = "Desarrollo",
                    tags = "github, chronion, open-source",
                    faviconUrl = "https://github.githubassets.com/favicons/favicon.png",
                    isSynced = true
                ),
                Bookmark(
                    title = "Google",
                    url = "https://www.google.com",
                    folder = "Favoritos",
                    tags = "search, google",
                    faviconUrl = "https://www.google.com/favicon.ico",
                    isSynced = true
                ),
                Bookmark(
                    title = "Wikipedia en Español",
                    url = "https://es.wikipedia.org",
                    folder = "Educación",
                    tags = "wiki, enciclopedia",
                    faviconUrl = "https://es.wikipedia.org/favicon.ico",
                    isSynced = true
                ),
                Bookmark(
                    title = "DuckDuckGo Privacy",
                    url = "https://duckduckgo.com",
                    folder = "Privacidad",
                    tags = "privacidad, buscador",
                    faviconUrl = "https://duckduckgo.com/favicon.ico",
                    isSynced = true
                ),
                Bookmark(
                    title = "Reddit",
                    url = "https://www.reddit.com",
                    folder = "Social",
                    tags = "noticias, foros",
                    faviconUrl = "https://www.redditstatic.com/shreddit/assets/favicon/192x192.png",
                    isSynced = true
                ),
                Bookmark(
                    title = "Android Developers",
                    url = "https://developer.android.com",
                    folder = "Desarrollo",
                    tags = "android, kotlin, dev",
                    faviconUrl = "https://developer.android.com/favicon.ico",
                    isSynced = true
                )
            )
            bookmarkDao.insertBookmarks(defaults)
        }
    }

    // Bookmark operations
    suspend fun addBookmark(title: String, url: String, folder: String = "General", tags: String = "") {
        val bookmark = Bookmark(
            title = title.ifBlank { url },
            url = url,
            folder = folder.ifBlank { "General" },
            tags = tags,
            isSynced = true
        )
        bookmarkDao.insertBookmark(bookmark)
    }

    suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.updateBookmark(bookmark.copy(isSynced = true))
    }

    suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.deleteBookmark(bookmark)
    }

    suspend fun deleteBookmarkById(id: Long) {
        bookmarkDao.deleteBookmarkById(id)
    }

    suspend fun isBookmarked(url: String): Boolean {
        return bookmarkDao.getBookmarkByUrl(url) != null
    }

    fun searchBookmarks(query: String): Flow<List<Bookmark>> {
        return bookmarkDao.searchBookmarks(query)
    }

    fun getBookmarksByFolder(folder: String): Flow<List<Bookmark>> {
        return bookmarkDao.getBookmarksByFolder(folder)
    }

    suspend fun clearBookmarks() {
        bookmarkDao.clearAllBookmarks()
    }

    // History operations
    suspend fun addHistory(title: String, url: String, isIncognito: Boolean) {
        if (isIncognito || url.startsWith("chronion://") || url.isBlank()) return
        val item = HistoryItem(
            title = title.ifBlank { url },
            url = url,
            timestamp = System.currentTimeMillis(),
            isIncognito = false
        )
        historyDao.insertHistory(item)
    }

    suspend fun deleteHistoryById(id: Long) {
        historyDao.deleteHistoryById(id)
    }

    suspend fun clearHistory() {
        historyDao.clearAllHistory()
    }

    fun getRecentHistory(limit: Int = 10): Flow<List<HistoryItem>> {
        return historyDao.getRecentHistory(limit)
    }

    fun searchHistory(query: String): Flow<List<HistoryItem>> {
        return historyDao.searchHistory(query)
    }

    // UserScript operations
    suspend fun toggleScript(script: UserScript) {
        userScriptDao.updateScript(script.copy(isEnabled = !script.isEnabled))
    }

    suspend fun saveScript(script: UserScript) {
        if (script.id == 0L) {
            userScriptDao.insertScript(script)
        } else {
            userScriptDao.updateScript(script)
        }
    }

    suspend fun deleteScript(script: UserScript) {
        userScriptDao.deleteScript(script)
    }

    // Cloud Sync simulation / Google account sync
    suspend fun syncBookmarksWithCloud(account: CloudSyncAccount): CloudSyncAccount {
        // Mark all as synced in local DB
        val bookmarks = bookmarkDao.getAllBookmarks().first()
        val updated = bookmarks.map { it.copy(isSynced = true) }
        bookmarkDao.insertBookmarks(updated)

        return account.copy(
            lastSyncTimestamp = System.currentTimeMillis(),
            totalSyncedBookmarks = bookmarks.size,
            isConnected = true
        )
    }

    // Export/Import bookmarks format
    suspend fun exportBookmarksToJson(): String {
        val bookmarks = bookmarkDao.getAllBookmarks().first()
        val jsonArray = StringBuilder("[\n")
        bookmarks.forEachIndexed { index, b ->
            jsonArray.append("  {\"title\": \"${escapeJson(b.title)}\", \"url\": \"${escapeJson(b.url)}\", \"folder\": \"${escapeJson(b.folder)}\", \"tags\": \"${escapeJson(b.tags)}\"}")
            if (index < bookmarks.size - 1) jsonArray.append(",")
            jsonArray.append("\n")
        }
        jsonArray.append("]")
        return jsonArray.toString()
    }

    private fun escapeJson(str: String): String {
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
    }
}
