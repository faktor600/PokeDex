package com.pokedex.api_pokemon.data

import com.pokedex.api_pokemon.dao.PokemonDao
import com.pokedex.api_pokemon.dao.entities.PokemonEntity
import com.pokedex.api_pokemon.data.api.PokemonApi
import com.pokedex.api_pokemon.data.mapper.toDomain
import com.pokedex.api_pokemon.data.response.PokemonResponse
import com.pokedex.api_pokemon.domain.PokemonRepository
import com.pokedex.api_pokemon.domain.model.Pokemon
import com.pokedex.core.domain.model.Pagination
import com.pokedex.core.network.mapper.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URI
import javax.inject.Inject

internal class PokemonRepositoryImpl @Inject constructor(
    private val api: PokemonApi,
    private val pokemonDao: PokemonDao
) : PokemonRepository {

    private val json = Json {
        ignoreUnknownKeys = true // Важно для API: если API изменится, приложение не упадет
        coerceInputValues = true
    }

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
        val id = getPokemonIdFromUrl(url)

        try {
            val cachedPokemon = pokemonDao.getPokemon(
                id = id,
                name = null
            )

            if (cachedPokemon != null) {
                Result.success(
                    json.decodeFromString<PokemonResponse>(cachedPokemon.json).toDomain()
                )
            } else {
                val response = api.getSinglePokemon(url)
                if (response.isSuccessful) {
                    response.body()?.toDomain()?.let {
                        pokemonDao.insertPokemon(
                            PokemonEntity(
                                id = it.id,
                                name = it.name,
                                json = json.encodeToString(response.body())
                            )
                        )
                        Result.success(it)
                    } ?: error("Empty response")
                } else {
                    error("Failed with ${response.code()} code")
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchForPokemonsByNameOrId(text: String): Result<List<Pokemon>> {
        try {
            if (text.isEmpty()) {
                val result = pokemonDao.getAll().map { json.decodeFromString<PokemonResponse>(it.json).toDomain() }

                return Result.success(
                    result
                )
            }

            val result = pokemonDao.searchForPokemons(text)
                ?.map { json.decodeFromString<PokemonResponse>(it.json).toDomain() }

            return Result.success(
                result ?: emptyList()
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun getPokemonIdFromUrl(url: String): Long {
        return try {
            URI(url)
                .path
                .split("/")
                .lastOrNull { it.isNotEmpty() }
                ?.toLongOrNull() ?:
                error("No pokemon id in $url url")
        } catch (e: Exception) {
            error("Wrong $url")
        }
    }
}
