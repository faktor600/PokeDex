package com.pokedex.api_pokemon.domain.model

import com.pokedex.core.PokemonTypeEnum
import com.pokedex.core.domain.model.PokemonId

sealed class PokemonSearchItem {
    abstract val id: PokemonId
    abstract val name: String
    abstract val type: PokemonTypeEnum

    data class LoadedPokemon(
        override val id: PokemonId,
        override val name: String,
        override val type: PokemonTypeEnum,
        val pokemonMainImageUrl: String?,
    ): PokemonSearchItem()

    data class NotLoaded(
        override val id: PokemonId,
        override val name: String,
        override val type: PokemonTypeEnum = PokemonTypeEnum.UNKNOWN,
    ): PokemonSearchItem()
}