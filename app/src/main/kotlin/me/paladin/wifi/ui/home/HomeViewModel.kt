package me.paladin.wifi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.paladin.wifi.Locator
import me.paladin.wifi.models.WifiModel

class HomeViewModel : ViewModel() {
    private val dao = Locator.database.wifiDao()
    private val _selectedId = MutableStateFlow(-1)
    val selectedId: StateFlow<Int> = _selectedId.asStateFlow()

    val items = dao.getAll().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        listOf()
    )

    fun addItem(item: WifiModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.post(item)
            }
        }
    }

    fun updateItem(item: WifiModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.post(item)
            }
        }
    }

    fun removeItem(item: WifiModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.delete(item)
            }
        }
    }

    fun onItemClicked(itemId: Int) {
        _selectedId.update {
            if (itemId == _selectedId.value) -1 else itemId
        }
    }
}