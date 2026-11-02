package com.pokedex.api_pokemon.domain.model

data class PokemonPage(
    val totalCount: Int,
    val loadedCount: Int,
    /** `null` when there are no more pages. */
    val nextUrl: String?
)
