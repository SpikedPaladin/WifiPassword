package me.paladin.wifi

import android.app.Application
import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import me.paladin.wifi.data.UserPreferencesRepository
import me.paladin.wifi.data.WifiDatabase

class WifiPassword : Application() {

    init { application = this }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            application.applicationContext,
            WifiDatabase::class.java,
            WifiDatabase.NAME
        ).build()
    }

    companion object {
        lateinit var application: WifiPassword
        lateinit var database: WifiDatabase

        private val Context.dataStore by preferencesDataStore(
            name = "user_preferences",
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
        )

        val prefs by lazy {
            UserPreferencesRepository(application.dataStore)
        }
    }
}