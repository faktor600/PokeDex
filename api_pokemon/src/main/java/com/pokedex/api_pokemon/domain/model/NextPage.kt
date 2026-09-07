package com.pokedex.api_pokemon.domain.model

data class NextPage(
    val nextUrl: String,
    val loadedCount: Int
)