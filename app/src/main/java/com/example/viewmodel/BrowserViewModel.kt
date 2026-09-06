package com.example.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Enums y Clases de Modelo necesarias en el ViewModel
enum class DownloadCategory { ALL, APKS, DOCUMENTS, IMAGES, MEDIA }
enum class DownloadStatus { DOWNLOADING, COMPLETED, FAILED, PAUSED }
enum class TargetLanguage { SPANISH, ENGLISH, FRENCH, GERMAN, CHINESE }
enum class DarkThemeStyle { SYSTEM, COSMIC_INDIGO, OLED_BLACK }
enum class SearchEngine { GOOGLE, DUCKDUCKGO, BING } {
    fun buildQueryUrl(query: String, category: SearchCategory): String {
        return "https://www.google.com/search?q=${Uri.encode(query)}"
    }
}
enum class SearchCategory { ALL, IMAGES, NEWS, VIDEOS }
enum class ReaderTheme { SYSTEM, LIGHT, SEPIA, DARK }
enum class ReaderFont { SANS_SERIF, SERIF, MONOSPACE }

data class DownloadItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val fileName: String,
    val url: String,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val status: DownloadStatus,
    val category: DownloadCategory
)

data class DevConsoleMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String,
    val message: String
)

data class SpeedDialItem(
    val title: String,
    val url: String,
    val iconDomain: String,
    val category: String
)

data class TabItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val url: String = "chronion://newtab",
    val title: String = "Nueva Pestaña",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val isIncognito: Boolean = false,
    val isDesktopMode: Boolean = false,
    val isReaderMode: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val favicon: String? = null,
    val blockedAdsCount: Int = 0,
    val blockedTrackersCount: Int = 0,
    val isFindInPageActive: Boolean = false,
    val findQuery: String = "",
    val findMatchesCount: Int = 0,
    val findActiveMatchOrdinal: Int = 0
)

data class ReaderArticle(
    val title: String,
    val domain: String,
    val author: String,
    val publishDate: String,
    val readingTimeMinutes: Int,
    val wordCount: Int,
    val paragraphs: List<String>
)

data class CloudSyncAccount(val email: String = "", val isSynced: Boolean = false)
data class Bookmark(val id: Long = 0, val title: String, val url: String, val folder: String = "", val tags: String = "")
data class HistoryItem(val id: Long = 0, val title: String, val url: String, val timestamp: Long = System.currentTimeMillis())
data class UserScript(val id: Long = 0, val name: String, val script: String, val isEnabled: Boolean = true)

// Interfaces simuladas para dependencias de repositorio y DB
interface BrowserRepository {
    val allBookmarks: StateFlow<List<Bookmark>>
    val allHistory: StateFlow<List<HistoryItem>>
    val allScripts: StateFlow<List<UserScript>>
    val activeScripts: StateFlow<List<UserScript>>
    suspend fun initDefaultDataIfNeeded()
    suspend fun addHistory(title: String, url: String, isIncognito: Boolean)
    suspend fun clearHistory()
    suspend fun deleteHistoryById(id: Long)
    suspend fun deleteHistoryBetween(startTime: Long, endTime: Long)
    suspend fun deleteHistoryOlderThan(timestamp: Long)
    suspend fun isBookmarked(url: String): Boolean
    suspend fun addBookmark(title: String, url: String, folder: String = "Favoritos", tags: String = "")
    suspend fun deleteBookmark(bookmark: Bookmark)
    suspend fun deleteBookmarksBetween(startTime: Long, endTime: Long)
    suspend fun syncBookmarksWithCloud(account: CloudSyncAccount): CloudSyncAccount
    suspend fun toggleScript(script: UserScript)
    suspend fun saveScript(script: UserScript)
    suspend fun deleteScript(script: UserScript)
}

interface SyncRepository {
    suspend fun exportBookmarksToJson(): String
    suspend fun importBookmarksFromJson(json: String): Result<Unit>
    suspend fun syncWithGoogleAccount(token: String): Result<CloudSyncAccount>
}

interface ExtensionManager {
    fun toggleScriptForDomain(scriptId: Long, url: String)
}

interface AppDatabase {
    fun vacuum()
}

object AdBlockEngine {
    fun isTracker(url: String): Boolean = url.contains("tracker") || url.contains("analytics")
}

// ViewModel Principal
class BrowserViewModel(
    private val repository: BrowserRepository,
    private val syncRepository: SyncRepository,
    private val extensionManager: ExtensionManager,
    private val db: AppDatabase
) : ViewModel() {

    // Tab Management State
    private val _tabs = MutableStateFlow<List<TabItem>>(listOf(TabItem()))
    val tabs: StateFlow<List<TabItem>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow(_tabs.value.first().id)
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    // UI Dialogs & Sheets Visibility State
    private val _isTabSwitcherVisible = MutableStateFlow(false)
    val isTabSwitcherVisible: StateFlow<Boolean> = _isTabSwitcherVisible.asStateFlow()

    private val _isBookmarksSheetVisible = MutableStateFlow(false)
    val isBookmarksSheetVisible: StateFlow<Boolean> = _isBookmarksSheetVisible.asStateFlow()

    private val _isHistorySheetVisible = MutableStateFlow(false)
    val isHistorySheetVisible: StateFlow<Boolean> = _isHistorySheetVisible.asStateFlow()

    private val _isExtensionsSheetVisible = MutableStateFlow(false)
    val isExtensionsSheetVisible: StateFlow<Boolean> = _isExtensionsSheetVisible.asStateFlow()

    private val _isSettingsSheetVisible = MutableStateFlow(false)
    val isSettingsSheetVisible: StateFlow<Boolean> = _isSettingsSheetVisible.asStateFlow()

    private val _isShieldDialogVisible = MutableStateFlow(false)
    val isShieldDialogVisible: StateFlow<Boolean> = _isShieldDialogVisible.asStateFlow()

    private val _isAdvancedSearchSheetVisible = MutableStateFlow(false)
    val isAdvancedSearchSheetVisible: StateFlow<Boolean> = _isAdvancedSearchSheetVisible.asStateFlow()

    private val _isDownloadsSheetVisible = MutableStateFlow(false)
    val isDownloadsSheetVisible: StateFlow<Boolean> = _isDownloadsSheetVisible.asStateFlow()

    private val _isDevToolsSheetVisible = MutableStateFlow(false)
    val isDevToolsSheetVisible: StateFlow<Boolean> = _isDevToolsSheetVisible.asStateFlow()

    private val _isTranslateSheetVisible = MutableStateFlow(false)
    val isTranslateSheetVisible: StateFlow<Boolean> = _isTranslateSheetVisible.asStateFlow()

    // Reader Mode State
    private val _readerTheme = MutableStateFlow(ReaderTheme.SYSTEM)
    val readerTheme: StateFlow<ReaderTheme> = _readerTheme.asStateFlow()

    private val _readerFont = MutableStateFlow(ReaderFont.SANS_SERIF)
    val readerFont: StateFlow<ReaderFont> = _readerFont.asStateFlow()

    private val _readerFontSize = MutableStateFlow(16)
    val readerFontSize: StateFlow<Int> = _readerFontSize.asStateFlow()

    private val _readerArticle = MutableStateFlow<ReaderArticle?>(null)
    val readerArticle: StateFlow<ReaderArticle?> = _readerArticle.asStateFlow()

    // Downloads State
    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    private val _selectedDownloadCategory = MutableStateFlow(DownloadCategory.ALL)
    val selectedDownloadCategory: StateFlow<DownloadCategory> = _selectedDownloadCategory.asStateFlow()

    // DevTools & Console State
    private val _devConsoleMessages = MutableStateFlow<List<DevConsoleMessage>>(
        listOf(DevConsoleMessage(type = "INFO", message = "Consola inicializada."))
    )
    val devConsoleMessages: StateFlow<List<DevConsoleMessage>> = _devConsoleMessages.asStateFlow()

    // Translation State
    private val _targetLanguage = MutableStateFlow(TargetLanguage.SPANISH)
    val targetLanguage: StateFlow<TargetLanguage> = _targetLanguage.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    private val _isPageTranslated = MutableStateFlow(false)
    val isPageTranslated: StateFlow<Boolean> = _isPageTranslated.asStateFlow()

    // Custom Speed Dial Shortcuts
    private val _customSpeedDialItems = MutableStateFlow<List<SpeedDialItem>>(
        listOf(
            SpeedDialItem("Google", "https://google.com", "google.com", "Buscadores"),
            SpeedDialItem("GitHub", "https://github.com", "github.com", "Desarrollo"),
            SpeedDialItem("YouTube", "https://youtube.com", "youtube.com", "Media"),
            SpeedDialItem("Wikipedia", "https://wikipedia.org", "wikipedia.org", "Educación"),
            SpeedDialItem("Reddit", "https://reddit.com", "reddit.com", "Comunidad"),
            SpeedDialItem("X / Twitter", "https://x.com", "x.com", "Social"),
            SpeedDialItem("DuckDuckGo", "https://duckduckgo.com", "duckduckgo.com", "Privacidad"),
            SpeedDialItem("Noticias", "https://google.com", "google.com", "Noticias")
        )
    )
    val customSpeedDialItems: StateFlow<List<SpeedDialItem>> = _customSpeedDialItems.asStateFlow()

    // Settings & Customization
    private val _darkThemeStyle = MutableStateFlow(DarkThemeStyle.COSMIC_INDIGO)
    val darkThemeStyle: StateFlow<DarkThemeStyle> = _darkThemeStyle.asStateFlow()

    private val _isEyeCareEnabled = MutableStateFlow(false)
    val isEyeCareEnabled: StateFlow<Boolean> = _isEyeCareEnabled.asStateFlow()

    private val _eyeCareWarmth = MutableStateFlow(0.35f)
    val eyeCareWarmth: StateFlow<Float> = _eyeCareWarmth.asStateFlow()

    private val _isAdBlockEnabled = MutableStateFlow(true)
    val isAdBlockEnabled: StateFlow<Boolean> = _isAdBlockEnabled.asStateFlow()

    private val _blockTrackers = MutableStateFlow(true)
    val blockTrackers: StateFlow<Boolean> = _blockTrackers.asStateFlow()

    private val _blockPopups = MutableStateFlow(true)
    val blockPopups: StateFlow<Boolean> = _blockPopups.asStateFlow()

    private val _forceWebDarkMode = MutableStateFlow(false)
    val forceWebDarkMode: StateFlow<Boolean> = _forceWebDarkMode.asStateFlow()

    private val _searchEngine = MutableStateFlow(SearchEngine.GOOGLE)
    val searchEngine: StateFlow<SearchEngine> = _searchEngine.asStateFlow()

    private val _searchCategory = MutableStateFlow(SearchCategory.ALL)
    val searchCategory: StateFlow<SearchCategory> = _searchCategory.asStateFlow()

    private val _whitelistedDomains = MutableStateFlow<Set<String>>(emptySet())
    val whitelistedDomains: StateFlow<Set<String>> = _whitelistedDomains.asStateFlow()

    // AdBlock Stats
    private val _totalAdsBlocked = MutableStateFlow(0)
    val totalAdsBlocked: StateFlow<Int> = _totalAdsBlocked.asStateFlow()

    private val _totalTrackersBlocked = MutableStateFlow(0)
    val totalTrackersBlocked: StateFlow<Int> = _totalTrackersBlocked.asStateFlow()

    // Cloud Sync Account
    private val _cloudAccount = MutableStateFlow(CloudSyncAccount())
    val cloudAccount: StateFlow<CloudSyncAccount> = _cloudAccount.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // Data from Database
    val bookmarks: StateFlow<List<Bookmark>> = repository.allBookmarks
    val history: StateFlow<List<HistoryItem>> = repository.allHistory
    val userScripts: StateFlow<List<UserScript>> = repository.allScripts
    val activeScripts: StateFlow<List<UserScript>> = repository.activeScripts

    val speedDialItems = listOf(
        SpeedDialItem("Google", "google.com", "google.com", "Buscadores"),
        SpeedDialItem("GitHub", "github.com", "github.com", "Desarrollo"),
        SpeedDialItem("YouTube", "youtube.com", "youtube.com", "Media"),
        SpeedDialItem("Wikipedia", "wikipedia.org", "wikipedia.org", "Educación"),
        SpeedDialItem("Reddit", "reddit.com", "reddit.com", "Comunidad"),
        SpeedDialItem("X / Twitter", "https://x.com", "x.com", "Social"),
        SpeedDialItem("DuckDuckGo", "https://duckduckgo.com", "duckduckgo.com", "Privacidad"),
        SpeedDialItem("Noticias", "google.com", "google.com", "Noticias")
    )

    init {
        viewModelScope.launch {
            repository.initDefaultDataIfNeeded()
        }
    }

    // Active Tab Helper
    val currentTab: TabItem
        get() = _tabs.value.find { it.id == _activeTabId.value } ?: _tabs.value.first()

    // Tab Management Actions
    fun createNewTab(url: String = "chronion://newtab", isIncognito: Boolean = false) {
        val newTab = TabItem(
            url = url,
            title = if (url == "chronion://newtab") "Nueva Pestaña" else url,
            isIncognito = isIncognito
        )
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = newTab.id
        _isTabSwitcherVisible.value = false
    }

    fun selectTab(id: String) {
        _activeTabId.value = id
        _isTabSwitcherVisible.value = false
    }

    fun closeTab(id: String) {
        val currentList = _tabs.value
        if (currentList.size <= 1) {
            val freshTab = TabItem()
            _tabs.value = listOf(freshTab)
            _activeTabId.value = freshTab.id
            return
        }
        val closingIndex = currentList.indexOfFirst { it.id == id }
        val updated = currentList.filter { it.id != id }
        _tabs.value = updated
        if (_activeTabId.value == id) {
            val newIndex = if (closingIndex >= updated.size) updated.size - 1 else closingIndex
            _activeTabId.value = updated[newIndex].id
        }
    }

    fun closeAllIncognitoTabs() {
        val regularTabs = _tabs.value.filter { !it.isIncognito }
        if (regularTabs.isEmpty()) {
            val freshTab = TabItem(isIncognito = false)
            _tabs.value = listOf(freshTab)
            _activeTabId.value = freshTab.id
        } else {
            _tabs.value = regularTabs
            if (currentTab.isIncognito) {
                _activeTabId.value = regularTabs.first().id
            }
        }
    }

    fun switchToNextTab() {
        val list = _tabs.value
        if (list.size <= 1) return
        val currentIndex = list.indexOfFirst { it.id == _activeTabId.value }
        val nextIndex = (currentIndex + 1) % list.size
        _activeTabId.value = list[nextIndex].id
    }

    fun switchToPrevTab() {
        val list = _tabs.value
        if (list.size <= 1) return
        val currentIndex = list.indexOfFirst { it.id == _activeTabId.value }
        val prevIndex = if (currentIndex - 1 < 0) list.size - 1 else currentIndex - 1
        _activeTabId.value = list[prevIndex].id
    }

    fun toggleIncognitoMode() {
        val current = currentTab
        if (current.isIncognito) {
            val regularTab = _tabs.value.find { !it.isIncognito }
            if (regularTab != null) {
                selectTab(regularTab.id)
            } else {
                createNewTab(isIncognito = false)
            }
        } else {
            val incognitoTab = _tabs.value.find { it.isIncognito }
            if (incognitoTab != null) {
                selectTab(incognitoTab.id)
            } else {
                createNewTab(isIncognito = true)
            }
        }
    }

    // Navigation & URL Handling
    fun navigateTo(rawInput: String, category: SearchCategory = _searchCategory.value) {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return
        val targetUrl = when {
            trimmed.startsWith("chronion://") -> trimmed
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> _searchEngine.value.buildQueryUrl(trimmed, category)
        }
        updateCurrentTab {
            it.copy(
                url = targetUrl,
                title = if (targetUrl.startsWith("chronion://")) "Nueva Pestaña" else targetUrl,
                searchQuery = if (targetUrl.startsWith("http")) "" else trimmed,
                isLoading = true,
                progress = 10,
                blockedAdsCount = 0,
                blockedTrackersCount = 0
            )
        }
    }

    fun updateCurrentTab(transform: (TabItem) -> TabItem) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == _activeTabId.value) transform(tab) else tab
        }
    }

    fun updateTabProgress(progress: Int) {
        updateCurrentTab { it.copy(progress = progress, isLoading = progress < 100) }
    }

    fun updateTabLoaded(title: String, url: String, canBack: Boolean, canForward: Boolean, favicon: String?) {
        val tab = currentTab
        updateCurrentTab {
            it.copy(
                title = title.ifBlank { it.title },
                url = url,
                canGoBack = canBack,
                canGoForward = canForward,
                favicon = favicon ?: it.favicon,
                isLoading = false,
                progress = 100
            )
        }
        if (!tab.isIncognito && !url.startsWith("chronion://")) {
            viewModelScope.launch {
                repository.addHistory(title, url, isIncognito = false)
            }
        }
    }

    // AdBlock & Tracker recording
    fun recordBlockedItem(url: String) {
        val isTracker = AdBlockEngine.isTracker(url)
        if (isTracker) {
            _totalTrackersBlocked.value += 1
            updateCurrentTab { it.copy(blockedTrackersCount = it.blockedTrackersCount + 1) }
        } else {
            _totalAdsBlocked.value += 1
            updateCurrentTab { it.copy(blockedAdsCount = it.blockedAdsCount + 1) }
        }
    }

    fun toggleAdBlockForCurrentDomain() {
        val host = Uri.parse(currentTab.url).host ?: return
        val currentSet = _whitelistedDomains.value.toMutableSet()
        if (currentSet.contains(host)) {
            currentSet.remove(host)
        } else {
            currentSet.add(host)
        }
        _whitelistedDomains.value = currentSet
    }

    fun isCurrentDomainWhitelisted(): Boolean {
        val host = Uri.parse(currentTab.url).host ?: return false
        return _whitelistedDomains.value.contains(host)
    }

    // Bookmarks & Cloud Sync
    fun toggleBookmarkCurrentPage() {
        val tab = currentTab
        if (tab.url.startsWith("chronion://")) return
        viewModelScope.launch {
            val isBookmarked = repository.isBookmarked(tab.url)
            if (isBookmarked) {
                val list = bookmarks.value
                val existing = list.find { it.url == tab.url }
                if (existing != null) repository.deleteBookmark(existing)
            } else {
                repository.addBookmark(tab.title, tab.url, folder = "Favoritos")
            }
        }
    }

    fun isCurrentPageBookmarked(): Boolean {
        val tab = currentTab
        return bookmarks.value.any { it.url == tab.url }
    }

    fun addBookmark(title: String, url: String, folder: String, tags: String) {
        viewModelScope.launch {
            repository.addBookmark(title, url, folder, tags)
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmark)
        }
    }

    fun syncWithCloud() {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1200)
            val updated = repository.syncBookmarksWithCloud(_cloudAccount.value)
            _cloudAccount.value = updated
            _isSyncing.value = false
        }
    }

    // UserScript / Extension operations
    fun toggleScript(script: UserScript) {
        viewModelScope.launch {
            repository.toggleScript(script)
        }
    }

    fun saveScript(script: UserScript) {
        viewModelScope.launch {
            repository.saveScript(script)
        }
    }

    fun deleteScript(script: UserScript) {
        viewModelScope.launch {
            repository.deleteScript(script)
        }
    }

    // In-page Find
    fun setFindInPage(active: Boolean, query: String = "") {
        updateCurrentTab {
            it.copy(
                isFindInPageActive = active,
                findQuery = query,
                findMatchesCount = if (active) it.findMatchesCount else 0
            )
        }
    }

    fun updateFindResults(matches: Int, activeOrdinal: Int) {
        updateCurrentTab {
            it.copy(findMatchesCount = matches, findActiveMatchOrdinal = activeOrdinal)
        }
    }

    // UI Dialog Toggles
    fun setTabSwitcherVisible(visible: Boolean) { _isTabSwitcherVisible.value = visible }
    fun setBookmarksSheetVisible(visible: Boolean) { _isBookmarksSheetVisible.value = visible }
    fun setHistorySheetVisible(visible: Boolean) { _isHistorySheetVisible.value = visible }
    fun setExtensionsSheetVisible(visible: Boolean) { _isExtensionsSheetVisible.value = visible }
    fun setSettingsSheetVisible(visible: Boolean) { _isSettingsSheetVisible.value = visible }
    fun setShieldDialogVisible(visible: Boolean) { _isShieldDialogVisible.value = visible }
    fun setAdvancedSearchSheetVisible(visible: Boolean) { _isAdvancedSearchSheetVisible.value = visible }
    fun setDownloadsSheetVisible(visible: Boolean) { _isDownloadsSheetVisible.value = visible }
    fun setDevToolsSheetVisible(visible: Boolean) { _isDevToolsSheetVisible.value = visible }
    fun setTranslateSheetVisible(visible: Boolean) { _isTranslateSheetVisible.value = visible }

    // Reader Mode controls
    fun setReaderTheme(theme: ReaderTheme) { _readerTheme.value = theme }
    fun setReaderFont(font: ReaderFont) { _readerFont.value = font }
    fun setReaderFontSize(size: Int) { _readerFontSize.value = size.coerceIn(12, 32) }

    fun toggleReaderMode() {
        val tab = currentTab
        val willBeReader = !tab.isReaderMode
        if (willBeReader) {
            extractReaderArticle(tab)
        }
        updateCurrentTab { it.copy(isReaderMode = willBeReader) }
    }

    private fun extractReaderArticle(tab: TabItem) {
        val domain = Uri.parse(tab.url).host ?: "web"
        val paragraphs = listOf(
            "El modo de lectura limpia el ruido visual y la publicidad.",
            "Optimiza el consumo de recursos al aislar el contenido principal.",
            "Personaliza tipografía y esquemas cromáticos.",
            "Mejora la velocidad de lectura gestionando el tiempo en navegación."
        )
        _readerArticle.value = ReaderArticle(
            title = if (tab.title.isNotBlank() && tab.title != "Nueva Pestaña") tab.title else "Artículo - $domain",
            domain = domain,
            author = "Chronioñ Reader Engine",
            publishDate = "Hoy",
            readingTimeMinutes = 2,
            wordCount = 280,
            paragraphs = paragraphs
        )
    }

    // Downloads actions
    fun setDownloadCategory(category: DownloadCategory) {
        _selectedDownloadCategory.value = category
    }

    fun startDownload(downloadUrl: String, customFileName: String? = null) {
        val name = customFileName ?: downloadUrl.substringAfterLast("/").takeIf { it.isNotBlank() && it.contains(".") } ?: "archivo_${System.currentTimeMillis()}.bin"
        val category = when {
            name.endsWith(".apk", true) -> DownloadCategory.APKS
            name.endsWith(".pdf", true) || name.endsWith(".docx", true) || name.endsWith(".txt", true) -> DownloadCategory.DOCUMENTS
            name.endsWith(".png", true) || name.endsWith(".jpg", true) || name.endsWith(".webp", true) -> DownloadCategory.IMAGES
            name.endsWith(".mp4", true) || name.endsWith(".mp3", true) || name.endsWith(".m4a", true) -> DownloadCategory.MEDIA
            else -> DownloadCategory.ALL
        }
        val newDownload = DownloadItem(
            fileName = name,
            url = downloadUrl,
            totalBytes = (1024 * 1024 * (2..25).random()).toLong(),
            downloadedBytes = 0,
            status = DownloadStatus.DOWNLOADING,
            category = category
        )
        _downloads.value = listOf(newDownload) + _downloads.value
        viewModelScope.launch {
            for (p in 1..10) {
                delay(200)
                _downloads.value = _downloads.value.map { item ->
                    if (item.id == newDownload.id) {
                        val downloaded = (item.totalBytes * p) / 10
                        item.copy(
                            downloadedBytes = downloaded,
                            status = if (p == 10) DownloadStatus.COMPLETED else DownloadStatus.DOWNLOADING
                        )
                    } else item
                }
            }
        }
    }

    fun deleteDownload(id: String) {
        _downloads.value = _downloads.value.filterNot { it.id == id }
    }

    fun clearDownloads() {
        _downloads.value = emptyList()
    }

    // DevTools & Console actions
    fun addDevConsoleMessage(type: String, message: String) {
        _devConsoleMessages.value = _devConsoleMessages.value + DevConsoleMessage(
            type = type,
            message = message
        )
    }

    fun clearConsole() {
        _devConsoleMessages.value = listOf(
            DevConsoleMessage(type = "INFO", message = "Consola limpia.")
        )
    }

    fun executeDevScript(code: String) {
        addDevConsoleMessage("INPUT", "> $code")
        val result = try {
            when {
                code.trim() == "document.title" -> "\"${currentTab.title}\""
                code.trim() == "window.location.href" -> "\"${currentTab.url}\""
                code.trim() == "navigator.userAgent" -> "\"${if (currentTab.isDesktopMode) "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" else "Mozilla/5.0 (Android)"}\""
                code.contains("alert") -> "undefined [Alerta mostrada]"
                code.contains("+") || code.contains("*") || code.contains("/") || code.contains("-") -> "42"
                else -> "undefined"
            }
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
        addDevConsoleMessage(if (result.startsWith("Error")) "ERROR" else "RESULT", "<- $result")
    }

    // Translation actions
    fun setTargetLanguage(lang: TargetLanguage) {
        _targetLanguage.value = lang
    }

    fun translateActivePage(lang: TargetLanguage) {
        _targetLanguage.value = lang
        _isTranslating.value = true
        viewModelScope.launch {
            delay(1000)
            _isTranslating.value = false
            _isPageTranslated.value = true
            addDevConsoleMessage("INFO", "Página traducida al ${lang.name} exitosamente.")
        }
    }

    fun restoreOriginalLanguage() {
        _isPageTranslated.value = false
    }

    // Custom Speed Dial
    fun addCustomSpeedDial(title: String, url: String, category: String = "Favoritos") {
        val domain = try { Uri.parse(url).host ?: url } catch (e: Exception) { url }
        val item = SpeedDialItem(title = title, url = url, iconDomain = domain, category = category)
        _customSpeedDialItems.value = _customSpeedDialItems.value + item
    }

    fun removeCustomSpeedDial(item: SpeedDialItem) {
        _customSpeedDialItems.value = _customSpeedDialItems.value.filterNot { it.url == item.url && it.title == item.title }
    }

    // Settings modifiers
    fun setDarkThemeStyle(style: DarkThemeStyle) { _darkThemeStyle.value = style }
    fun setEyeCareEnabled(enabled: Boolean) { _isEyeCareEnabled.value = enabled }
    fun setEyeCareWarmth(warmth: Float) { _eyeCareWarmth.value = warmth }
    fun setAdBlockEnabled(enabled: Boolean) { _isAdBlockEnabled.value = enabled }
    fun setBlockTrackers(enabled: Boolean) { _blockTrackers.value = enabled }
    fun setBlockPopups(enabled: Boolean) { _blockPopups.value = enabled }
    fun setForceWebDarkMode(enabled: Boolean) { _forceWebDarkMode.value = enabled }
    fun setSearchEngine(engine: SearchEngine) { _searchEngine.value = engine }
    fun setSearchCategory(category: SearchCategory) { _searchCategory.value = category }

    fun toggleDesktopMode() {
        updateCurrentTab { it.copy(isDesktopMode = !it.isDesktopMode) }
    }

    fun clearBrowsingHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryById(id)
        }
    }

    fun deleteHistoryBetween(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            repository.deleteHistoryBetween(startTime, endTime)
        }
    }

    fun deleteHistoryOlderThan(timestamp: Long) {
        viewModelScope.launch {
            repository.deleteHistoryOlderThan(timestamp)
        }
    }

    fun deleteBookmarksBetween(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            repository.deleteBookmarksBetween(startTime, endTime)
        }
    }

    // JSON Backup and Sync
    fun exportBookmarksToJson(onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            try {
                val json = syncRepository.exportBookmarksToJson()
                onResult(Result.success(json))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    fun importBookmarksFromJson(json: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = syncRepository.importBookmarksFromJson(json)
            onResult(result)
        }
    }

    fun syncWithGoogleAccount(token: String = "bearer_token", onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            _isSyncing.value = true
            val result = syncRepository.syncWithGoogleAccount(token)
            result.onSuccess { updatedAccount ->
                _cloudAccount.value = updatedAccount
                onComplete?.invoke(true)
            }.onFailure {
                onComplete?.invoke(false)
            }
            _isSyncing.value = false
        }
    }

    // Extensions and DB Optimization
    fun toggleScriptForDomain(scriptId: Long, url: String) {
        extensionManager.toggleScriptForDomain(scriptId, url)
    }

    fun optimizeDatabase(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            db.vacuum()
            withContext(Dispatchers.Main) {
                onComplete?.invoke()
            }
        }
    }
}
