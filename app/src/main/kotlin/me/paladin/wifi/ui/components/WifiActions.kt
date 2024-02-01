package me.paladin.wifi.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import me.paladin.wifi.R
import me.paladin.wifi.models.WifiModel
import me.paladin.wifi.ui.components.sheets.EditBottomSheet
import me.paladin.wifi.ui.components.sheets.QRBottomSheet

@Composable
fun WifiActions(
    item: WifiModel,
    onEdit: ((ssid: String, password: String) -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onSave: (() -> Unit)? = null
) {
    var showQr by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Row {
        WifiActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            painter = painterResource(id = R.drawable.ic_qr_code),
            text = "QR Code",
            onClick = { showQr = true }
        )

        WifiActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            painter = painterResource(id = R.drawable.ic_share),
            text = "Share",
            onClick = {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        item.toString()
                    )
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }
        )

        if (onEdit != null && onDelete != null) {
            WifiActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                painter = painterResource(id = R.drawable.ic_edit),
                text = "Edit",
                onClick = { showEdit = true }
            )

            EditBottomSheet(model = item, onDelete = onDelete, onEdit = onEdit, visible = showEdit) {
                showEdit = false
            }
        }

        if (onSave != null) {
            WifiActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                painter = painterResource(id = R.drawable.ic_save),
                text = "Save",
                onClick = onSave
            )
        }
    }

    QRBottomSheet(model = item, visible = showQr) {
        showQr = false
    }
}