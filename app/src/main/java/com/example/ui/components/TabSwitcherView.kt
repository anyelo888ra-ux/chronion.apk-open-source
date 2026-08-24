package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TabItem
import com.example.ui.theme.ShieldGreen
import com.example.viewmodel.BrowserViewModel

@Composable
fun TabSwitcherView(
    tabs: List<TabItem>,
    activeTabId: String,
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember {
        mutableIntStateOf(if (tabs.find { it.id == activeTabId }?.isIncognito == true) 1 else 0)
    }

    val isViewingIncognito = selectedTabIndex == 1
    val displayedTabs = tabs.filter { it.isIncognito == isViewingIncognito }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.setTabSwitcherVisible(false) },
                    modifier = Modifier.testTag("close_tab_switcher_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver a navegación",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = if (isViewingIncognito) "Pestañas Privadas" else "Pestañas Abiertas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(
                    onClick = { viewModel.createNewTab(isIncognito = isViewingIncognito) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("add_tab_switcher_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Crear pestaña",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Normal / Incognito Toggle Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = { Icon(Icons.Default.Tab, contentDescription = null) },
                    text = {
                        val count = tabs.count { !it.isIncognito }
                        Text("Estándar ($count)", fontWeight = FontWeight.SemiBold)
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = { Icon(Icons.Default.VisibilityOff, contentDescription = null) },
                    text = {
                        val count = tabs.count { it.isIncognito }
                        Text("Privadas ($count)", fontWeight = FontWeight.SemiBold)
                    }
                )
            }

            // Banner for Incognito Mode
            if (isViewingIncognito) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Modo Incógnito: el historial no se guarda.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (displayedTabs.isNotEmpty()) {
                        Button(
                            onClick = { viewModel.closeAllIncognitoTabs() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cerrar todas", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Grid of Tabs
            if (displayedTabs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isViewingIncognito) Icons.Default.VisibilityOff else Icons.Default.Language,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                        Text(
                            text = if (isViewingIncognito) "No hay pestañas privadas abiertas" else "No hay pestañas abiertas",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.createNewTab(isIncognito = isViewingIncognito) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Abrir Nueva Pestaña")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedTabs, key = { it.id }) { tab ->
                        val isActive = tab.id == activeTabId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isActive) 2.dp else 1.dp,
                                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.selectTab(tab.id) }
                                .testTag("tab_card_${tab.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 6.dp else 2.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Card Title Bar
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = if (tab.isIncognito) Icons.Default.VisibilityOff else Icons.Default.Language,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = tab.title,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontSize = 12.sp,
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.closeTab(tab.id) },
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Cerrar pestaña",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                // Card Content Preview Area
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.surfaceVariant,
                                                    MaterialTheme.colorScheme.surface
                                                )
                                            )
                                        )
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = if (tab.url == "chronion://newtab") "Inicio Chronioñ" else tab.url.removePrefix("https://").removePrefix("http://"),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center
                                        )

                                        val totalBlocked = tab.blockedAdsCount + tab.blockedTrackersCount
                                        if (totalBlocked > 0) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(ShieldGreen.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Icon(Icons.Default.Security, contentDescription = null, tint = ShieldGreen, modifier = Modifier.size(12.dp))
                                                Text(
                                                    text = "$totalBlocked bloqueados",
                                                    fontSize = 10.sp,
                                                    color = ShieldGreen,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
