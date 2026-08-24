package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TabItem
import com.example.viewmodel.BrowserViewModel

@Composable
fun FindInPageBar(
    tab: TabItem,
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf(tab.findQuery) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            BasicTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.setFindInPage(active = true, query = it)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .testTag("find_in_page_input"),
                singleLine = true,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.setFindInPage(active = true, query = query)
                    }
                ),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            "Buscar en esta página...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            )

            if (tab.findMatchesCount > 0) {
                Text(
                    text = "${tab.findActiveMatchOrdinal + 1}/${tab.findMatchesCount}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = { viewModel.setFindInPage(false) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar búsqueda",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
