package me.paladin.wifi.ui.components.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import me.paladin.wifi.ui.components.settings.internal.SettingsTileScaffold

@Composable
fun SwitchRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    checked: Boolean,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    subtitle: @Composable (() -> Unit)? = null,
    switchColors: SwitchColors = SwitchDefaults.colors(),
    onCheckedChange: (Boolean) -> Unit = {},
) {
    Surface {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .toggleable(
                    enabled = enabled,
                    value = checked,
                    role = Role.Switch,
                    onValueChange = { onCheckedChange(!checked) },
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsTileScaffold(
                enabled = enabled,
                title = title,
                subtitle = subtitle,
                icon = icon,
                action = {
                    Switch(
                        enabled = enabled,
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors = switchColors,
                    )
                },
            )
        }
    }
}