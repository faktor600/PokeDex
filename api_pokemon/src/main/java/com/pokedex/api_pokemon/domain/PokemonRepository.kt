package com.pokedex.api_pokemon.domain

import com.pokedex.api_pokemon.domain.model.Pokemon
import com.pokedex.api_pokemon.domain.model.PokemonPage
import com.pokedex.api_pokemon.domain.model.PokemonSearchItem
import com.pokedex.api_pokemon.domain.model.PokemonSpecies
import com.pokedex.core.domain.model.PokemonId
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getPokemon(id: PokemonId): Result<Pokemon>
    suspend fun searchForPokemonsByNameOrId(text: String): Flow<List<PokemonSearchItem>>
    /** [url] == null loads the first page. */
    suspend fun loadPokemonPageAndSave(url: String? = null): Result<PokemonPage>
    suspend fun getPokemonSpecies(url: String): Result<PokemonSpecies>
}
