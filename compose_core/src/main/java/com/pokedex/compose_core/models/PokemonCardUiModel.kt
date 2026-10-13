package com.pokedex.compose_core.models

import androidx.compose.runtime.Immutable
import com.pokedex.core.domain.model.PokemonId

@Immutable
sealed class PokemonCardUiModel {

    abstract val id: PokemonId

    @Immutable
    data class PokemonSkeleton(
        val pokemonName: String,
        override val id: PokemonId
    ): PokemonCardUiModel()

    @Immutable
    data class Pokemon(
        override val id: PokemonId,
        val pokemonNumber: String,
        val pokemonName: String,
        val url: String,
    ): PokemonCardUiModel()
}