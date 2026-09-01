package com.pokedex.api_pokemon.domain

import com.pokedex.api_pokemon.domain.model.Pokemon
import com.pokedex.core.domain.model.Pagination

interface PokemonRepository {
    suspend fun getFirstPokemonPage(): Result<Pagination>
    suspend fun getNextPokemonPage(url: String): Result<Pagination>
    suspend fun getPokemon(url: String): Result<Pokemon>
    suspend fun searchForPokemonsByNameOrId(text: String): Result<List<Pokemon>>
}
