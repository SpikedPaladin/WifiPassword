package me.paladin.wifi.ui.components.settings.internal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun SettingsTileScaffold(
    enabled: Boolean = true,
    title: @Composable () -> Unit,
    subtitle: @Composable (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    action: (@Composable (Boolean) -> Unit)? = null,
    actionDivider: Boolean = false,
) {
    ListItem(
        headlineContent = {
            WrapContentColor(enabled = enabled) {
                title()
            }
        },
        supportingContent = if (subtitle == null) {
            null
        } else {
            {
                WrapContentColor(enabled = enabled) {
                    subtitle()
                }
            }
        },
        leadingContent = if (icon == null) {
            null
        } else {
            {
                WrapContentColor(enabled = enabled) {
                    icon()
                }
            }
        },
        trailingContent = if (action == null) {
            null
        } else {
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (actionDivider) {
                        val color = DividerDefaults.color.copy(
                            alpha = if (enabled) {
                                1f
                            } else {
                                0.6f
                            },
                        )
                        HorizontalDivider(
                            color = color,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .fillMaxHeight()
                                .width(1.dp),
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    action(enabled)
                }
            }
        },
    )
}