package me.paladin.wifi.usecase

import kotlinx.coroutines.flow.Flow
import me.paladin.wifi.data.UserPreferencesRepository
import me.paladin.wifi.models.AppTheme

class GetTheme(private val userPreferencesRepository: UserPreferencesRepository) {
    operator fun invoke(): Flow<AppTheme> = userPreferencesRepository.theme
}