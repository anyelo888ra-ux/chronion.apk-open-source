package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TabItem
import com.example.viewmodel.BrowserViewModel

@Composable
fun BottomBrowserBar(
    tab: TabItem,
    tabCount: Int,
    isBookmarked: Boolean,
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var totalDragX by remember { mutableFloatStateOf(0f) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (totalDragX > 70f) {
                            viewModel.switchToPrevTab()
                        } else if (totalDragX < -70f) {
                            viewModel.switchToNextTab()
                        }
                        totalDragX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        totalDragX += dragAmount
                    }
                )
            },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Home / New Tab button
            IconButton(
                onClick = { viewModel.navigateTo("chronion://newtab") },
                modifier = Modifier
                    .size(44.dp)
                    .testTag("home_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Página de Inicio",
                    tint = if (tab.url == "chronion://newtab") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Bookmarks Quick Button
            IconButton(
                onClick = { viewModel.toggleBookmarkCurrentPage() },
                modifier = Modifier
                    .size(44.dp)
                    .testTag("bookmark_toggle_button")
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Guardar en Marcadores",
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // New Tab (+) Button
            IconButton(
                onClick = { viewModel.createNewTab(isIncognito = tab.isIncognito) },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .testTag("new_tab_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva Pestaña",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Tab Switcher Pill Button with Tab Counter
            Box(
                modifier = Modifier
                    .size(width = 38.dp, height = 30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.8.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { viewModel.setTabSwitcherVisible(true) }
                    .testTag("tab_switcher_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (tabCount > 99) ":D" else tabCount.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Overflow / Quick Tools Menu
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("menu_more_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Más Opciones",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    DropdownMenuItem(
                        text = { Text("Marcadores y Sincronización") },
                        onClick = {
                            showMenu = false
                            viewModel.setBookmarksSheetVisible(true)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Historial de Navegación") },
                        onClick = {
                            showMenu = false
                            viewModel.setHistorySheetVisible(true)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Descargas y Archivos") },
                        onClick = {
                            showMenu = false
                            viewModel.setDownloadsSheetVisible(true)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Extensiones y Scripts") },
                        onClick = {
                            showMenu = false
                            viewModel.setExtensionsSheetVisible(true)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Traducir Página Web") },
                        onClick = {
                            showMenu = false
                            viewModel.setTranslateSheetVisible(true)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Buscar en la página") },
                        onClick = {
                            showMenu = false
                            viewModel.setFindInPage(true)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(if (tab.isDesktopMode) "✓ Sitio para Móvil" else "Sitio para Computadora (Desktop)")
                        },
                        onClick = {
                            showMenu = false
                            viewModel.toggleDesktopMode()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(if (tab.isReaderMode) "✓ Salir de Modo Lectura" else "Modo Lectura (Reader View)")
                        },
                        onClick = {
                            showMenu = false
                            viewModel.toggleReaderMode()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Herramientas de Desarrollador (DevTools)") },
                        onClick = {
                            showMenu = false
                            viewModel.setDevToolsSheetVisible(true)
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (tab.isIncognito) "Nueva Pestaña Normal" else "Nueva Pestaña Privada",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        onClick = {
                            showMenu = false
                            viewModel.createNewTab(isIncognito = !tab.isIncognito)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Configuración y Modo Oscuro") },
                        onClick = {
                            showMenu = false
                            viewModel.setSettingsSheetVisible(true)
                        }
                    )
                }
            }
        }
    }
}
