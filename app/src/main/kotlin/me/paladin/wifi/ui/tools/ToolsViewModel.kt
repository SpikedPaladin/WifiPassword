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
import kotlinx.coroutines.withContext
import me.paladin.wifi.Locator
import me.paladin.wifi.data.RootRepository
import me.paladin.wifi.models.WifiModel

class ToolsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private val _selectedId = MutableStateFlow(-1)
    val selectedId: StateFlow<Int> = _selectedId.asStateFlow()

    fun checkRootAndLoad() {
        _uiState.update { UiState.Loading }
        Shell.getShell { shell ->
            if (shell.isRoot) {
                _uiState.update { UiState.Success(RootRepository.loadWifi()) }
            } else
                _uiState.update { UiState.Error(noRoot = true) }
        }
    }

    fun saveItem(item: WifiModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Locator.database.wifiDao().post(item)
            }
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