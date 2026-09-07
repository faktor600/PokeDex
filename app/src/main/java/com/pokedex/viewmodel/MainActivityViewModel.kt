package com.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import com.pokedex.data_store.prefs.PokedexWelcomePreferences
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val welcomeInitializedDataStore: PokedexWelcomePreferences
): ViewModel() {

    val isInitialized = welcomeInitializedDataStore.isPokemonDataCachedFlow
}