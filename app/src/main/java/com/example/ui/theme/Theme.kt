package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.DarkThemeStyle

private val CosmicColorScheme = darkColorScheme(
    primary = CosmicPrimary,
    onPrimary = Color.White,
    primaryContainer = CosmicPrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary = CosmicSecondary,
    onSecondary = Color.Black,
    tertiary = CosmicAccent,
    background = CosmicBackground,
    onBackground = CosmicTextPrimary,
    surface = CosmicSurface,
    onSurface = CosmicTextPrimary,
    surfaceVariant = CosmicSurfaceVariant,
    onSurfaceVariant = CosmicTextSecondary,
    outline = Color(0xFF30363D)
)

private val OledColorScheme = darkColorScheme(
    primary = OledPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003852),
    onPrimaryContainer = Color.White,
    secondary = OledSecondary,
    onSecondary = Color.White,
    tertiary = CosmicAccent,
    background = OledBackground,
    onBackground = Color(0xFFF0F0F0),
    surface = OledSurface,
    onSurface = Color(0xFFF0F0F0),
    surfaceVariant = OledSurfaceVariant,
    onSurfaceVariant = Color(0xFFA0A0A0),
    outline = Color(0xFF262626)
)

private val SepiaColorScheme = darkColorScheme(
    primary = SepiaPrimary,
    onPrimary = Color.Black,
    secondary = SepiaSecondary,
    onSecondary = Color.White,
    background = SepiaBackground,
    onBackground = SepiaTextPrimary,
    surface = SepiaSurface,
    onSurface = SepiaTextPrimary,
    surfaceVariant = SepiaSurfaceVariant,
    onSurfaceVariant = Color(0xFFC7BAA7),
    outline = Color(0xFF4A4035)
)

private val EmeraldColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.Black,
    secondary = EmeraldSecondary,
    onSecondary = Color.Black,
    background = EmeraldBackground,
    onBackground = Color(0xFFE6FFF7),
    surface = EmeraldSurface,
    onSurface = Color(0xFFE6FFF7),
    surfaceVariant = EmeraldSurfaceVariant,
    onSurfaceVariant = Color(0xFF80D4BC),
    outline = Color(0xFF1B4D3E)
)

private val IncognitoColorScheme = darkColorScheme(
    primary = IncognitoPrimary,
    onPrimary = Color.Black,
    secondary = IncognitoSecondary,
    onSecondary = Color.White,
    tertiary = IncognitoAccent,
    background = IncognitoBackground,
    onBackground = Color(0xFFF1EEF8),
    surface = IncognitoSurface,
    onSurface = Color(0xFFF1EEF8),
    surfaceVariant = IncognitoSurfaceVariant,
    onSurfaceVariant = Color(0xFFB5AEC6),
    outline = Color(0xFF39324C)
)

@Composable
fun MyApplicationTheme(
    darkThemeStyle: DarkThemeStyle = DarkThemeStyle.COSMIC_INDIGO,
    isIncognito: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemInDark = isSystemInDarkTheme()

    val colorScheme = when {
        isIncognito -> IncognitoColorScheme
        darkThemeStyle == DarkThemeStyle.COSMIC_INDIGO -> CosmicColorScheme
        darkThemeStyle == DarkThemeStyle.OLED_PITCH_BLACK -> OledColorScheme
        darkThemeStyle == DarkThemeStyle.WARM_SEPIA -> SepiaColorScheme
        darkThemeStyle == DarkThemeStyle.CYBER_EMERALD -> EmeraldColorScheme
        darkThemeStyle == DarkThemeStyle.DYNAMIC_SYSTEM && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (systemInDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkThemeStyle == DarkThemeStyle.DYNAMIC_SYSTEM && !systemInDark -> lightColorScheme(
            primary = CosmicPrimary,
            secondary = CosmicSecondary,
            background = Color(0xFFF8FAFC),
            surface = Color.White
        )
        else -> CosmicColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
