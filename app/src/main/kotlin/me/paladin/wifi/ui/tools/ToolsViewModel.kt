package me.paladin.wifi.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.paladin.wifi.WifiPassword
import me.paladin.wifi.data.RootRepository
import me.paladin.wifi.models.WifiModel

class ToolsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    private val _selectedId = MutableStateFlow(-1)
    val selectedId: StateFlow<Int> = _selectedId.asStateFlow()
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun checkRootAndLoad() = viewModelScope.launch(Dispatchers.IO) {
        _uiState.update { UiState.Loading }
        Shell.getShell { shell ->
            if (shell.isRoot) {
                _uiState.update { UiState.Success(RootRepository.loadWifi()) }
            } else
                _uiState.update { UiState.Error(noRoot = true) }
        }
    }

    fun saveItem(item: WifiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            WifiPassword.database.wifiDao().post(item)
        }
    }

    fun onItemClicked(itemId: Int) {
        _selectedId.update {
            if (itemId == _selectedId.value) -1 else itemId
        }
    }
}

sealed interface UiState {
    data object Idle : UiState
    data object Loading : UiState

    data class Success(
        val data: List<WifiModel>
    ) : UiState

    data class Error(
        val noRoot: Boolean = false
    ) : UiState
}