package com.pokedex.data_store.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.pokedexWelcomePreferences: DataStore<Preferences> by preferencesDataStore(name = "pokedex_welcome_prefs")

class PokedexWelcomePreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        val IS_POKEMON_DATA_CACHED = booleanPreferencesKey("is_pokemon_data_cached")
    }

    val isPokemonDataCachedFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_POKEMON_DATA_CACHED] ?: false
        }

    suspend fun setPokemonDataCached(isCached: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_POKEMON_DATA_CACHED] = isCached
        }
    }
}
