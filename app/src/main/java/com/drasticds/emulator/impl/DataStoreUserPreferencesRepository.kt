package com.drasticds.emulator.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.drasticds.emulator.domain.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class DataStoreUserPreferencesRepository(
    private val context: Context
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val IS_GRID_VIEW = booleanPreferencesKey("is_grid_view")
    }

    override val isGridView: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_GRID_VIEW] ?: true
        }

    override suspend fun setGridView(isGridView: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_GRID_VIEW] = isGridView
        }
    }
}
