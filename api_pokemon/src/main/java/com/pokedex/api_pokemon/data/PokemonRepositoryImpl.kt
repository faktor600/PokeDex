package com.pokedex.api_pokemon.data

import com.pokedex.api_pokemon.dao.PokemonDao
import com.pokedex.api_pokemon.dao.entities.PokemonEntity
import com.pokedex.api_pokemon.data.api.PokemonApi
import com.pokedex.api_pokemon.data.mapper.toDomain
import com.pokedex.api_pokemon.data.response.PokemonResponse
import com.pokedex.api_pokemon.domain.PokemonRepository
import com.pokedex.api_pokemon.domain.model.FirstPage
import com.pokedex.api_pokemon.domain.model.NextPage
import com.pokedex.api_pokemon.domain.model.PokemonHolder
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
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    override suspend fun getPokemon(id: Long): Result<PokemonHolder.Pokemon> = withContext(Dispatchers.IO) {
        try {
            val cachedPokemon = pokemonDao.getPokemon(
                id = id,
                name = null
            )

            cachedPokemon?.json?.let {
                Result.success(
                    json.decodeFromString<PokemonResponse>(it).toDomain()
                )
            } ?: run {
                val response = api.getSinglePokemon(id)

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

    override suspend fun searchForPokemonsByNameOrId(text: String): Result<List<PokemonHolder>> {
        try {
            if (text.isEmpty()) {
                val result = pokemonDao.getAll().map { pokemonEntity ->
                    pokemonEntity.json?.let {
                        json.decodeFromString<PokemonResponse>(it).toDomain()
                    } ?: PokemonHolder.PokemonNotLoaded(
                        id = pokemonEntity.id,
                        name = pokemonEntity.name
                    )
                }

                return Result.success(
                    result
                )
            }

            val result = pokemonDao.searchForPokemons(text)
                ?.map { pokemonEntity ->
                    pokemonEntity.json?.let {
                        json.decodeFromString<PokemonResponse>(it).toDomain()
                    } ?: PokemonHolder.PokemonNotLoaded(
                        id = pokemonEntity.id,
                        name = pokemonEntity.name
                    )
                }

            return Result.success(
                result ?: emptyList()
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun loadFirstPokemonPageAndSave(): Result<FirstPage> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPokemonPaginationStart()
            if (response.isSuccessful) {
                val response = response.body()
                pokemonDao.insertPokemons(
                    response?.results?.map {
                        PokemonEntity(
                            id = getPokemonIdFromUrl(it.url),
                            name = it.name ?: "",
                            json = null
                        )
                    } ?: emptyList()
                )
                Result.success(
                    FirstPage(
                        pokemonCount = response?.count ?: 0,
                        nextUrl = response?.next ?: ""
                    )
                )
            } else {
                error("Failed with ${response.code()} code")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadPokemonPageAndSave(next: String): Result<NextPage> = withContext(Dispatchers.IO) {
        try {
            val response = api.getNextPokemonPagination(next)
            if (response.isSuccessful) {
                val response = response.body()
                pokemonDao.insertPokemons(
                    response?.results?.map {
                        PokemonEntity(
                            id = getPokemonIdFromUrl(it.url),
                            name = it.name ?: "",
                            json = null
                        )
                    } ?: emptyList()
                )
                Result.success(NextPage(
                    nextUrl = response?.next ?: "",
                    loadedCount = response?.results?.size ?: 0
                ))
            } else {
                error("Failed with ${response.code()} code")
            }
        } catch (e: Exception) {
            Result.failure(e)
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
