package com.pokedex.pokedex_welcome.state

import androidx.compose.runtime.Immutable

@Immutable
internal sealed class PokedexWelcomeUiState {

    data object Welcome: PokedexWelcomeUiState()

    data class LoadingDatabase(
        val progress: Float = 0.0f,
        val progressText: String = "0%"
    ): PokedexWelcomeUiState()

    data object DatabaseLoadedSuccessfully: PokedexWelcomeUiState()

    data object DatabaseLoadFailed: PokedexWelcomeUiState()
}