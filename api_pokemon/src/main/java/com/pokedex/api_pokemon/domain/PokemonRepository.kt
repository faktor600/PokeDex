package com.pokedex.api_pokemon.domain

import com.pokedex.api_pokemon.domain.model.FirstPage
import com.pokedex.api_pokemon.domain.model.NextPage
import com.pokedex.api_pokemon.domain.model.Pokemon
import com.pokedex.api_pokemon.domain.model.PokemonSearchItem
import com.pokedex.api_pokemon.domain.model.PokemonSpecies
import com.pokedex.core.domain.model.PokemonId
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getPokemon(id: PokemonId): Result<Pokemon>
    suspend fun searchForPokemonsByNameOrId(text: String): Flow<List<PokemonSearchItem>>
    suspend fun loadFirstPokemonPageAndSave(): Result<FirstPage>
    suspend fun loadPokemonPageAndSave(next: String): Result<NextPage>
    suspend fun getPokemonSpecies(url: String): Result<PokemonSpecies>
}
