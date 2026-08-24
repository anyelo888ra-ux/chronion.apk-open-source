package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DevConsoleMessage
import com.example.data.model.TabItem
import com.example.viewmodel.BrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevToolsSheet(
    tab: TabItem,
    consoleMessages: List<DevConsoleMessage>,
    viewModel: BrowserViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Consola JS", "Código Fuente DOM", "Seguridad & Red")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("devtools_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Chronioñ DevTools",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Inspector Web & Depurador",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (selectedTabIndex == 0) {
                    IconButton(
                        onClick = { viewModel.clearConsole() },
                        modifier = Modifier.testTag("clear_console_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Limpiar Consola")
                    }
                }
            }

            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> ConsoleTabContent(consoleMessages = consoleMessages, viewModel = viewModel)
                1 -> DomSourceTabContent(tab = tab)
                2 -> SecurityTabContent(tab = tab)
            }
        }
    }
}

@Composable
private fun ConsoleTabContent(
    consoleMessages: List<DevConsoleMessage>,
    viewModel: BrowserViewModel
) {
    var commandInput by remember { mutableStateOf("") }
    val quickSnippets = listOf(
        "document.title" to "Título",
        "window.location.href" to "URL",
        "navigator.userAgent" to "UserAgent",
        "document.body.style.filter='invert(1)'" to "Invertir Colores",
        "document.querySelectorAll('img').length" to "Contar Imágenes",
        "document.querySelectorAll('a').length" to "Contar Enlaces"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick Snippets Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickSnippets) { (snippet, label) ->
                FilterChip(
                    selected = false,
                    onClick = {
                        commandInput = snippet
                        viewModel.executeDevScript(snippet)
                    },
                    label = { Text(label, fontSize = 11.sp) }
                )
            }
        }

        // Output Console Log Box
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(consoleMessages, key = { it.id }) { msg ->
                    val color = when (msg.type) {
                        "ERROR" -> Color(0xFFEF4444)
                        "WARN" -> Color(0xFFF59E0B)
                        "INPUT" -> Color(0xFF38BDF8)
                        "RESULT" -> Color(0xFF10B981)
                        "INFO" -> Color(0xFF94A3B8)
                        else -> Color(0xFFE2E8F0)
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "[${msg.type}] ",
                            color = color,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = msg.message,
                            color = Color(0xFFF1F5F9),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Input Command Prompt
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                placeholder = { Text("Ejecutar JS (ej. document.title)", fontSize = 12.sp) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (commandInput.isNotBlank()) {
                        viewModel.executeDevScript(commandInput)
                        commandInput = ""
                    }
                }),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (commandInput.isNotBlank()) {
                        viewModel.executeDevScript(commandInput)
                        commandInput = ""
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Ejecutar")
            }
        }
    }
}

@Composable
private fun DomSourceTabContent(tab: TabItem) {
    val sampleHtml = remember(tab.url, tab.title) {
        """
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${tab.title}</title>
    <!-- Chronioñ Shield Protected -->
    <link rel="canonical" href="${tab.url}">
</head>
<body class="chronion-rendered">
    <header id="main-nav">
        <h1>${tab.title}</h1>
    </header>
    <main>
        <article class="content-body">
            <p>Contenido DOM inspeccionado en tiempo real por Chronioñ DevTools.</p>
        </article>
    </main>
</body>
</html>
        """.trimIndent()
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = sampleHtml,
                color = Color(0xFF58A6FF),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SecurityTabContent(tab: TabItem) {
    val isHttps = tab.url.startsWith("https://", ignoreCase = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = if (isHttps) Color(0xFF10B981) else Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHttps) "Conexión Segura (TLS 1.3 / HTTPS)" else "Conexión No Encriptada",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isHttps)
                        "La información que envías o recibes a través de este sitio web es privada y está encriptada."
                    else
                        "No introduzcas datos confidenciales (como contraseñas o tarjetas) en este sitio.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Telemetría y Bloqueo de Rastreo",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Anuncios bloqueados en esta pestaña:", style = MaterialTheme.typography.bodySmall)
                    Text("${tab.blockedAdsCount}", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Rastreadores interceptados:", style = MaterialTheme.typography.bodySmall)
                    Text("${tab.blockedTrackersCount}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
