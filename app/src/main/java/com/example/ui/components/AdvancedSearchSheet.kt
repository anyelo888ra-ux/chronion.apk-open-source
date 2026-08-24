package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SearchCategory
import com.example.data.model.SearchEngine
import com.example.viewmodel.BrowserViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdvancedSearchSheet(
    viewModel: BrowserViewModel,
    currentEngine: SearchEngine,
    currentCategory: SearchCategory,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var queryText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(currentCategory) }
    var selectedEngine by remember { mutableStateOf(currentEngine) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Búsqueda Avanzada",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            // Query Input Field
            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("advanced_query_input"),
                placeholder = { Text("¿Qué deseas investigar?") },
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
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            )

            // Categorized Filters Section
            Text(
                text = "Filtrar por Categoría:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchCategory.values().forEach { cat ->
                    val icon = getCategoryIcon(cat)
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = { Text(cat.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Search Engines Selector
            Text(
                text = "Motor de Búsqueda:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchEngine.values().forEach { engine ->
                    FilterChip(
                        selected = selectedEngine == engine,
                        onClick = {
                            selectedEngine = engine
                            viewModel.setSearchEngine(engine)
                        },
                        label = { Text(engine.displayName, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (queryText.isNotBlank()) {
                        viewModel.setSearchCategory(selectedCategory)
                        viewModel.setSearchEngine(selectedEngine)
                        viewModel.navigateTo(queryText, selectedCategory)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_advanced_search"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Buscar en ${selectedCategory.label}", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun getCategoryIcon(category: SearchCategory): ImageVector {
    return when (category) {
        SearchCategory.ALL -> Icons.Default.Search
        SearchCategory.NEWS -> Icons.Default.Newspaper
        SearchCategory.IMAGES -> Icons.Default.Image
        SearchCategory.VIDEOS -> Icons.Default.Movie
        SearchCategory.SCHOLAR -> Icons.Default.School
        SearchCategory.WIKIPEDIA -> Icons.Default.MenuBook
        SearchCategory.DEV -> Icons.Default.Code
        SearchCategory.REDDIT -> Icons.Default.Forum
    }
}
