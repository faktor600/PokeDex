package com.pokedex.compose_core.models

import androidx.compose.runtime.Immutable

@Immutable
data class PokemonCardUiModel(
    val pokemonId: Long,
    val pokemonNumber: String,
    val pokemonName: String,
    val url: String,
)