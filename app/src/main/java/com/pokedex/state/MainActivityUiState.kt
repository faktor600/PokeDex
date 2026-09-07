package com.pokedex.state

import androidx.compose.runtime.Immutable

@Immutable
internal data class MainActivityUiState(
    val needToNavigateToWelcomeScreen: Boolean = false
)