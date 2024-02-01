package me.paladin.wifi.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.paladin.wifi.models.AppTheme

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val monet = booleanPreferencesKey("monet")
        val theme = stringPreferencesKey("theme")
    }

    private inline val Preferences.monet
        get() = this[Keys.monet] ?: true

    val monet: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences.monet
        }
        .distinctUntilChanged()

    suspend fun changeMonet(monet: Boolean) {
        dataStore.edit { it[Keys.monet] = monet }
    }

    val theme: Flow<AppTheme> = dataStore.data
        .map {
            AppTheme.fromPref(it[Keys.theme])
        }
        .distinctUntilChanged()

    suspend fun changeTheme(theme: AppTheme) {
        dataStore.edit {
            it[Keys.theme] = theme.toPref()
        }
    }
}