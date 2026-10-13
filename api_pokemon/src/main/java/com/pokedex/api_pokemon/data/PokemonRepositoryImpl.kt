package com.pokedex.api_pokemon.data

import com.pokedex.api_pokemon.dao.PokemonDao
import com.pokedex.api_pokemon.dao.entities.PokemonEntity
import com.pokedex.api_pokemon.data.api.PokemonApi
import com.pokedex.api_pokemon.data.mapper.toDomain
import com.pokedex.api_pokemon.data.response.PokemonResponse
import com.pokedex.api_pokemon.data.response.PokemonSpeciesResponse
import com.pokedex.api_pokemon.domain.PokemonRepository
import com.pokedex.api_pokemon.domain.model.FirstPage
import com.pokedex.api_pokemon.domain.model.NextPage
import com.pokedex.api_pokemon.domain.model.Pokemon
import com.pokedex.api_pokemon.domain.model.PokemonSearchItem
import com.pokedex.api_pokemon.domain.model.PokemonSpecies
import com.pokedex.core.PokemonTypeEnum
import com.pokedex.core.asStringBackend
import com.pokedex.core.domain.model.PokemonId
import com.pokedex.core.getTypeEnum
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

internal class PokemonRepositoryImpl @Inject constructor(
    private val api: PokemonApi,
    private val pokemonDao: PokemonDao
) : PokemonRepository {

    companion object {
        private val loadingPokemonsMap = ConcurrentHashMap<PokemonId, Job>()

        private val savePokemonSemaphore = Semaphore(20)

        private val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    override suspend fun getPokemonSpecies(url: String): Result<PokemonSpecies> = withContext(Dispatchers.IO) {
        try {
            val species = api.getSpeciesByUrl(url)

            species.body()?.string()?.let {
                withContext(Dispatchers.Default) {
                    Result.success(
                        json.decodeFromString<PokemonSpeciesResponse>(it).toDomain()
                    )
                }
            } ?: Result.failure(IllegalStateException("Null string body for pokemon species"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPokemon(id: PokemonId): Result<Pokemon> = withContext(Dispatchers.IO) {
        try {
            loadingPokemonsMap[id]?.join()

            val cachedPokemon = pokemonDao.getPokemon(
                id = id.id,
                name = null
            )

            cachedPokemon?.json?.let {
                withContext(Dispatchers.Default) {
                    Result.success(
                        json.decodeFromString<PokemonResponse>(it).toDomain()
                    )
                }
            } ?: run {
                val response = api.getSinglePokemonRaw(id)

                if (response.isSuccessful) {
                    response.body()?.let {
                        val jsonRaw = it.string()
                        val pokemonResponse = json.decodeFromString<PokemonResponse>(jsonRaw)

                        withContext(NonCancellable) {
                            savePokemonToDb(jsonRaw)
                        }

                        Result.success(pokemonResponse.toDomain())
                    } ?: Result.failure(IllegalStateException("Empty response"))
                } else {
                    Result.failure(IllegalStateException("Failed with ${response.code()} code"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    val pokemonSaveScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun searchForPokemonsByNameOrId(text: String): Flow<List<PokemonSearchItem>> {
        val pokemonMapScope = CoroutineScope(Dispatchers.Default)

        return if (text.isEmpty()) {
            pokemonDao.getAll()
        } else {
            pokemonDao.searchForPokemons(text)
        }
            .map { pokemonList ->
                pokemonList.map { pokemonEntity ->
                    if (pokemonEntity.type != PokemonTypeEnum.UNKNOWN.asStringBackend()) {
                        PokemonSearchItem.LoadedPokemon(
                            id = PokemonId(pokemonEntity.id),
                            name = pokemonEntity.name,
                            type = pokemonEntity.type.getTypeEnum(),
                            pokemonMainImageUrl = pokemonEntity.pokemonMainImageUrl ?: ""
                        )
                    } else {
                        val pokemonId = PokemonId(pokemonEntity.id)
                        PokemonSearchItem.NotLoaded(
                            id = pokemonId,
                            name = pokemonEntity.name,
                        ).also {
                            pokemonSaveScope.launch {
                                savePokemonSemaphore.withPermit {
                                    val response = api.getSinglePokemonRaw(pokemonId)

                                    if (response.isSuccessful) {
                                        response.body()?.let { pokemonResponse ->

                                            val jsonString = pokemonResponse.string()
                                            val pokemonResponse = json.decodeFromString<PokemonResponse>(jsonString)

                                            pokemonDao.insertPokemon(
                                                PokemonEntity(
                                                    id = pokemonResponse.id.id,
                                                    name = pokemonResponse.name,
                                                    type = pokemonResponse.types.first().type.name ?: PokemonTypeEnum.UNKNOWN.asStringBackend(),
                                                    pokemonMainImageUrl = pokemonResponse.sprites.other.officialArtwork?.frontDefault ?: "",
                                                    json = jsonString
                                                )
                                            )
                                        } ?: error("Empty response")
                                    } else {
                                        error("Failed with ${response.code()} code")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .catch { exception ->
                if (exception is CancellationException) throw exception

                emit(emptyList())
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun loadFirstPokemonPageAndSave(): Result<FirstPage> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPokemonPaginationStart()

            if (response.isSuccessful) {
                val response = response.body()

                pokemonDao.insertPokemons(
                    response?.results?.map {
                        PokemonEntity(
                            id = getPokemonIdFromUrl(it.url).id,
                            name = it.name ?: "",
                            type = PokemonTypeEnum.UNKNOWN.asStringBackend(),
                            pokemonMainImageUrl = null,
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
                Result.failure(IllegalStateException("Failed with ${response.code()} code"))
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
                            id = getPokemonIdFromUrl(it.url).id,
                            name = it.name ?: "",
                            type = PokemonTypeEnum.UNKNOWN.asStringBackend(),
                            pokemonMainImageUrl = null,
                            json = null
                        )
                    } ?: emptyList()
                )

                Result.success(
                    NextPage(
                        nextUrl = response?.next ?: "",
                        loadedCount = response?.results?.size ?: 0
                    )
                )
            } else {
                Result.failure(IllegalStateException("Failed with ${response.code()} code"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getAndSavePokemon(pokemonId: PokemonId) {
        val response = api.getSinglePokemonRaw(pokemonId)

        if (response.isSuccessful) {
            response.body()?.let { pokemonResponse ->

                savePokemonToDb(pokemonResponse.string())
            } ?: error("Empty response")
        } else {
            error("Failed with ${response.code()} code")
        }
    }

    private suspend fun savePokemonToDb(jsonString: String) {
        val pokemonResponse = json.decodeFromString<PokemonResponse>(jsonString)

        pokemonDao.insertPokemon(
            PokemonEntity(
                id = pokemonResponse.id.id,
                name = pokemonResponse.name,
                type = pokemonResponse.types.first().type.name ?: PokemonTypeEnum.UNKNOWN.asStringBackend(),
                pokemonMainImageUrl = pokemonResponse.sprites.other.officialArtwork?.frontDefault ?: "",
                json = jsonString
            )
        )
    }

    private fun getPokemonIdFromUrl(url: String): PokemonId {
        val id = URI(url)
            .path
            .split("/")
            .lastOrNull { it.isNotEmpty() }
            ?.toLongOrNull()
            ?: error("No pokemon id in $url url")

        return PokemonId(id)
    }
}
