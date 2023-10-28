package me.paladin.wifi.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.paladin.wifi.R
import me.paladin.wifi.models.WifiModel

@Composable
fun EditBottomSheet(
    model: WifiModel,
    deleteCallback: () -> Unit,
    saveCallback: (ssid: String, password: String) -> Unit,
    hideCallback: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = hideCallback,
        sheetState = sheetState
    ) {
        var ssid by remember { mutableStateOf(model.ssid) }
        var password by remember { mutableStateOf(model.password) }

        Column(
            modifier = Modifier.padding(
                bottom = 8.dp,
                start = 12.dp,
                end = 12.dp
            )
        ) {
            OutlinedTextField(
                value = ssid,
                onValueChange = { ssid = it },
                label = { Text(text = "SSID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(4.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.size(8.dp))

            Row {
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                deleteCallback()
                                hideCallback()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                ) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Delete")
                }

                Spacer(modifier = Modifier.size(4.dp))

                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                saveCallback(ssid, password)
                                hideCallback()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                ) {
                    Icon(painter = painterResource(R.drawable.ic_save), contentDescription = "Save")
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Save")
                }
            }
        }
    }
}