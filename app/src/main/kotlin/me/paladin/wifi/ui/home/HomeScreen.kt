package me.paladin.wifi.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.wifi.ui.components.AboutDialog
import me.paladin.wifi.ui.components.ExpandableCard
import me.paladin.wifi.ui.components.WifiActions
import me.paladin.wifi.ui.components.WifiItem
import me.paladin.wifi.ui.components.sheets.CreateBottomSheet

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    settingsAction: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val selectedId by viewModel.selectedId.collectAsState()
    val items by viewModel.items.collectAsState()
    val context = LocalContext.current
    var showCreate by remember { mutableStateOf(false) }
    var openAbout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HomeTopBar(
                scrollBehavior,
                settingsAction = settingsAction,
                aboutAction = { openAbout = true },
                rateAppAction = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=me.paladin.wifi"))
                    )
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },

        contentWindowInsets = WindowInsets.statusBars,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(bottom = 48.dp)
        ) {
            itemsIndexed(items) { index, item ->
                ExpandableCard(
                    expandedContent = {
                        WifiActions(
                            item = item,
                            onDelete = {
                                viewModel.onItemClicked(-1)
                                viewModel.removeItem(item)
                            },
                            onEdit = { ssid, password ->
                                viewModel.updateItem(item.apply {
                                    this.ssid = ssid
                                    this.password = password
                                })
                            }
                        )
                    },

                    onCardClicked = { viewModel.onItemClicked(index) },
                    expanded = selectedId == index
                ) {
                    WifiItem(item)
                }
            }
        }

        if (openAbout)
            AboutDialog {
                openAbout = false
            }

        CreateBottomSheet(visible = showCreate, saveCallback = {
            viewModel.addItem(it)
        }) {
            showCreate = false
        }
    }
}

@Composable
fun HomeTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    settingsAction: () -> Unit,
    rateAppAction: () -> Unit,
    aboutAction: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = "Home")
        },

        actions = {
            IconButton(onClick = settingsAction) {
                Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Open settings")
            }
            IconButton(onClick = rateAppAction) {
                Icon(imageVector = Icons.Outlined.Star, contentDescription = "Rate app")
            }
            IconButton(onClick = aboutAction) {
                Icon(imageVector = Icons.Outlined.Info, contentDescription = "Show About")
            }
        },
        scrollBehavior = scrollBehavior
    )
}