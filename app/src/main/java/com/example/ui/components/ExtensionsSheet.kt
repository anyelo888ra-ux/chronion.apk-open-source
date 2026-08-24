package com.example.ui.components

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserScript
import com.example.viewmodel.BrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionsSheet(
    scripts: List<UserScript>,
    viewModel: BrowserViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var editingScript by remember { mutableStateOf<UserScript?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

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
                        imageVector = Icons.Default.Extension,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Extensiones & Scripts (${scripts.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row {
                    IconButton(onClick = { isCreatingNew = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo Script", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            }

            Text(
                text = "Personaliza el comportamiento web, inyecta JavaScript y agrega funciones adicionales a cualquier sitio.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Scripts List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(scripts, key = { it.id }) { script ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = if (script.isEnabled) 0.8f else 0.4f
                            )
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = script.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "v${script.version}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = script.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Switch(
                                    checked = script.isEnabled,
                                    onCheckedChange = { viewModel.toggleScript(script) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Patrón: ${script.matchUrlPattern}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )

                                Row {
                                    IconButton(
                                        onClick = { editingScript = script },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                                    }
                                    if (!script.isBuiltIn) {
                                        IconButton(
                                            onClick = { viewModel.deleteScript(script) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
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

    // Editor Dialog for Creating / Editing UserScripts
    val activeEdit = editingScript ?: if (isCreatingNew) UserScript(
        id = 0L,
        name = "Nuevo UserScript",
        description = "Script personalizado",
        author = "Usuario",
        version = "1.0",
        matchUrlPattern = "*",
        jsCode = "// Escribe tu código JavaScript aquí\nconsole.log('Chronioñ script ejecutado');",
        isEnabled = true,
        isBuiltIn = false
    ) else null

    if (activeEdit != null) {
        var scriptName by remember(activeEdit) { mutableStateOf(activeEdit.name) }
        var scriptDesc by remember(activeEdit) { mutableStateOf(activeEdit.description) }
        var scriptPattern by remember(activeEdit) { mutableStateOf(activeEdit.matchUrlPattern) }
        var scriptCode by remember(activeEdit) { mutableStateOf(activeEdit.jsCode) }

        AlertDialog(
            onDismissRequest = {
                editingScript = null
                isCreatingNew = false
            },
            title = { Text(if (activeEdit.id == 0L) "Crear Extensión / Script" else "Editar Extensión") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = scriptName,
                        onValueChange = { scriptName = it },
                        label = { Text("Nombre del Script") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = scriptPattern,
                        onValueChange = { scriptPattern = it },
                        label = { Text("URL / Dominio Match (* para todos)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = scriptCode,
                        onValueChange = { scriptCode = it },
                        label = { Text("Código JavaScript") },
                        maxLines = 8,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val toSave = activeEdit.copy(
                            name = scriptName.ifBlank { "Script" },
                            description = scriptDesc,
                            matchUrlPattern = scriptPattern.ifBlank { "*" },
                            jsCode = scriptCode
                        )
                        viewModel.saveScript(toSave)
                        editingScript = null
                        isCreatingNew = false
                    }
                ) {
                    Text("Guardar Script")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        editingScript = null
                        isCreatingNew = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
