package com.pokedex.api_pokemon.dao.entities

/**
 * Lightweight projection of [PokemonEntity] for list screens: it deliberately omits the heavy
 * `pokemon_json_data` column so that every Flow emission doesn't read the whole JSON of every row.
 */
data class PokemonListRow(
    val id: Long,
    val name: String,
    val type: String,
    val imageUrl: String?,
    val isLoaded: Boolean
)
