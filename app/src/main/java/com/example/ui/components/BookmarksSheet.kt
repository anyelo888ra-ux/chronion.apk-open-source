package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bookmark
import com.example.data.model.CloudSyncAccount
import com.example.ui.theme.ShieldGreen
import com.example.viewmodel.BrowserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksSheet(
    bookmarks: List<Bookmark>,
    cloudAccount: CloudSyncAccount,
    isSyncing: Boolean,
    viewModel: BrowserViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var selectedFolder by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val folders = bookmarks.map { it.folder }.distinct().filter { it.isNotBlank() }

    val filteredBookmarks = bookmarks.filter { b ->
        val matchesSearch = searchQuery.isBlank() ||
                b.title.contains(searchQuery, ignoreCase = true) ||
                b.url.contains(searchQuery, ignoreCase = true) ||
                b.tags.contains(searchQuery, ignoreCase = true)
        val matchesFolder = selectedFolder == null || b.folder == selectedFolder
        matchesSearch && matchesFolder
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxHeight(0.9f)
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Marcadores (${bookmarks.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Marcador", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            }

            // Google Account Cloud Sync Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = cloudAccount.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = cloudAccount.email,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = ShieldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Sincronizado",
                                    fontSize = 11.sp,
                                    color = ShieldGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val formattedDate = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(cloudAccount.lastSyncTimestamp))
                        Text(
                            text = "Última sincronización: $formattedDate",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )

                        Button(
                            onClick = { viewModel.syncWithCloud() },
                            enabled = !isSyncing,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Sincronizar", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                placeholder = { Text("Buscar en marcadores...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            // Folder Filter Pills
            if (folders.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedFolder == null,
                            onClick = { selectedFolder = null },
                            label = { Text("Todos", fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    items(folders) { folder ->
                        FilterChip(
                            selected = selectedFolder == folder,
                            onClick = { selectedFolder = folder },
                            leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            label = { Text(folder, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Bookmarks List
            if (filteredBookmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron marcadores.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(filteredBookmarks, key = { it.id }) { bookmark ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.navigateTo(bookmark.url)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = bookmark.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = bookmark.url,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (bookmark.folder.isNotBlank()) {
                                            Text(
                                                text = "📁 ${bookmark.folder}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteBookmark(bookmark) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Bookmark Dialog
    if (showAddDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newUrl by remember { mutableStateOf("") }
        var newFolder by remember { mutableStateOf("Favoritos") }
        var newTags by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nuevo Marcador") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Título") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        label = { Text("URL (https://...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newFolder,
                        onValueChange = { newFolder = it },
                        label = { Text("Carpeta") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTags,
                        onValueChange = { newTags = it },
                        label = { Text("Etiquetas (separadas por comas)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newUrl.isNotBlank()) {
                            viewModel.addBookmark(
                                title = newTitle.ifBlank { newUrl },
                                url = if (newUrl.startsWith("http")) newUrl else "https://$newUrl",
                                folder = newFolder,
                                tags = newTags
                            )
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Guardar")
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
