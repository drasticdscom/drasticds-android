package com.drasticds.emulator.domain.repositories

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val isGridView: Flow<Boolean>
    suspend fun setGridView(isGridView: Boolean)
}
