package com.pokedex.splash_screen.state

import androidx.compose.runtime.Immutable

@Immutable
internal sealed class SplashScreenUiState {
    data object Loading: SplashScreenUiState()

    data class Ready(
        val isPokedexDatabaseFilled: Boolean = false
    ): SplashScreenUiState()
}
