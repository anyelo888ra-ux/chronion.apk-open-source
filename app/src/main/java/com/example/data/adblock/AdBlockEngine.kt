package com.example.data.adblock

import android.net.Uri
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

object AdBlockEngine {

    // Known ad & tracker hosts
    private val blockedHosts = hashSetOf(
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "google-analytics.com",
        "analytics.google.com",
        "admob.com",
        "adnxs.com",
        "adform.net",
        "adsystem.com",
        "adroll.com",
        "adserver.com",
        "adsafeprotected.com",
        "criteo.com",
        "criteo.net",
        "taboola.com",
        "outbrain.com",
        "scorecardresearch.com",
        "moatads.com",
        "amazon-adsystem.com",
        "quantserve.com",
        "popads.net",
        "popcash.net",
        "propellerads.com",
        "adskeeper.co.uk",
        "adskeeper.com",
        "zergnet.com",
        "mgid.com",
        "revcontent.com",
        "infolinks.com",
        "bidswitch.net",
        "rubiconproject.com",
        "openx.net",
        "pubmatic.com",
        "smartadserver.com",
        "casalemedia.com",
        "media.net",
        "sovrn.com",
        "chartbeat.com",
        "hotjar.com",
        "segment.io",
        "mixpanel.com",
        "newrelic.com",
        "yandex.ru/metrika",
        "adservice.com",
        "serving-sys.com",
        "adcolony.com",
        "unityads.unity3d.com",
        "applovin.com",
        "vungle.com",
        "ironsrc.com",
        "chartboost.com"
    )

    private val adUrlKeywords = listOf(
        "/ads.js",
        "/pagead/",
        "/adsbygoogle.js",
        "/advertisement/",
        "/adserver/",
        "/ad_banner",
        "googleads.g.doubleclick.net",
        "fbevents.js",
        "pixel.facebook.com",
        "ad_type=",
        "banner_id="
    )

    val elementHidingCss = """
        [id*='google_ads'], [class*='adsbygoogle'], [class*='ad-banner'], 
        [class*='ad-container'], [class*='sponsored-post'], [class*='advertisement'], 
        [aria-label*='advertisement' i], [aria-label*='patrocinado' i], 
        iframe[src*='doubleclick'], iframe[src*='googlesyndication'], 
        [id*='taboola-'], .outbrain-ad, .mgid-ad, [class*='ad-slot'],
        div[data-ad-unit], div[data-google-query-id] {
            display: none !important;
            visibility: hidden !important;
            height: 0 !important;
            width: 0 !important;
            pointer-events: none !important;
        }
    """.trimIndent()

    val elementHidingJs = """
        (function() {
            try {
                if (document.getElementById('chronion-adblock-css')) return;
                var style = document.createElement('style');
                style.id = 'chronion-adblock-css';
                style.textContent = `$elementHidingCss`;
                (document.head || document.documentElement).appendChild(style);
                
                // Remove existing intrusive popups/ad elements
                var badSelectors = ['[id*="google_ads"]', '.adsbygoogle', '[class*="sponsored"]'];
                badSelectors.forEach(function(sel) {
                    var els = document.querySelectorAll(sel);
                    for (var i = 0; i < els.length; i++) {
                        els[i].style.display = 'none';
                    }
                });
            } catch(e) {}
        })();
    """.trimIndent()

    fun shouldBlockUrl(url: String, isAdBlockEnabled: Boolean, whitelistedDomains: Set<String>): Boolean {
        if (!isAdBlockEnabled) return false
        val uri = try {
            Uri.parse(url)
        } catch (e: Exception) {
            return false
        }
        val host = uri.host?.lowercase() ?: return false

        // Check if domain is whitelisted
        if (whitelistedDomains.any { host.contains(it.lowercase()) }) {
            return false
        }

        // Check host matches
        for (blockedHost in blockedHosts) {
            if (host == blockedHost || host.endsWith(".$blockedHost")) {
                return true
            }
        }

        // Check path & query patterns
        val lowerUrl = url.lowercase()
        for (keyword in adUrlKeywords) {
            if (lowerUrl.contains(keyword)) {
                return true
            }
        }

        return false
    }

    fun createEmptyResourceResponse(): WebResourceResponse {
        return WebResourceResponse(
            "text/plain",
            "UTF-8",
            ByteArrayInputStream(ByteArray(0))
        )
    }

    fun isTracker(url: String): Boolean {
        val lower = url.lowercase()
        return lower.contains("analytics") || lower.contains("pixel") || 
               lower.contains("telemetry") || lower.contains("tracker") ||
               lower.contains("hotjar") || lower.contains("segment.io") ||
               lower.contains("scorecardresearch")
    }
}
