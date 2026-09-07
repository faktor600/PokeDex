package com.pokedex.compose_core.models

import androidx.compose.runtime.Immutable

@Immutable
sealed class PokemonCardUiModel {

    abstract val id: Long

    @Immutable
    data class PokemonSkeleton(
        val pokemonName: String,
        override val id: Long
    ): PokemonCardUiModel()

    @Immutable
    data class Pokemon(
        override val id: Long,
        val pokemonNumber: String,
        val pokemonName: String,
        val url: String,
    ): PokemonCardUiModel()
}