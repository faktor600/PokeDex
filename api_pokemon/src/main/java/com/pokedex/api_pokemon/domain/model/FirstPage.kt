package com.pokedex.api_pokemon.domain.model

data class FirstPage(
    val pokemonCount: Int,
    val nextUrl: String
)