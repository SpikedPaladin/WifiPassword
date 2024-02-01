package me.paladin.wifi.ui.tools

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.paladin.wifi.ui.components.ExpandableCard
import me.paladin.wifi.ui.components.WifiActions
import me.paladin.wifi.ui.components.WifiItem

@Composable
fun ToolsScreen(
    viewModel: ToolsViewModel = viewModel(),
    settingsAction: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val selectedId by viewModel.selectedId.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            ToolsTopBar(
                scrollBehavior = scrollBehavior,
                settingsAction = settingsAction
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState) {
                UiState.Idle -> {
                    Button(onClick = { viewModel.checkRootAndLoad() }) {
                        Text(text = "Search passwords in system")
                    }

                    Text(text = "*Requires root access")
                }
                is UiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed((uiState as UiState.Success).data) { index, item ->
                            ExpandableCard(
                                expandedContent = {
                                    WifiActions(
                                        item = item,
                                        onSave = {
                                            viewModel.saveItem(item)
                                            Toast.makeText(context, "Saved to Home screen", Toast.LENGTH_SHORT).show()
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
                }
                is UiState.Error -> {
                    Text(text = "Root access not found.")
                    Button(onClick = { viewModel.checkRootAndLoad() }) {
                        Text(text = "Try again")
                    }
                }
                UiState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Loading")
                }
            }
        }
    }
}

@Composable
fun ToolsTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    settingsAction: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = "Search")
        },
        actions = {
            IconButton(onClick = settingsAction) {
                Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Open settings")
            }
        },
        scrollBehavior = scrollBehavior
    )
}