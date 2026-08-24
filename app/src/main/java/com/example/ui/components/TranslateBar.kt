package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TargetLanguage
import com.example.viewmodel.BrowserViewModel

@Composable
fun TranslateBar(
    targetLanguage: TargetLanguage,
    availableLanguages: List<TargetLanguage>,
    isTranslating: Boolean,
    isPageTranslated: Boolean,
    viewModel: BrowserViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLanguageMenuOpen by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 3.dp,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("translate_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.GTranslate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box {
                    TextButton(
                        onClick = { isLanguageMenuOpen = true }
                    ) {
                        Text(
                            text = "${targetLanguage.flag} ${targetLanguage.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    DropdownMenu(
                        expanded = isLanguageMenuOpen,
                        onDismissRequest = { isLanguageMenuOpen = false }
                    ) {
                        availableLanguages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text("${lang.flag} ${lang.name}") },
                                onClick = {
                                    viewModel.setTargetLanguage(lang)
                                    isLanguageMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isTranslating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else if (isPageTranslated) {
                    TextButton(onClick = { viewModel.restoreOriginalLanguage() }) {
                        Text("Original", fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = { viewModel.translateActivePage(targetLanguage) },
                        contentPadding = ButtonDefaults.TextButtonContentPadding,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Traducir", fontSize = 12.sp)
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar Traducción",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
