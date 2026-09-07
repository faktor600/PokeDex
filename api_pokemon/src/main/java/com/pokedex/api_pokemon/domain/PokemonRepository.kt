package com.pokedex.api_pokemon.domain

import com.pokedex.api_pokemon.domain.model.FirstPage
import com.pokedex.api_pokemon.domain.model.NextPage
import com.pokedex.api_pokemon.domain.model.PokemonHolder
import com.pokedex.core.domain.model.Pagination

interface PokemonRepository {
    suspend fun getPokemon(id: Long): Result<PokemonHolder.Pokemon>
    suspend fun searchForPokemonsByNameOrId(text: String): Result<List<PokemonHolder>>
    suspend fun loadFirstPokemonPageAndSave(): Result<FirstPage>
    suspend fun loadPokemonPageAndSave(next: String): Result<NextPage>
}
