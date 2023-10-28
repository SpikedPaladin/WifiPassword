package me.paladin.wifi.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import me.paladin.wifi.R

@Composable
fun WifiActions(
    onQrClick: () -> Unit,
    onShareClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onSaveClick: (() -> Unit)? = null
) {
    Row {
        WifiActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            painter = painterResource(id = R.drawable.ic_qr_code),
            text = "QR Code",
            onClick = onQrClick
        )

        WifiActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            painter = painterResource(id = R.drawable.ic_share),
            text = "Share",
            onClick = onShareClick
        )

        if (onEditClick != null) {
            WifiActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                painter = painterResource(id = R.drawable.ic_edit),
                text = "Edit",
                onClick = onEditClick
            )
        }

        if (onSaveClick != null) {
            WifiActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                painter = painterResource(id = R.drawable.ic_save),
                text = "Save",
                onClick = onSaveClick
            )
        }
    }
}