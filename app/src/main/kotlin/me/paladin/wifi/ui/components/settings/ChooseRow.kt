package me.paladin.wifi.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import me.paladin.wifi.ui.components.settings.internal.SettingsTileScaffold

@Composable
fun ChooseRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    strings: List<String>,
    chosenIndex: Int,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    subtitle: @Composable (() -> Unit)? = null,
    onChoose: (Int) -> Unit = {},
) {
    var openChoose by remember { mutableStateOf(false) }

    Surface {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = { openChoose = true },
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsTileScaffold(
                enabled = enabled,
                title = title,
                subtitle = subtitle,
                icon = icon
            )
        }

        ChooseBottomSheet(
            visible = openChoose,
            strings = strings,
            chosenIndex = chosenIndex,
            chooseCallback = onChoose
        ) {
            openChoose = false
        }
    }
}