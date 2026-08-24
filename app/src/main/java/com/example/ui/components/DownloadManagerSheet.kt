package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadCategory
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.viewmodel.BrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadManagerSheet(
    downloads: List<DownloadItem>,
    selectedCategory: DownloadCategory,
    viewModel: BrowserViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("download_manager_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Descargas Chronioñ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${downloads.size} archivos guardados",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("new_download_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Nueva Descarga")
                    }
                    if (downloads.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearDownloads() },
                            modifier = Modifier.testTag("clear_downloads_button")
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Limpiar Todo")
                        }
                    }
                }
            }

            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DownloadCategory.values()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.setDownloadCategory(category) },
                        label = { Text(category.label) }
                    )
                }
            }

            val filteredDownloads = remember(downloads, selectedCategory) {
                if (selectedCategory == DownloadCategory.ALL) {
                    downloads
                } else {
                    downloads.filter { it.category == selectedCategory }
                }
            }

            if (filteredDownloads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No hay descargas en esta categoría",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredDownloads, key = { it.id }) { item ->
                        DownloadItemCard(
                            item = item,
                            onDelete = { viewModel.deleteDownload(item.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var urlInput by remember { mutableStateOf("") }
        var nameInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Iniciar Nueva Descarga") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Introduce la URL directa del archivo o APK que deseas descargar con el gestor nativo de Chronioñ.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("URL de descarga (https://...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nombre del archivo (opcional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (urlInput.isNotBlank()) {
                            viewModel.startDownload(urlInput.trim(), nameInput.ifBlank { null })
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Descargar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun DownloadItemCard(
    item: DownloadItem,
    onDelete: () -> Unit
) {
    val icon: ImageVector = when (item.category) {
        DownloadCategory.APKS -> Icons.Default.Android
        DownloadCategory.DOCUMENTS -> Icons.Default.Description
        DownloadCategory.IMAGES -> Icons.Default.Image
        DownloadCategory.MEDIA -> Icons.Default.Movie
        else -> Icons.Default.FolderZip
    }

    val sizeMb = String.format("%.1f MB", item.totalBytes / (1024f * 1024f))
    val downloadedMb = String.format("%.1f MB", item.downloadedBytes / (1024f * 1024f))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.fileName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = when (item.status) {
                            DownloadStatus.DOWNLOADING -> "Descargando: $downloadedMb de $sizeMb (${item.progressPercent}%)"
                            DownloadStatus.COMPLETED -> "$sizeMb • Completado con éxito"
                            DownloadStatus.PAUSED -> "Pausado: $downloadedMb de $sizeMb"
                            DownloadStatus.FAILED -> "Error de red"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = when (item.status) {
                            DownloadStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                            DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (item.status == DownloadStatus.DOWNLOADING) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = item.progressPercent / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }
    }
}
