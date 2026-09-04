package com.pokedex.data_store.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.pokedex.data_store.prefs.PokedexWelcomePreferences
import com.pokedex.data_store.prefs.pokedexWelcomePreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PokedexWelcomeDataStoreModule {
    @Provides
    @Singleton
    fun provideDatastorePokedexWelcome(
        context: Context
    ) = context.pokedexWelcomePreferences

    @Provides
    @Singleton
    fun providePokedexWelcomePreferences(
        dataStore: DataStore<Preferences>
    ) = PokedexWelcomePreferences(dataStore)
}