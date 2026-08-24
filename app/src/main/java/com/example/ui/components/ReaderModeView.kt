package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReaderArticle
import com.example.data.model.ReaderFont
import com.example.data.model.ReaderTheme
import com.example.viewmodel.BrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderModeView(
    article: ReaderArticle?,
    theme: ReaderTheme,
    font: ReaderFont,
    fontSize: Int,
    viewModel: BrowserViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFormattingControls by remember { mutableStateOf(false) }
    val bgColor = Color(theme.bgHex)
    val textColor = Color(theme.textHex)
    val fontFamily = when (font) {
        ReaderFont.SERIF -> FontFamily.Serif
        ReaderFont.SANS -> FontFamily.SansSerif
        ReaderFont.MONO -> FontFamily.Monospace
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("reader_mode_view")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Reader Bar
            Surface(
                color = bgColor,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TextFields,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Modo Lectura Chronioñ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                text = article?.domain ?: "Vista Limpia",
                                fontSize = 11.sp,
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showFormattingControls = !showFormattingControls },
                            modifier = Modifier.testTag("reader_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Ajustes de Lectura",
                                tint = textColor
                            )
                        }
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.testTag("close_reader_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar Modo Lectura",
                                tint = textColor
                            )
                        }
                    }
                }
            }

            // Formatting Controls Panel
            AnimatedVisibility(
                visible = showFormattingControls,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Personalizar Vista de Lectura",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Theme Selection
                        Text(text = "Tema de Papel", style = MaterialTheme.typography.labelMedium)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            ReaderTheme.values().forEach { t ->
                                val isSelected = theme == t
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(t.bgHex))
                                        .clickable { viewModel.setReaderTheme(t) }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = t.title.take(6),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = Color(t.textHex)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Font size control
                        Text(text = "Tamaño de Fuente (${fontSize}sp)", style = MaterialTheme.typography.labelMedium)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.setReaderFontSize(fontSize - 2) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("A-", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.setReaderFontSize(fontSize + 2) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("A+", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Font Family Selection
                        Text(text = "Tipografía", style = MaterialTheme.typography.labelMedium)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ReaderFont.values().forEach { f ->
                                FilterChip(
                                    selected = font == f,
                                    onClick = { viewModel.setReaderFont(f) },
                                    label = { Text(f.title.take(5), fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Article Content Scrollable Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Article Header
                Text(
                    text = article?.title ?: "Artículo Web",
                    fontSize = (fontSize + 8).sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    lineHeight = (fontSize + 12).sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Metadata badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = textColor.copy(alpha = 0.08f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = textColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${article?.readingTimeMinutes ?: 3} min de lectura",
                                fontSize = 11.sp,
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Text(
                        text = "• ${article?.wordCount ?: 350} palabras",
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Divider(color = textColor.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(20.dp))

                // Article Paragraphs
                val paragraphs = article?.paragraphs ?: listOf(
                    "El modo de lectura de Chronioñ limpia todo el desorden visual para ofrecer una experiencia placentera y libre de distracciones."
                )

                paragraphs.forEach { paragraph ->
                    Text(
                        text = paragraph,
                        fontSize = fontSize.sp,
                        fontFamily = fontFamily,
                        color = textColor.copy(alpha = 0.92f),
                        lineHeight = (fontSize * 1.6f).sp,
                        modifier = Modifier.padding(bottom = 18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "— Fin del artículo optimizado por Chronioñ —",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = textColor.copy(alpha = 0.5f)
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
