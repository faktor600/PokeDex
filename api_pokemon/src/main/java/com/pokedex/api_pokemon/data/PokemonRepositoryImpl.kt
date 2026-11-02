package com.pokedex.api_pokemon.data

import com.pokedex.api_pokemon.dao.PokemonDao
import com.pokedex.api_pokemon.dao.entities.PokemonEntity
import com.pokedex.api_pokemon.data.api.PokemonApi
import com.pokedex.api_pokemon.data.mapper.toDomain
import com.pokedex.api_pokemon.data.response.PokemonResponse
import com.pokedex.api_pokemon.data.response.PokemonSpeciesResponse
import com.pokedex.api_pokemon.domain.PokemonRepository
import com.pokedex.api_pokemon.domain.model.Pokemon
import com.pokedex.api_pokemon.domain.model.PokemonPage
import com.pokedex.api_pokemon.domain.model.PokemonSearchItem
import com.pokedex.api_pokemon.domain.model.PokemonSpecies
import com.pokedex.core.PokemonTypeEnum
import com.pokedex.core.asStringBackend
import com.pokedex.core.domain.model.PokemonId
import com.pokedex.core.getTypeEnum
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.flowOn
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
        /** Fetches that are running right now. Entries are removed on completion (success or failure). */
        private val loadingPokemonsMap = ConcurrentHashMap<PokemonId, Deferred<Unit>>()

        /** Time of the last failed background fetch, used to avoid hammering the API on every Flow emission. */
        private val failedAtMap = ConcurrentHashMap<PokemonId, Long>()
        private const val RETRY_COOLDOWN_MS = 30_000L

        /** Relative to the Retrofit baseUrl. */
        private const val FIRST_PAGE_PATH = "pokemon"

        /** Limits only background (list) fetches, so the details screen is never stuck behind them. */
        private val savePokemonSemaphore = Semaphore(20)

        // async (not launch): exceptions are stored in the Deferred instead of reaching the
        // uncaught exception handler (which would crash the app).
        private val pokemonSaveScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        private val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    override suspend fun getPokemonSpecies(url: String): Result<PokemonSpecies> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSpeciesByUrl(url)

            if (!response.isSuccessful) {
                response.errorBody()?.close()
                return@withContext Result.failure(IllegalStateException("Failed with ${response.code()} code"))
            }

            val body = response.body()?.string()
                ?: return@withContext Result.failure(IllegalStateException("Null string body for pokemon species"))

            Result.success(json.decodeFromString<PokemonSpeciesResponse>(body).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPokemon(id: PokemonId): Result<Pokemon> = withContext(Dispatchers.IO) {
        try {
            // 1. Already cached - no network and no DB rewrite.
            var jsonString = pokemonDao.getById(id.id)?.json

            // 2. Otherwise join (or start) the single fetch for this id. await() rethrows the real cause.
            if (jsonString == null) {
                ensureLoaded(id, background = false).await()
                jsonString = pokemonDao.getById(id.id)?.json
            }

            Result.success(
                json.decodeFromString<PokemonResponse>(jsonString ?: error("No Pokemon Found")).toDomain()
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchForPokemonsByNameOrId(text: String): Flow<List<PokemonSearchItem>> {
        val source = if (text.isEmpty()) {
            pokemonDao.getAll()
        } else {
            pokemonDao.searchForPokemons(
                id = text.toLongOrNull(),
                likePattern = text.escapeLike()
            )
        }

        return source
            .onEach { pokemonList ->
                // Idempotent: already running / recently failed ids are skipped.
                pokemonList.forEach { entity ->
                    if (entity.json == null) ensureLoaded(PokemonId(entity.id), background = true)
                }
            }
            .map { pokemonList ->
                pokemonList.map { pokemonEntity ->
                    val pokemonId = PokemonId(pokemonEntity.id)

                    if (pokemonEntity.json != null) {
                        PokemonSearchItem.LoadedPokemon(
                            id = pokemonId,
                            name = pokemonEntity.name,
                            type = pokemonEntity.type.getTypeEnum(),
                            pokemonMainImageUrl = pokemonEntity.pokemonMainImageUrl ?: ""
                        )
                    } else {
                        PokemonSearchItem.NotLoaded(
                            id = pokemonId,
                            name = pokemonEntity.name,
                        )
                    }
                }
            }
            // DB errors are propagated to the collector instead of silently completing the Flow.
            .flowOn(Dispatchers.IO)
    }

    override suspend fun loadPokemonPageAndSave(url: String?): Result<PokemonPage> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPokemonPage(url ?: FIRST_PAGE_PATH)

            if (!response.isSuccessful) {
                response.errorBody()?.close()
                return@withContext Result.failure(IllegalStateException("Failed with ${response.code()} code"))
            }

            val body = response.body()
                ?: return@withContext Result.failure(IllegalStateException("Empty pagination response"))

            // A single malformed item must not fail the whole page.
            val stubs = body.results.mapNotNull {
                val id = runCatching { getPokemonIdFromUrl(it.url) }.getOrNull() ?: return@mapNotNull null
                PokemonEntity(
                    id = id.id,
                    name = it.name ?: "",
                    type = PokemonTypeEnum.UNKNOWN.asStringBackend(),
                    pokemonMainImageUrl = null,
                    json = null
                )
            }

            // IGNORE on conflict: already loaded pokemons are not overwritten by stubs.
            pokemonDao.insertPokemons(stubs)

            Result.success(
                PokemonPage(
                    totalCount = body.count,
                    loadedCount = body.results.size,
                    nextUrl = body.next?.takeIf { it.isNotEmpty() }
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Returns the single in-flight fetch for [id], starting it if needed.
     *
     * @param background list prefetch: limited by the semaphore and subject to a retry cooldown after failure.
     * Foreground (details) requests skip both.
     */
    private fun ensureLoaded(id: PokemonId, background: Boolean): Deferred<Unit> {
        loadingPokemonsMap[id]?.let { return it }

        if (background) {
            val failedAt = failedAtMap[id]
            if (failedAt != null && System.currentTimeMillis() - failedAt < RETRY_COOLDOWN_MS) {
                return CompletedUnit
            }
        }

        val job = pokemonSaveScope.async(start = CoroutineStart.LAZY) {
            if (background) {
                savePokemonSemaphore.withPermit { getAndSavePokemon(id) }
            } else {
                getAndSavePokemon(id)
            }
        }

        val existing = loadingPokemonsMap.putIfAbsent(id, job)
        if (existing != null) {
            job.cancel()
            return existing
        }

        job.invokeOnCompletion { cause ->
            loadingPokemonsMap.remove(id, job)
            when {
                cause == null -> failedAtMap.remove(id)
                cause !is CancellationException -> failedAtMap[id] = System.currentTimeMillis()
            }
        }
        job.start()
        return job
    }

    private suspend fun getAndSavePokemon(pokemonId: PokemonId) {
        val response = api.getSinglePokemonRaw(pokemonId)

        if (!response.isSuccessful) {
            response.errorBody()?.close()
            error("Failed with ${response.code()} code")
        }

        val body = response.body() ?: error("Empty response")
        savePokemonToDb(body.string())
    }

    private suspend fun savePokemonToDb(jsonString: String) {
        val pokemonResponse = json.decodeFromString<PokemonResponse>(jsonString)

        pokemonDao.insertPokemon(
            PokemonEntity(
                id = pokemonResponse.id.id,
                name = pokemonResponse.name,
                type = pokemonResponse.types.firstOrNull()?.type?.name
                    ?: PokemonTypeEnum.UNKNOWN.asStringBackend(),
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

    private fun String.escapeLike(): String =
        replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
}

private val CompletedUnit: Deferred<Unit> = kotlinx.coroutines.CompletableDeferred(Unit)
