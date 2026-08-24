package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.adblock.AdBlockEngine
import com.example.data.model.TabItem
import com.example.data.model.UserScript
import com.example.viewmodel.BrowserViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
    tab: TabItem,
    viewModel: BrowserViewModel,
    activeScripts: List<UserScript>,
    isAdBlockEnabled: Boolean,
    whitelistedDomains: Set<String>,
    forceWebDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webView = remember(tab.id) {
        createConfiguredWebView(context, tab.isIncognito)
    }

    // Handle desktop mode change
    LaunchedEffect(tab.isDesktopMode) {
        val defaultUa = WebSettings.getDefaultUserAgent(context)
        if (tab.isDesktopMode) {
            webView.settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            webView.settings.useWideViewPort = true
            webView.settings.loadWithOverviewMode = true
        } else {
            webView.settings.userAgentString = defaultUa
            webView.settings.useWideViewPort = true
            webView.settings.loadWithOverviewMode = true
        }
    }

    // Handle In-Page Search
    LaunchedEffect(tab.isFindInPageActive, tab.findQuery) {
        if (tab.isFindInPageActive && tab.findQuery.isNotBlank()) {
            webView.findAllAsync(tab.findQuery)
            webView.setFindListener { activeMatchOrdinal, numberOfMatches, _ ->
                viewModel.updateFindResults(numberOfMatches, activeMatchOrdinal)
            }
        } else if (!tab.isFindInPageActive) {
            webView.clearMatches()
        }
    }

    // Set clients
    LaunchedEffect(isAdBlockEnabled, whitelistedDomains, activeScripts, forceWebDarkMode, tab.isReaderMode) {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val url = request?.url?.toString() ?: return null
                if (AdBlockEngine.shouldBlockUrl(url, isAdBlockEnabled, whitelistedDomains)) {
                    viewModel.recordBlockedItem(url)
                    return AdBlockEngine.createEmptyResourceResponse()
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                viewModel.updateTabProgress(20)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val currentUrl = url ?: ""
                val title = view?.title ?: currentUrl
                viewModel.updateTabLoaded(
                    title = title,
                    url = currentUrl,
                    canBack = view?.canGoBack() ?: false,
                    canForward = view?.canGoForward() ?: false,
                    favicon = null
                )

                // Inject AdBlock element hiding CSS & JS
                if (isAdBlockEnabled) {
                    view?.evaluateJavascript(AdBlockEngine.elementHidingJs, null)
                }

                // Inject Dark Mode if requested
                if (forceWebDarkMode) {
                    val darkReader = """
                        (function() {
                            if (document.getElementById('chronion-dark-reader-auto')) return;
                            var style = document.createElement('style');
                            style.id = 'chronion-dark-reader-auto';
                            style.textContent = `
                                html, body { background-color: #121212 !important; color: #E0E0E0 !important; }
                                p, span, h1, h2, h3, h4, h5, h6, li, a { color: #E0E0E0 !important; }
                                input, textarea, select { background-color: #1E1E1E !important; color: #FFF !important; }
                            `;
                            document.head.appendChild(style);
                        })();
                    """.trimIndent()
                    view?.evaluateJavascript(darkReader, null)
                }

                // Inject UserScripts
                activeScripts.forEach { script ->
                    if (script.isEnabled && script.jsCode.isNotBlank()) {
                        view?.evaluateJavascript(script.jsCode, null)
                    }
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                viewModel.updateTabProgress(newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (!title.isNullOrBlank()) {
                    viewModel.updateCurrentTab {
                        it.copy(title = title)
                    }
                }
            }
        }
    }

    // Handle initial or updated URL loading
    LaunchedEffect(tab.url) {
        if (!tab.url.startsWith("chronion://") && webView.url != tab.url) {
            webView.loadUrl(tab.url)
        }
    }

    DisposableEffect(tab.id) {
        onDispose {
            if (tab.isIncognito) {
                webView.clearCache(true)
                webView.clearFormData()
                webView.clearHistory()
                CookieManager.getInstance().removeSessionCookies(null)
            }
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier.fillMaxSize()
    )
}

@SuppressLint("SetJavaScriptEnabled")
private fun createConfiguredWebView(context: Context, isIncognito: Boolean): WebView {
    return WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = !isIncognito
            cacheMode = if (isIncognito) WebSettings.LOAD_NO_CACHE else WebSettings.LOAD_DEFAULT
            allowFileAccess = false
            allowContentAccess = false
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            useWideViewPort = true
            loadWithOverviewMode = true
            javaScriptCanOpenWindowsAutomatically = false
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
        isVerticalScrollBarEnabled = true
        isHorizontalScrollBarEnabled = false

        if (isIncognito) {
            CookieManager.getInstance().setAcceptCookie(false)
        } else {
            CookieManager.getInstance().setAcceptCookie(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            }
        }
    }
}
