package com.example.data.model

import java.util.UUID

enum class SearchCategory(val id: String, val label: String, val iconName: String) {
    ALL("all", "Todo", "Search"),
    NEWS("news", "Noticias", "Newspaper"),
    IMAGES("images", "Imágenes", "Image"),
    VIDEOS("videos", "Videos", "Movie"),
    SCHOLAR("scholar", "Académico", "School"),
    WIKIPEDIA("wikipedia", "Wikipedia", "MenuBook"),
    DEV("dev", "Código / Dev", "Code"),
    REDDIT("reddit", "Foros / Reddit", "Forum")
}

enum class SearchEngine(
    val id: String,
    val displayName: String,
    val searchUrlPattern: String,
    val suggestUrlPattern: String,
    val iconDomain: String
) {
    GOOGLE(
        id = "google",
        displayName = "Google",
        searchUrlPattern = "https://www.google.com/search?q=",
        suggestUrlPattern = "https://suggestqueries.google.com/complete/search?client=chrome&q=",
        iconDomain = "google.com"
    ),
    DUCKDUCKGO(
        id = "duckduckgo",
        displayName = "DuckDuckGo (Privado)",
        searchUrlPattern = "https://duckduckgo.com/?q=",
        suggestUrlPattern = "https://duckduckgo.com/ac/?q=",
        iconDomain = "duckduckgo.com"
    ),
    BRAVE(
        id = "brave",
        displayName = "Brave Search",
        searchUrlPattern = "https://search.brave.com/search?q=",
        suggestUrlPattern = "https://search.brave.com/api/suggest?q=",
        iconDomain = "brave.com"
    ),
    BING(
        id = "bing",
        displayName = "Microsoft Bing",
        searchUrlPattern = "https://www.bing.com/search?q=",
        suggestUrlPattern = "https://api.bing.com/osjson.aspx?query=",
        iconDomain = "bing.com"
    ),
    ECOSIA(
        id = "ecosia",
        displayName = "Ecosia",
        searchUrlPattern = "https://www.ecosia.org/search?q=",
        suggestUrlPattern = "https://ac.ecosia.org/autocomplete?q=",
        iconDomain = "ecosia.org"
    ),
    STARTPAGE(
        id = "startpage",
        displayName = "Startpage",
        searchUrlPattern = "https://www.startpage.com/sp/search?query=",
        suggestUrlPattern = "",
        iconDomain = "startpage.com"
    );

    fun buildQueryUrl(query: String, category: SearchCategory = SearchCategory.ALL): String {
        val encoded = java.net.URLEncoder.encode(query, "UTF-8")
        return when (category) {
            SearchCategory.ALL -> "$searchUrlPattern$encoded"
            SearchCategory.NEWS -> when (this) {
                GOOGLE -> "https://www.google.com/search?tbm=nws&q=$encoded"
                BING -> "https://www.bing.com/news/search?q=$encoded"
                DUCKDUCKGO -> "https://duckduckgo.com/?iar=news&ia=news&q=$encoded"
                else -> "$searchUrlPattern$encoded+noticias"
            }
            SearchCategory.IMAGES -> when (this) {
                GOOGLE -> "https://www.google.com/search?tbm=isch&q=$encoded"
                BING -> "https://www.bing.com/images/search?q=$encoded"
                DUCKDUCKGO -> "https://duckduckgo.com/?iar=images&ia=images&q=$encoded"
                else -> "$searchUrlPattern$encoded+imagenes"
            }
            SearchCategory.VIDEOS -> when (this) {
                GOOGLE -> "https://www.google.com/search?tbm=vid&q=$encoded"
                BING -> "https://www.bing.com/videos/search?q=$encoded"
                DUCKDUCKGO -> "https://duckduckgo.com/?iar=videos&ia=videos&q=$encoded"
                else -> "https://www.youtube.com/results?search_query=$encoded"
            }
            SearchCategory.SCHOLAR -> "https://scholar.google.com/scholar?q=$encoded"
            SearchCategory.WIKIPEDIA -> "https://es.wikipedia.org/wiki/Special:Search?search=$encoded"
            SearchCategory.DEV -> "https://github.com/search?q=$encoded"
            SearchCategory.REDDIT -> "https://www.reddit.com/search/?q=$encoded"
        }
    }
}

enum class DarkThemeStyle(val id: String, val title: String, val description: String) {
    COSMIC_INDIGO("cosmic_indigo", "Chronioñ Cósmico", "Oscuro profundo con acentos violeta y cian brillante"),
    OLED_PITCH_BLACK("oled_black", "OLED Puro (Pitch Black)", "Negro 100% absoluto para ahorrar batería y máximo contraste"),
    WARM_SEPIA("warm_sepia", "Modo Sepia & Lectura", "Tono cálido relajante ideal para lectura nocturna prolongada"),
    CYBER_EMERALD("cyber_emerald", "Cyber Verde Hacker", "Fondo oscuro mate con toques de esmeralda tecnológico"),
    DYNAMIC_SYSTEM("dynamic_system", "Automático del Sistema", "Se adapta automáticamente al tema claro/oscuro de Android")
}

data class TabItem(
    val id: String = UUID.randomUUID().toString(),
    val url: String = "chronion://newtab",
    val title: String = "Nueva Pestaña",
    val isIncognito: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val favicon: String? = null,
    val blockedAdsCount: Int = 0,
    val blockedTrackersCount: Int = 0,
    val isDesktopMode: Boolean = false,
    val isReaderMode: Boolean = false,
    val isFindInPageActive: Boolean = false,
    val searchQuery: String = "",
    val findQuery: String = "",
    val findMatchesCount: Int = 0,
    val findActiveMatchOrdinal: Int = 0
)

data class SpeedDialItem(
    val title: String,
    val url: String,
    val iconDomain: String,
    val category: String = "Popular"
)

data class CloudSyncAccount(
    val email: String = "anyelo888ra@gmail.com",
    val displayName: String = "Anyelo (Chronioñ Cloud)",
    val isConnected: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val totalSyncedBookmarks: Int = 0,
    val isAutoSyncEnabled: Boolean = true
)

enum class DownloadStatus {
    DOWNLOADING, COMPLETED, PAUSED, FAILED
}

enum class DownloadCategory(val label: String) {
    ALL("Todos"),
    DOCUMENTS("Documentos"),
    IMAGES("Imágenes"),
    MEDIA("Multimedia"),
    APKS("APKs / Apps")
}

data class DownloadItem(
    val id: String = UUID.randomUUID().toString(),
    val fileName: String,
    val url: String,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val progressPercent: Int = if (totalBytes > 0) ((downloadedBytes * 100) / totalBytes).toInt() else 0,
    val status: DownloadStatus = DownloadStatus.COMPLETED,
    val timestamp: Long = System.currentTimeMillis(),
    val mimeType: String = "application/octet-stream",
    val category: DownloadCategory = DownloadCategory.ALL
)

enum class ReaderTheme(val title: String, val bgHex: Long, val textHex: Long) {
    PAPER_WHITE("Papel Blanco", 0xFFFFFFFF, 0xFF1E293B),
    SEPIA_VINTAGE("Sepia Relajante", 0xFFFBF0D9, 0xFF3D2C1D),
    SLATE_DARK("Gris Pizarra", 0xFF1E293B, 0xFFF1F5F9),
    AMOLED_BLACK("OLED Negro Puro", 0xFF000000, 0xFFE2E8F0)
}

enum class ReaderFont(val title: String) {
    SANS("Sans-Serif Moderno"),
    SERIF("Serif Editorial"),
    MONO("Monoespaciado Dev")
}

data class ReaderArticle(
    val title: String,
    val domain: String,
    val author: String = "Chronioñ Extractor",
    val publishDate: String = "Hoy",
    val readingTimeMinutes: Int = 3,
    val wordCount: Int = 650,
    val paragraphs: List<String> = emptyList()
)

data class DevConsoleMessage(
    val id: String = UUID.randomUUID().toString(),
    val type: String = "LOG", // LOG, WARN, ERROR, RESULT, INFO
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class TargetLanguage(val code: String, val name: String, val flag: String)

