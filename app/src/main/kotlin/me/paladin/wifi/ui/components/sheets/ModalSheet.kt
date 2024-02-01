package me.paladin.wifi.ui.components.sheets

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ModalSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.(SheetState) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(visible) {
        if (visible) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    if (!visible) {
        return
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) { content(sheetState) }
}