package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TabItem
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.WarningOrange
import com.example.viewmodel.BrowserViewModel

@Composable
fun PageInfoAndShieldDialog(
    tab: TabItem,
    isWhitelisted: Boolean,
    isAdBlockGloballyEnabled: Boolean,
    viewModel: BrowserViewModel,
    onDismiss: () -> Unit
) {
    val domain = try {
        Uri.parse(tab.url).host ?: tab.url
    } catch (e: Exception) {
        tab.url
    }

    val isSecure = tab.url.startsWith("https://")
    val totalBlocked = tab.blockedAdsCount + tab.blockedTrackersCount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (totalBlocked > 0) ShieldGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (totalBlocked > 0) ShieldGreen else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text("Escudo de Seguridad", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(domain, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Connection Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isSecure) Icons.Default.Lock else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isSecure) ShieldGreen else WarningOrange,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = if (isSecure) "Conexión Cifrada (HTTPS)" else "Conexión No Segura (HTTP)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isSecure) "Tus contraseñas y datos viajan cifrados." else "Evita ingresar datos sensibles en este sitio.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Blocked Elements on this Page Breakdown
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Elementos bloqueados en esta página:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Anuncios y Banners:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${tab.blockedAdsCount}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ShieldGreen)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Rastreadores y Cookies espía:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${tab.blockedTrackersCount}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ShieldGreen)
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Bloqueador para este sitio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(if (isWhitelisted) "Desactivado (Lista blanca)" else "Protección activa", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = !isWhitelisted && isAdBlockGloballyEnabled,
                                onCheckedChange = { viewModel.toggleAdBlockForCurrentDomain() }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}
