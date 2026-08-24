package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun EyeCareOverlay(
    isEnabled: Boolean,
    warmth: Float,
    modifier: Modifier = Modifier
) {
    if (!isEnabled) return

    // Warm amber color (reduces blue light spectrum)
    val overlayColor = Color(
        red = 1.0f,
        green = 0.65f,
        blue = 0.2f,
        alpha = (warmth * 0.45f).coerceIn(0.05f, 0.45f)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(overlayColor)
    )
}
