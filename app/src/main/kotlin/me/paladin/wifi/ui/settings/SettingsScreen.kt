package me.paladin.wifi.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.wifi.models.AppTheme
import me.paladin.wifi.ui.components.settings.ChooseRow
import me.paladin.wifi.ui.components.settings.SwitchRow
import me.paladin.wifi.ui.main.viewmodels.ThemeViewModel

@Composable
fun SettingsScreen() {
    val viewModel: ThemeViewModel = viewModel()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val theme by viewModel.theme.collectAsState()
    val monet by viewModel.monet.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopBar(scrollBehavior)
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            ChooseRow(
                strings = listOf("Day", "Night", "Auto"),
                title = { Text(text = "App Theme") },
                subtitle = {
                    Text(theme.toString())
                },
                chosenIndex = theme.toIndex(),
                onChoose = {
                    viewModel.changeTheme(AppTheme.fromIndex(it))
                }
            )
            SwitchRow(
                checked = monet,
                title = { Text(text = "Dynamic colors") },
                subtitle = { Text(text = "Only for Android 12+") },
                onCheckedChange = { viewModel.changeMonet(!monet) }
            )
        }
    }
}

@Composable
fun SettingsTopBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(text = "Settings")
        },
        scrollBehavior = scrollBehavior
    )
}