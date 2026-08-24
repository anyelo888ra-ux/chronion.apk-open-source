package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AdvancedSearchSheet
import com.example.ui.components.BookmarksSheet
import com.example.ui.components.BottomBrowserBar
import com.example.ui.components.BrowserWebView
import com.example.ui.components.DevToolsSheet
import com.example.ui.components.DownloadManagerSheet
import com.example.ui.components.ExtensionsSheet
import com.example.ui.components.EyeCareOverlay
import com.example.ui.components.FindInPageBar
import com.example.ui.components.HistorySheet
import com.example.ui.components.NewTabPage
import com.example.ui.components.OmniboxAddressBar
import com.example.ui.components.PageInfoAndShieldDialog
import com.example.ui.components.ReaderModeView
import com.example.ui.components.SettingsSheet
import com.example.ui.components.TabSwitcherView
import com.example.ui.components.TranslateBar
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BrowserViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val tabs by viewModel.tabs.collectAsStateWithLifecycle()
            val activeTabId by viewModel.activeTabId.collectAsStateWithLifecycle()
            val activeTab = tabs.find { it.id == activeTabId } ?: tabs.firstOrNull() ?: return@setContent

            val isTabSwitcherVisible by viewModel.isTabSwitcherVisible.collectAsStateWithLifecycle()
            val isBookmarksSheetVisible by viewModel.isBookmarksSheetVisible.collectAsStateWithLifecycle()
            val isHistorySheetVisible by viewModel.isHistorySheetVisible.collectAsStateWithLifecycle()
            val isExtensionsSheetVisible by viewModel.isExtensionsSheetVisible.collectAsStateWithLifecycle()
            val isSettingsSheetVisible by viewModel.isSettingsSheetVisible.collectAsStateWithLifecycle()
            val isShieldDialogVisible by viewModel.isShieldDialogVisible.collectAsStateWithLifecycle()
            val isAdvancedSearchSheetVisible by viewModel.isAdvancedSearchSheetVisible.collectAsStateWithLifecycle()
            val isDownloadsSheetVisible by viewModel.isDownloadsSheetVisible.collectAsStateWithLifecycle()
            val isDevToolsSheetVisible by viewModel.isDevToolsSheetVisible.collectAsStateWithLifecycle()
            val isTranslateSheetVisible by viewModel.isTranslateSheetVisible.collectAsStateWithLifecycle()

            val darkThemeStyle by viewModel.darkThemeStyle.collectAsStateWithLifecycle()
            val isEyeCareEnabled by viewModel.isEyeCareEnabled.collectAsStateWithLifecycle()
            val eyeCareWarmth by viewModel.eyeCareWarmth.collectAsStateWithLifecycle()
            val isAdBlockEnabled by viewModel.isAdBlockEnabled.collectAsStateWithLifecycle()
            val blockTrackers by viewModel.blockTrackers.collectAsStateWithLifecycle()
            val blockPopups by viewModel.blockPopups.collectAsStateWithLifecycle()
            val forceWebDarkMode by viewModel.forceWebDarkMode.collectAsStateWithLifecycle()
            val searchEngine by viewModel.searchEngine.collectAsStateWithLifecycle()
            val searchCategory by viewModel.searchCategory.collectAsStateWithLifecycle()
            val whitelistedDomains by viewModel.whitelistedDomains.collectAsStateWithLifecycle()

            val totalAdsBlocked by viewModel.totalAdsBlocked.collectAsStateWithLifecycle()
            val totalTrackersBlocked by viewModel.totalTrackersBlocked.collectAsStateWithLifecycle()
            val cloudAccount by viewModel.cloudAccount.collectAsStateWithLifecycle()
            val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

            val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
            val history by viewModel.history.collectAsStateWithLifecycle()
            val userScripts by viewModel.userScripts.collectAsStateWithLifecycle()
            val activeScripts by viewModel.activeScripts.collectAsStateWithLifecycle()
            val customSpeedDialItems by viewModel.customSpeedDialItems.collectAsStateWithLifecycle()

            val downloads by viewModel.downloads.collectAsStateWithLifecycle()
            val selectedDownloadCategory by viewModel.selectedDownloadCategory.collectAsStateWithLifecycle()
            val devConsoleMessages by viewModel.devConsoleMessages.collectAsStateWithLifecycle()

            val readerTheme by viewModel.readerTheme.collectAsStateWithLifecycle()
            val readerFont by viewModel.readerFont.collectAsStateWithLifecycle()
            val readerFontSize by viewModel.readerFontSize.collectAsStateWithLifecycle()
            val readerArticle by viewModel.readerArticle.collectAsStateWithLifecycle()

            val targetLanguage by viewModel.targetLanguage.collectAsStateWithLifecycle()
            val isTranslating by viewModel.isTranslating.collectAsStateWithLifecycle()
            val isPageTranslated by viewModel.isPageTranslated.collectAsStateWithLifecycle()

            // Back button handling
            BackHandler {
                when {
                    isTabSwitcherVisible -> viewModel.setTabSwitcherVisible(false)
                    isBookmarksSheetVisible -> viewModel.setBookmarksSheetVisible(false)
                    isHistorySheetVisible -> viewModel.setHistorySheetVisible(false)
                    isExtensionsSheetVisible -> viewModel.setExtensionsSheetVisible(false)
                    isSettingsSheetVisible -> viewModel.setSettingsSheetVisible(false)
                    isAdvancedSearchSheetVisible -> viewModel.setAdvancedSearchSheetVisible(false)
                    isDownloadsSheetVisible -> viewModel.setDownloadsSheetVisible(false)
                    isDevToolsSheetVisible -> viewModel.setDevToolsSheetVisible(false)
                    isTranslateSheetVisible -> viewModel.setTranslateSheetVisible(false)
                    activeTab.isReaderMode -> viewModel.toggleReaderMode()
                    activeTab.isFindInPageActive -> viewModel.setFindInPage(false)
                    activeTab.canGoBack -> {
                        viewModel.navigateTo("chronion://newtab")
                    }
                    activeTab.url != "chronion://newtab" -> {
                        viewModel.navigateTo("chronion://newtab")
                    }
                    else -> {
                        finish()
                    }
                }
            }

            MyApplicationTheme(
                darkThemeStyle = darkThemeStyle,
                isIncognito = activeTab.isIncognito
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if (isTabSwitcherVisible) {
                        TabSwitcherView(
                            tabs = tabs,
                            activeTabId = activeTabId,
                            viewModel = viewModel
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                        ) {
                            // Top Omnibox Address & Search Bar
                            OmniboxAddressBar(
                                tab = activeTab,
                                viewModel = viewModel,
                                selectedCategory = searchCategory,
                                onCategorySelected = { cat -> viewModel.setSearchCategory(cat) }
                            )

                            // Find in page bar if active
                            AnimatedVisibility(
                                visible = activeTab.isFindInPageActive,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                FindInPageBar(
                                    tab = activeTab,
                                    viewModel = viewModel
                                )
                            }

                            // Translation Bar if activated
                            AnimatedVisibility(
                                visible = isTranslateSheetVisible,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                TranslateBar(
                                    targetLanguage = targetLanguage,
                                    availableLanguages = viewModel.availableLanguages,
                                    isTranslating = isTranslating,
                                    isPageTranslated = isPageTranslated,
                                    viewModel = viewModel,
                                    onDismiss = { viewModel.setTranslateSheetVisible(false) }
                                )
                            }

                            // Main Content Area (Speed Dial / New Tab Page OR Reader Mode OR Web Content)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                when {
                                    activeTab.isReaderMode -> {
                                        ReaderModeView(
                                            article = readerArticle,
                                            theme = readerTheme,
                                            font = readerFont,
                                            fontSize = readerFontSize,
                                            viewModel = viewModel,
                                            onClose = { viewModel.toggleReaderMode() }
                                        )
                                    }
                                    activeTab.url == "chronion://newtab" -> {
                                        NewTabPage(
                                            tab = activeTab,
                                            viewModel = viewModel,
                                            searchEngine = searchEngine,
                                            totalAdsBlocked = totalAdsBlocked,
                                            totalTrackersBlocked = totalTrackersBlocked,
                                            speedDialItems = customSpeedDialItems
                                        )
                                    }
                                    else -> {
                                        BrowserWebView(
                                            tab = activeTab,
                                            viewModel = viewModel,
                                            activeScripts = activeScripts,
                                            isAdBlockEnabled = isAdBlockEnabled,
                                            whitelistedDomains = whitelistedDomains,
                                            forceWebDarkMode = forceWebDarkMode
                                        )
                                    }
                                }
                            }

                            // Bottom Navigation & Tools Bar
                            val isCurrentPageBookmarked = bookmarks.any { it.url == activeTab.url }
                            BottomBrowserBar(
                                tab = activeTab,
                                tabCount = tabs.size,
                                isBookmarked = isCurrentPageBookmarked,
                                viewModel = viewModel
                            )
                        }
                    }

                    // Eye Care Amber Overlay (Reduces Blue Light & Visual Fatigue)
                    EyeCareOverlay(
                        isEnabled = isEyeCareEnabled,
                        warmth = eyeCareWarmth
                    )

                    // Bottom Sheets & Dialogs
                    if (isBookmarksSheetVisible) {
                        BookmarksSheet(
                            bookmarks = bookmarks,
                            cloudAccount = cloudAccount,
                            isSyncing = isSyncing,
                            viewModel = viewModel,
                            onDismiss = { viewModel.setBookmarksSheetVisible(false) }
                        )
                    }

                    if (isHistorySheetVisible) {
                        HistorySheet(
                            history = history,
                            viewModel = viewModel,
                            onDismiss = { viewModel.setHistorySheetVisible(false) }
                        )
                    }

                    if (isExtensionsSheetVisible) {
                        ExtensionsSheet(
                            scripts = userScripts,
                            viewModel = viewModel,
                            onDismiss = { viewModel.setExtensionsSheetVisible(false) }
                        )
                    }

                    if (isSettingsSheetVisible) {
                        SettingsSheet(
                            viewModel = viewModel,
                            darkThemeStyle = darkThemeStyle,
                            isEyeCareEnabled = isEyeCareEnabled,
                            eyeCareWarmth = eyeCareWarmth,
                            isAdBlockEnabled = isAdBlockEnabled,
                            blockTrackers = blockTrackers,
                            blockPopups = blockPopups,
                            forceWebDarkMode = forceWebDarkMode,
                            searchEngine = searchEngine,
                            onDismiss = { viewModel.setSettingsSheetVisible(false) }
                        )
                    }

                    if (isAdvancedSearchSheetVisible) {
                        AdvancedSearchSheet(
                            viewModel = viewModel,
                            currentEngine = searchEngine,
                            currentCategory = searchCategory,
                            onDismiss = { viewModel.setAdvancedSearchSheetVisible(false) }
                        )
                    }

                    if (isShieldDialogVisible) {
                        PageInfoAndShieldDialog(
                            tab = activeTab,
                            isWhitelisted = viewModel.isCurrentDomainWhitelisted(),
                            isAdBlockGloballyEnabled = isAdBlockEnabled,
                            viewModel = viewModel,
                            onDismiss = { viewModel.setShieldDialogVisible(false) }
                        )
                    }

                    if (isDownloadsSheetVisible) {
                        DownloadManagerSheet(
                            downloads = downloads,
                            selectedCategory = selectedDownloadCategory,
                            viewModel = viewModel,
                            onDismiss = { viewModel.setDownloadsSheetVisible(false) }
                        )
                    }

                    if (isDevToolsSheetVisible) {
                        DevToolsSheet(
                            tab = activeTab,
                            consoleMessages = devConsoleMessages,
                            viewModel = viewModel,
                            onDismiss = { viewModel.setDevToolsSheetVisible(false) }
                        )
                    }
                }
            }
        }
    }
}

