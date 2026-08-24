package com.example.data.model

object ExtensionCatalog {

    val DEFAULT_SCRIPTS = listOf(
        UserScript(
            id = 1L,
            name = "Dark Reader Pro",
            description = "Aplica un tema oscuro inteligente y suave a cualquier sitio web para reducir la fatiga ocular.",
            author = "Chronioñ Core Team",
            version = "2.4",
            matchUrlPattern = "*",
            jsCode = """
                (function() {
                    if (document.getElementById('chronion-dark-reader')) return;
                    var style = document.createElement('style');
                    style.id = 'chronion-dark-reader';
                    style.textContent = `
                        html {
                            filter: invert(90%) hue-rotate(180deg) !important;
                            background: #121212 !important;
                        }
                        img, video, canvas, svg, picture, [style*="background-image"] {
                            filter: invert(111%) hue-rotate(180deg) !important;
                        }
                        iframe {
                            filter: invert(100%) hue-rotate(180deg) !important;
                        }
                    `;
                    document.head.appendChild(style);
                })();
            """.trimIndent(),
            cssCode = "",
            isEnabled = false,
            isBuiltIn = true
        ),
        UserScript(
            id = 2L,
            name = "Bloqueador de Consentimiento de Cookies",
            description = "Oculta automáticamente banners molestos de cookies y ventanas de consentimiento GDPR.",
            author = "Chronioñ Privacy Lab",
            version = "1.8",
            matchUrlPattern = "*",
            jsCode = """
                (function() {
                    var selectors = [
                        '#onetrust-consent-sdk', '.cookie-banner', '#cookie-banner',
                        '[id*="cookie-law"]', '[class*="consent-modal"]', '.qc-cmp2-container',
                        '#didomi-host', '.cc-window', '[aria-label*="cookie" i]'
                    ];
                    function removeCookies() {
                        selectors.forEach(function(sel) {
                            var els = document.querySelectorAll(sel);
                            for (var i = 0; i < els.length; i++) {
                                els[i].remove();
                            }
                        });
                        document.body.style.overflow = 'auto';
                    }
                    removeCookies();
                    window.addEventListener('load', removeCookies);
                })();
            """.trimIndent(),
            cssCode = "",
            isEnabled = true,
            isBuiltIn = true
        ),
        UserScript(
            id = 3L,
            name = "Controlador de Velocidad de Video & PiP",
            description = "Permite cambiar la velocidad de cualquier reproductor de video (HTML5/YouTube) hasta 3x y activar Picture-in-Picture.",
            author = "Chronioñ Media",
            version = "3.1",
            matchUrlPattern = "*",
            jsCode = """
                (function() {
                    var videos = document.querySelectorAll('video');
                    videos.forEach(function(v) {
                        v.playbackRate = window.__chronionSpeed || 1.0;
                    });
                })();
            """.trimIndent(),
            cssCode = "",
            isEnabled = true,
            isBuiltIn = true
        ),
        UserScript(
            id = 4L,
            name = "Modo Lectura Inmersivo",
            description = "Limpia la página de distracciones y enfoca el texto principal con tipografía optimizada.",
            author = "Chronioñ Reader",
            version = "2.0",
            matchUrlPattern = "*",
            jsCode = """
                (function() {
                    if (document.getElementById('chronion-reading-mode')) return;
                    var style = document.createElement('style');
                    style.id = 'chronion-reading-mode';
                    style.textContent = `
                        article, main, .post-content, .article-body, .entry-content {
                            max-width: 720px !important;
                            margin: 0 auto !important;
                            font-size: 19px !important;
                            line-height: 1.75 !important;
                            font-family: serif !important;
                            color: #e0e0e0 !important;
                            background: #1a1a1a !important;
                            padding: 24px !important;
                        }
                        aside, nav, header, footer, .sidebar, .comments, .related-posts {
                            display: none !important;
                        }
                    `;
                    document.head.appendChild(style);
                })();
            """.trimIndent(),
            cssCode = "",
            isEnabled = false,
            isBuiltIn = true
        ),
        UserScript(
            id = 5L,
            name = "Traductor Rápido & Resaltador",
            description = "Resalta párrafos al tocarlos para facilitar la traducción y comprensión rápida.",
            author = "Chronioñ Tools",
            version = "1.2",
            matchUrlPattern = "*",
            jsCode = """
                (function() {
                    document.addEventListener('click', function(e) {
                        if (e.target && (e.target.tagName === 'P' || e.target.tagName === 'SPAN')) {
                            e.target.style.outline = '2px dashed #00E5FF';
                            setTimeout(function() { e.target.style.outline = ''; }, 2500);
                        }
                    });
                })();
            """.trimIndent(),
            cssCode = "",
            isEnabled = false,
            isBuiltIn = true
        )
    )
}
