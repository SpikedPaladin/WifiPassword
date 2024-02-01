package me.paladin.wifi.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.paladin.wifi.ui.components.sheets.ModalSheet

@Composable
fun ChooseBottomSheet(
    visible: Boolean,
    strings: List<String>,
    chosenIndex: Int,
    chooseCallback: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalSheet(visible = visible, onDismiss = onDismiss) { sheetState ->
        val scope = rememberCoroutineScope()

        LazyColumn(modifier = Modifier.padding(bottom = 16.dp)) {
            itemsIndexed(strings) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable(
                            onClick = {
                                scope
                                    .launch { sheetState.hide() }
                                    .invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            chooseCallback(index)
                                            onDismiss()
                                        }
                                    }
                            }
                        )
                ) {
                    Text(style = MaterialTheme.typography.titleLarge, text = item)

                    if (index == chosenIndex) {
                        Spacer(modifier = Modifier.weight(1F))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}