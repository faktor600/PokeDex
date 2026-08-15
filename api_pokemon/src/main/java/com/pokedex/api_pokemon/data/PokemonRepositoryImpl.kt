package com.pokedex.api_pokemon.data

import com.pokedex.api_pokemon.data.api.PokemonApi
import com.pokedex.api_pokemon.data.mapper.toDomain
import com.pokedex.api_pokemon.domain.PokemonRepository
import com.pokedex.api_pokemon.domain.model.Pokemon
import com.pokedex.core.domain.model.Pagination
import com.pokedex.core.network.mapper.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PokemonRepositoryImpl @Inject constructor(
    private val api: PokemonApi,
) : PokemonRepository {

    override suspend fun getFirstPokemonPage(): Result<Pagination> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPokemonPaginationStart()
            if (response.isSuccessful) {
                response.body()?.toDomain()?.let {
                    Result.success(it)
                } ?: error("Empty response")
            } else {
                error("Failed with ${response.code()} code")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNextPokemonPage(url: String): Result<Pagination> = withContext(Dispatchers.IO) {
        try {
            val response = api.getNextPokemonPagination(url)
            if (response.isSuccessful) {
                response.body()?.toDomain()?.let {
                    Result.success(it)
                } ?: error("Empty response")
            } else {
                error("Failed with ${response.code()} code")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPokemon(url: String): Result<Pokemon> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSinglePokemon(url)
            if (response.isSuccessful) {
                response.body()?.toDomain()?.let {
                    Result.success(it)
                } ?: error("Empty response")
            } else {
                error("Failed with ${response.code()} code")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
