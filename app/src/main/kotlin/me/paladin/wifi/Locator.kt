package me.paladin.wifi

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import me.paladin.wifi.data.UserPreferencesRepository
import me.paladin.wifi.data.WifiDatabase
import me.paladin.wifi.ui.main.viewmodels.ThemeViewModel
import me.paladin.wifi.usecase.*

object Locator {
    private var application: Application? = null
    lateinit var database: WifiDatabase

    private inline val requireApplication
        get() = application ?: error("Missing call: initWith(application)")

    fun initWith(application: Application) {
        this.application = application

        database = Room.databaseBuilder(
            application.applicationContext,
            WifiDatabase::class.java,
            WifiDatabase.NAME
        ).build()
    }

    val themeViewModelFactory
        get() = ThemeViewModel.Factory(
            getTheme = getTheme,
            changeTheme = changeTheme,
            getMonet = getMonet,
            changeMonet = changeMonet
        )

    private val changeTheme get() = ChangeTheme(userPreferencesRepository)
    private val getTheme get() = GetTheme(userPreferencesRepository)

    private val changeMonet get() = ChangeMonet(userPreferencesRepository)
    private val getMonet get() = GetMonet(userPreferencesRepository)

    private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    private val userPreferencesRepository by lazy {
        UserPreferencesRepository(requireApplication.dataStore)
    }
}