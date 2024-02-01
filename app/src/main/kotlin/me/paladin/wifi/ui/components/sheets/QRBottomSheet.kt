package me.paladin.wifi.ui.components.sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.paladin.wifi.models.WifiModel
import me.paladin.wifi.ui.components.rememberQrBitmapPainter

@Composable
fun QRBottomSheet(
    model: WifiModel,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    ModalSheet(visible = visible, onDismiss = onDismiss) { sheetState ->
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (model.type != WifiModel.TYPE_ENTERPRISE) {
                Image(
                    painter = rememberQrBitmapPainter(model.toQrString()),
                    contentDescription = "Wifi QR-Code",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.size(150.dp),
                )
            } else {
                Text(text = "EAP Networks currently not supported")
            }

            Button(onClick = {
                scope
                    .launch { sheetState.hide() }
                    .invokeOnCompletion {
                        onDismiss()
                    }
            }) {
                Text("Hide")
            }
        }
    }
}