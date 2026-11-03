package com.pokedex.api_pokemon.data

import com.pokedex.api_pokemon.dao.PokemonDao
import com.pokedex.api_pokemon.dao.entities.PokemonEntity
import com.pokedex.api_pokemon.dao.entities.PokemonListRow
import com.pokedex.api_pokemon.data.api.PokemonApi
import com.pokedex.api_pokemon.data.mapper.toDomain
import com.pokedex.api_pokemon.data.response.PokemonResponse
import com.pokedex.api_pokemon.data.response.PokemonSpeciesResponse
import com.pokedex.api_pokemon.domain.PokemonRepository
import com.pokedex.api_pokemon.domain.model.Pokemon
import com.pokedex.api_pokemon.domain.model.PokemonError
import com.pokedex.api_pokemon.domain.model.PokemonPage
import com.pokedex.api_pokemon.domain.model.PokemonSearchItem
import com.pokedex.api_pokemon.domain.model.PokemonSpecies
import com.pokedex.core.PokemonTypeEnum
import com.pokedex.core.asStringBackend
import com.pokedex.core.domain.model.PokemonId
import com.pokedex.core.getTypeEnum
import com.pokedex.core.network.AppJson
import com.pokedex.core.network.NetworkEvents
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.Response
import java.io.IOException
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

internal class PokemonRepositoryImpl @Inject constructor(
    private val api: PokemonApi,
    private val pokemonDao: PokemonDao,
    private val networkEvents: NetworkEvents
) : PokemonRepository {

    companion object {
        /** Fetches that are running right now. Entries are removed on completion (success or failure). */
        private val loadingPokemonsMap = ConcurrentHashMap<PokemonId, Deferred<Unit>>()

        /** Time of the last failed background fetch, used to avoid hammering the API. */
        private val failedAtMap = ConcurrentHashMap<PokemonId, Long>()
        private const val RETRY_COOLDOWN_MS = 30_000L

        /** Species never change during a session, so the details screen doesn't need to refetch them. */
        private val speciesCache = ConcurrentHashMap<String, PokemonSpecies>()

        /** Relative to the Retrofit baseUrl. */
        private const val FIRST_PAGE_PATH = "pokemon"

        /** Retries of a single request on I/O errors, HTTP 429 and 5xx. */
        private const val MAX_RETRIES = 3
        private const val BACKOFF_BASE_MS = 500L
        private const val MAX_RETRY_DELAY_MS = 10_000L

        /** Limits only background (list) fetches, so the details screen is never stuck behind them. */
        private val savePokemonSemaphore = Semaphore(8)

        // async (not launch): exceptions are stored in the Deferred instead of reaching the
        // uncaught exception handler (which would crash the app).
        private val pokemonSaveScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        private val CompletedUnit: Deferred<Unit> = CompletableDeferred(Unit)
    }

    override suspend fun getPokemonSpecies(url: String): Result<PokemonSpecies> = withContext(Dispatchers.IO) {
        try {
            speciesCache[url]?.let { return@withContext Result.success(it) }

            val body = executeWithRetry { api.getSpeciesByUrl(url) }.string()
            val species = AppJson.decodeFromString<PokemonSpeciesResponse>(body).toDomain()

            speciesCache[url] = species
            Result.success(species)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toPokemonError())
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
                AppJson.decodeFromString<PokemonResponse>(jsonString ?: throw PokemonError.NotFound()).toDomain()
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toPokemonError())
        }
    }

    override suspend fun searchForPokemonsByNameOrId(text: String): Flow<List<PokemonSearchItem>> {
        val query = text.trim()
        val source = if (query.isEmpty()) {
            pokemonDao.getAllRows()
        } else {
            pokemonDao.searchRows(
                // Only plain digits count as an id: "+5" or "1 " must not match pokemon #5 / #1.
                id = query.takeIf { str -> str.all { it in '0'..'9' } }?.toLongOrNull(),
                likePattern = query.escapeLike()
            )
        }

        // No downloads here: the UI asks for what is actually visible via prefetchPokemons().
        return source
            .map { rows -> rows.map { it.toSearchItem() } }
            // DB errors are propagated to the collector instead of silently completing the Flow.
            .flowOn(Dispatchers.IO)
    }

    override suspend fun prefetchPokemons(ids: List<PokemonId>) {
        if (ids.isEmpty()) return

        try {
            withContext(Dispatchers.IO) {
                pokemonDao.getNotLoadedIds(ids.map { it.id }).forEach { id ->
                    // Idempotent: already running / recently failed ids are skipped.
                    ensureLoaded(PokemonId(id), background = true)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Prefetch is best effort: the skeletons simply stay until the next attempt.
        }
    }

    override suspend fun loadPokemonPageAndSave(url: String?): Result<PokemonPage> = withContext(Dispatchers.IO) {
        try {
            val body = executeWithRetry { api.getPokemonPage(url ?: FIRST_PAGE_PATH) }

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
            Result.failure(e.toPokemonError())
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
            // A user-requested retry ("Try again" / network is back) drops failures that happened before it.
            if (failedAt != null &&
                failedAt >= networkEvents.lastRetryRequestedAt &&
                System.currentTimeMillis() - failedAt < RETRY_COOLDOWN_MS
            ) {
                return CompletedUnit
            }
        }

        val job = pokemonSaveScope.async(start = CoroutineStart.LAZY) {
            if (background) {
                savePokemonSemaphore.withPermit { getAndSavePokemon(id, background = true) }
            } else {
                getAndSavePokemon(id, background = false)
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

    private suspend fun getAndSavePokemon(pokemonId: PokemonId, background: Boolean) {
        val marker = if (background) "1" else null
        val jsonString = executeWithRetry { api.getSinglePokemonRaw(pokemonId, marker) }.string()
        savePokemonToDb(jsonString)
    }

    private suspend fun savePokemonToDb(jsonString: String) {
        val pokemonResponse = AppJson.decodeFromString<PokemonResponse>(jsonString)

        pokemonDao.insertPokemon(
            PokemonEntity(
                id = pokemonResponse.id.id,
                name = pokemonResponse.name,
                type = pokemonResponse.types.firstOrNull()?.type?.name
                    ?: PokemonTypeEnum.UNKNOWN.asStringBackend(),
                pokemonMainImageUrl = pokemonResponse.sprites.other?.officialArtwork?.frontDefault
                    ?: pokemonResponse.sprites.frontDefault
                    ?: "",
                json = jsonString
            )
        )
    }

    /**
     * Executes [call], retrying I/O errors, HTTP 429 and 5xx with exponential backoff
     * (honoring `Retry-After` when present). Other non-success codes fail immediately.
     *
     * @throws PokemonError.Http for a final non-success code, [PokemonError.Network] for a final I/O error,
     * [PokemonError.Parsing] for an empty body.
     */
    private suspend fun <T : Any> executeWithRetry(call: suspend () -> Response<T>): T {
        var attempt = 0
        while (true) {
            var retryDelayMs = 0L
            try {
                val response = call()
                if (response.isSuccessful) {
                    return response.body() ?: throw PokemonError.Parsing("Empty response body")
                }

                val code = response.code()
                val retryAfterMs = response.headers()["Retry-After"]?.toLongOrNull()?.times(1000)
                response.errorBody()?.close()

                val retryable = code == 429 || code in 500..599
                if (!retryable || attempt >= MAX_RETRIES) throw PokemonError.Http(code)

                retryDelayMs = retryAfterMs ?: backoffMs(attempt)
            } catch (e: IOException) {
                if (attempt >= MAX_RETRIES) throw PokemonError.Network(e)
                retryDelayMs = backoffMs(attempt)
            }

            delay(retryDelayMs.coerceAtMost(MAX_RETRY_DELAY_MS))
            attempt++
        }
    }

    private fun backoffMs(attempt: Int): Long = BACKOFF_BASE_MS shl attempt

    private fun Throwable.toPokemonError(): PokemonError = when (this) {
        is PokemonError -> this
        is IOException -> PokemonError.Network(this)
        is SerializationException -> PokemonError.Parsing("Failed to decode response", this)
        else -> PokemonError.Unknown(this)
    }

    private fun PokemonListRow.toSearchItem(): PokemonSearchItem {
        val pokemonId = PokemonId(id)
        return if (isLoaded) {
            PokemonSearchItem.LoadedPokemon(
                id = pokemonId,
                name = name,
                type = type.getTypeEnum(),
                pokemonMainImageUrl = imageUrl ?: ""
            )
        } else {
            PokemonSearchItem.NotLoaded(id = pokemonId, name = name)
        }
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
