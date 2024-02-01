package me.paladin.wifi.ui.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.paladin.wifi.WifiPassword
import me.paladin.wifi.models.AppTheme

class ThemeViewModel : ViewModel() {
    val theme: StateFlow<AppTheme> = WifiPassword.prefs.theme.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AppTheme.AUTO,
    )
    val monet: StateFlow<Boolean> = WifiPassword.prefs.monet.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )

    fun changeTheme(theme: AppTheme) {
        viewModelScope.launch { WifiPassword.prefs.changeTheme(theme) }
    }

    fun changeMonet(monet: Boolean) {
        viewModelScope.launch { WifiPassword.prefs.changeMonet(monet) }
    }
}