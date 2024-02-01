package me.paladin.wifi.ui.components.sheets

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.paladin.wifi.R
import me.paladin.wifi.models.WifiModel

@Composable
fun CreateBottomSheet(
    saveCallback: (model: WifiModel) -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    ModalSheet(visible = visible, onDismiss = onDismiss) { sheetState ->
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val listItems = arrayOf(WifiModel.TYPE_WPA, WifiModel.TYPE_WEP, WifiModel.TYPE_ENTERPRISE)

        var expanded by remember { mutableStateOf(false) }
        var wifiType by remember { mutableStateOf(listItems[0]) }

        var ssid by remember { mutableStateOf("") }
        var user by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

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

            AnimatedVisibility(wifiType == WifiModel.TYPE_ENTERPRISE || wifiType == WifiModel.TYPE_WEP, modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.size(4.dp))

                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text(text = if (wifiType == WifiModel.TYPE_ENTERPRISE) "User" else "Key index") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.size(4.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(4.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    value = wifiType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Network type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    listItems.forEach { selectionOption ->
                        DropdownMenuItem(
                            onClick = {
                                wifiType = selectionOption
                                expanded = false
                            },
                            text = {
                                Text(text = selectionOption)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(8.dp))

            Row {
                Button(
                    onClick = {
                        var valid = true
                        var errorText = ""

                        if (ssid == "" || password == "") {
                            valid = false
                            errorText = "Fill all fields!"
                        }

                        if (wifiType != WifiModel.TYPE_WPA) {
                            if (user == "") {
                                valid = false
                                errorText = "Fill all fields!"
                            } else if (wifiType == WifiModel.TYPE_WEP) {
                                try {
                                    if (user.toInt() > 4 || user.toInt() < 1) {
                                        valid = false
                                        errorText = "Key index must be not greater than 4 and not lower than 1"
                                    }
                                } catch (_: Exception) {
                                    valid = false
                                    errorText = "Key index must be number not be greater than 4 and not lower than 1"
                                }
                            }
                        }

                        if (valid) scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                saveCallback(WifiModel(
                                    ssid = ssid,
                                    password = password,
                                    type = wifiType,
                                    user = if (wifiType != WifiModel.TYPE_WPA) user else null
                                ))
                                onDismiss()
                            }
                        } else Toast.makeText(context, errorText, Toast.LENGTH_LONG).show()
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