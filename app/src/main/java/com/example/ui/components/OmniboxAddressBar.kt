package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SearchCategory
import com.example.data.model.TabItem
import com.example.ui.theme.ShieldGreen
import com.example.viewmodel.BrowserViewModel

@Composable
fun OmniboxAddressBar(
    tab: TabItem,
    viewModel: BrowserViewModel,
    selectedCategory: SearchCategory,
    onCategorySelected: (SearchCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var inputText by remember(tab.url, isEditing) {
        mutableStateOf(
            if (tab.url == "chronion://newtab") "" else tab.url
        )
    }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var totalDragX by remember { mutableFloatStateOf(0f) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (totalDragX > 80f) {
                            viewModel.switchToPrevTab()
                        } else if (totalDragX < -80f) {
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
        tonalElevation = 3.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Shield / AdBlock Button with Badge
                IconButton(
                    onClick = { viewModel.setShieldDialogVisible(true) },
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("shield_button")
                ) {
                    val totalBlocked = tab.blockedAdsCount + tab.blockedTrackersCount
                    BadgedBox(
                        badge = {
                            if (totalBlocked > 0) {
                                Badge(
                                    containerColor = ShieldGreen,
                                    contentColor = Color.Black
                                ) {
                                    Text(
                                        text = if (totalBlocked > 99) "99+" else totalBlocked.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Protección y AdBlock",
                            tint = if (totalBlocked > 0) ShieldGreen else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Main Address / Search Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Left Icon in Box
                        if (tab.isIncognito) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "Pestaña Privada",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        } else if (tab.url.startsWith("https://")) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Conexión Segura HTTPS",
                                tint = ShieldGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Text Field
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .onFocusChanged { focusState ->
                                    isEditing = focusState.isFocused
                                }
                                .testTag("omnibox_input"),
                            singleLine = true,
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus()
                                    viewModel.navigateTo(inputText, selectedCategory)
                                }
                            ),
                            decorationBox = { innerTextField ->
                                if (inputText.isEmpty() && !isEditing) {
                                    Text(
                                        text = "Buscar o ingresar URL...",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                innerTextField()
                            }
                        )

                        // Clear or Refresh Action
                        if (isEditing && inputText.isNotEmpty()) {
                            IconButton(
                                onClick = { inputText = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar texto",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    if (tab.url != "chronion://newtab") {
                                        viewModel.navigateTo(tab.url)
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Recargar página",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Advanced Filter / Search Categories Trigger
                IconButton(
                    onClick = { viewModel.setAdvancedSearchSheetVisible(true) },
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("advanced_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtros de Búsqueda Avanzada",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Quick Category Chips when user is typing / searching
            AnimatedVisibility(
                visible = isEditing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(SearchCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = {
                                onCategorySelected(cat)
                                if (inputText.isNotBlank()) {
                                    focusManager.clearFocus()
                                    viewModel.navigateTo(inputText, cat)
                                }
                            },
                            label = {
                                Text(
                                    text = cat.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = CircleShape
                        )
                    }
                }
            }

            // Linear Progress Indicator
            if (tab.isLoading && tab.progress in 1..99) {
                LinearProgressIndicator(
                    progress = { tab.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}
