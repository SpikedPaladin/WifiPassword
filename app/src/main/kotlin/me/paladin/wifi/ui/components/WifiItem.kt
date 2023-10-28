package me.paladin.wifi.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.paladin.wifi.R
import me.paladin.wifi.models.WifiModel

@Composable
fun WifiItem(item: WifiModel) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.ssid, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.size(4.dp))
            Icon(
                painter = painterResource(R.drawable.ic_security),
                modifier = Modifier.size(14.dp),
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = item.type)
        }
        item.user?.let {
            Text(
                text = if (item.type == WifiModel.TYPE_ENTERPRISE)
                    "User: $it"
                else
                    "Key index: $it",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(text = item.password, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview
@Composable
fun WifiItemPreview() {
    WifiItem(WifiModel(ssid = "Example Name", password = "Example Password", type = WifiModel.TYPE_ENTERPRISE, user = "Example User"))
}