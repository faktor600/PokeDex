package com.pokedex.compose_core.models

import androidx.compose.runtime.Immutable

@Immutable
sealed class PokemonCardUiModel {

    abstract val urlId: String

    @Immutable
    data class PokemonSkeleton(
        val pokemonName: String,
        override val urlId: String
    ): PokemonCardUiModel()

    @Immutable
    data class Pokemon(
        override val urlId: String,
        val pokemonNumber: String,
        val pokemonName: String,
        val url: String,
    ): PokemonCardUiModel()
}