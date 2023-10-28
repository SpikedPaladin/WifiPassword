package me.paladin.wifi.usecase

import me.paladin.wifi.data.UserPreferencesRepository

class ChangeMonet(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(monet: Boolean) =
        userPreferencesRepository.changeMonet(monet)
}