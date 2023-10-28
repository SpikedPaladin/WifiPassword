package me.paladin.wifi.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun WifiActionButton(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    imageVector: ImageVector? = null,
    contentDescription: String? = null,
    onClick: () -> Unit,
    text: String? = null
) {
    Column(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                role = Role.Button,
            )
            .padding(6.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        painter?.let {
            Icon(
                painter = it,
                contentDescription = contentDescription,
                modifier = Modifier.size(30.dp)
            )
        }
        imageVector?.let {
            Icon(
                imageVector = it,
                contentDescription = contentDescription,
                modifier = Modifier.size(30.dp)
            )
        }
        text?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}