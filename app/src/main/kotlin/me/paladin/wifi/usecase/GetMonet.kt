package me.paladin.wifi.usecase

import kotlinx.coroutines.flow.Flow
import me.paladin.wifi.data.UserPreferencesRepository

class GetMonet(private val userPreferencesRepository: UserPreferencesRepository) {
    operator fun invoke(): Flow<Boolean> = userPreferencesRepository.monet
}