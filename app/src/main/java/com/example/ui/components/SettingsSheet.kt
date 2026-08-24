package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DarkThemeStyle
import com.example.data.model.SearchEngine
import com.example.ui.theme.CosmicBackground
import com.example.ui.theme.CosmicPrimary
import com.example.ui.theme.EmeraldBackground
import com.example.ui.theme.OledBackground
import com.example.ui.theme.SepiaBackground
import com.example.ui.theme.ShieldGreen
import com.example.viewmodel.BrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    viewModel: BrowserViewModel,
    darkThemeStyle: DarkThemeStyle,
    isEyeCareEnabled: Boolean,
    eyeCareWarmth: Float,
    isAdBlockEnabled: Boolean,
    blockTrackers: Boolean,
    blockPopups: Boolean,
    forceWebDarkMode: Boolean,
    searchEngine: SearchEngine,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showClearConfirm by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Configuración & Temas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 28.dp)
            ) {
                // Section: Tema y Reducción de Fatiga Visual
                item {
                    Text(
                        text = "🎨 Modo Oscuro Personalizable",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DarkThemeStyle.values().forEach { style ->
                            val isSelected = darkThemeStyle == style
                            val previewBg = when (style) {
                                DarkThemeStyle.COSMIC_INDIGO -> CosmicBackground
                                DarkThemeStyle.OLED_PITCH_BLACK -> OledBackground
                                DarkThemeStyle.WARM_SEPIA -> SepiaBackground
                                DarkThemeStyle.CYBER_EMERALD -> EmeraldBackground
                                DarkThemeStyle.DYNAMIC_SYSTEM -> Color(0xFF2C2C2E)
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.setDarkThemeStyle(style) },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(previewBg)
                                                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                        )
                                        Column {
                                            Text(style.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(style.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Seleccionado",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section: Filtro de Luz Azul / Cuidado Ocular
                item {
                    Text(
                        text = "👁️ Cuidado Visual & Filtro Cálido",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Filtro Nocturno Antifatiga", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Atenúa la luz azul para proteger la vista en ambientes oscuros.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = isEyeCareEnabled,
                                    onCheckedChange = { viewModel.setEyeCareEnabled(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            if (isEyeCareEnabled) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Calidez del color", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${(eyeCareWarmth * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Slider(
                                        value = eyeCareWarmth,
                                        onValueChange = { viewModel.setEyeCareWarmth(it) },
                                        valueRange = 0.1f..0.7f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = MaterialTheme.colorScheme.primary,
                                            activeTrackColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Modo Oscuro Forzado en Sitios Web", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Transforma páginas web con fondo claro a tonos oscuros.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = forceWebDarkMode,
                                    onCheckedChange = { viewModel.setForceWebDarkMode(it) }
                                )
                            }
                        }
                    }
                }

                // Section: Bloqueo de Anuncios y Rastreadores
                item {
                    Text(
                        text = "🛡️ Bloqueador Nativo de Anuncios",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Bloqueo de Anuncios Nativo", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Filtra banners publicitarios, popups y anuncios intrusivos.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = isAdBlockEnabled,
                                    onCheckedChange = { viewModel.setAdBlockEnabled(it) }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Bloquear Rastreadores y Telemetría", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Impide el seguimiento de actividad de terceros.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = blockTrackers,
                                    onCheckedChange = { viewModel.setBlockTrackers(it) }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Bloquear Ventanas Emergentes (Popups)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Evita redirecciones y pestañas automáticas indeseadas.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = blockPopups,
                                    onCheckedChange = { viewModel.setBlockPopups(it) }
                                )
                            }
                        }
                    }
                }

                // Section: Datos y Limpieza
                item {
                    Text(
                        text = "🗑️ Privacidad y Almacenamiento",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                item {
                    Button(
                        onClick = { viewModel.clearBrowsingHistory() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Borrar Historial de Navegación")
                    }
                }
            }
        }
    }
}
