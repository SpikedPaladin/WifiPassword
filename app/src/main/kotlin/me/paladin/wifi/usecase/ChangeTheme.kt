package me.paladin.wifi.usecase

import me.paladin.wifi.data.UserPreferencesRepository
import me.paladin.wifi.models.AppTheme

class ChangeTheme(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(theme: AppTheme) =
        userPreferencesRepository.changeTheme(theme)
}