package me.paladin.wifi.ui.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.paladin.wifi.models.AppTheme
import me.paladin.wifi.usecase.ChangeMonet
import me.paladin.wifi.usecase.ChangeTheme
import me.paladin.wifi.usecase.GetMonet
import me.paladin.wifi.usecase.GetTheme

class ThemeViewModel(
    getTheme: GetTheme,
    private val _changeTheme: ChangeTheme,
    getMonet: GetMonet,
    private val _changeMonet: ChangeMonet
) : ViewModel() {
    val theme: StateFlow<AppTheme> = getTheme().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AppTheme.AUTO,
    )
    val monet: StateFlow<Boolean> = getMonet().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )

    fun changeTheme(theme: AppTheme) {
        viewModelScope.launch { _changeTheme(theme) }
    }

    fun changeMonet(monet: Boolean) {
        viewModelScope.launch { _changeMonet(monet) }
    }

    class Factory(
        private val getTheme: GetTheme,
        private val changeTheme: ChangeTheme,
        private val getMonet: GetMonet,
        private val changeMonet: ChangeMonet

    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ThemeViewModel(
                    getTheme = getTheme,
                    _changeTheme = changeTheme,
                    getMonet = getMonet,
                    _changeMonet = changeMonet
                ) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }
}