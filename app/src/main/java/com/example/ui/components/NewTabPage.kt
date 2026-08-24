package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SearchCategory
import com.example.data.model.SearchEngine
import com.example.data.model.SpeedDialItem
import com.example.data.model.TabItem
import com.example.ui.theme.CosmicSecondary
import com.example.ui.theme.ShieldGreen
import com.example.viewmodel.BrowserViewModel

@Composable
fun NewTabPage(
    tab: TabItem,
    viewModel: BrowserViewModel,
    searchEngine: SearchEngine,
    totalAdsBlocked: Int,
    totalTrackersBlocked: Int,
    speedDialItems: List<SpeedDialItem>,
    modifier: Modifier = Modifier
) {
    var searchInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(SearchCategory.ALL) }
    var showAddSpeedDialDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Official Brand Logo & Cosmic Title Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.chronion_app_icon_1787534814846),
                            contentDescription = "Chronioñ Official Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Text(
                        text = if (tab.isIncognito) "Chronioñ Privado" else "Chronioñ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = if (tab.isIncognito)
                            "Navegación ultra-privada sin rastros ni historial"
                        else
                            "Navegador de Próxima Generación • Rápido & Seguro",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Quick Categorized Search Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = searchInput,
                            onValueChange = { searchInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_search_input"),
                            placeholder = {
                                Text(
                                    "Buscar con ${searchEngine.displayName}...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )

                        // Category Pills inside search box
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                SearchCategory.ALL,
                                SearchCategory.NEWS,
                                SearchCategory.IMAGES,
                                SearchCategory.VIDEOS,
                                SearchCategory.DEV
                            ).forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = {
                                        selectedCategory = cat
                                        if (searchInput.isNotBlank()) {
                                            viewModel.navigateTo(searchInput, cat)
                                        }
                                    },
                                    label = { Text(cat.label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Privacy & AdBlock Protection Metrics Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setShieldDialogVisible(true) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ShieldGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ShieldGreen,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Protección Chronioñ Activa",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Bloqueo de anuncios, rastreadores y minería",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${totalAdsBlocked + totalTrackersBlocked}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ShieldGreen
                            )
                            Text(
                                text = "Bloqueados",
                                fontSize = 10.sp,
                                color = ShieldGreen
                            )
                        }
                    }
                }
            }

            // Speed Dial Shortcuts (Accesos Directos)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sitios Frecuentes",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(onClick = { showAddSpeedDialDialog = true }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Añadir", fontSize = 12.sp)
                        }
                    }

                    // 4 columns grid for speed dial items
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        speedDialItems.chunked(4).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                rowItems.forEach { item ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier
                                            .width(72.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { viewModel.navigateTo(item.url) }
                                            .padding(vertical = 6.dp)
                                            .testTag("speed_dial_${item.title}")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = item.title.take(2).uppercase(),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Text(
                                            text = item.title,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Power Tools Hub (Descargas, DevTools, Scripts, Sincronización)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Herramientas Avanzadas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setDownloadsSheetVisible(true) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text("Descargas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Gestor Nativo", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setDevToolsSheetVisible(true) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text("DevTools", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Consola & DOM", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setExtensionsSheetVisible(true) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Extension,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text("Extensiones", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("UserScripts", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setBookmarksSheetVisible(true) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = CosmicSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text("Nube & Cuenta", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Sincronización", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSpeedDialDialog) {
        var titleInput by remember { mutableStateOf("") }
        var urlInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSpeedDialDialog = false },
            title = { Text("Añadir Sitio Frecuente") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Nombre o Título (ej. GitHub)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("Dirección URL (ej. https://github.com)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.isNotBlank() && urlInput.isNotBlank()) {
                            val finalUrl = if (!urlInput.startsWith("http://") && !urlInput.startsWith("https://")) {
                                "https://$urlInput"
                            } else urlInput
                            viewModel.addCustomSpeedDial(titleInput.trim(), finalUrl.trim())
                            showAddSpeedDialDialog = false
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSpeedDialDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

