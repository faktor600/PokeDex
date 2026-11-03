package com.pokedex.api_pokemon.domain.model

/** Typed failure reasons returned inside `Result.failure` by [com.pokedex.api_pokemon.domain.PokemonRepository]. */
sealed class PokemonError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    /** No connection, timeout or any other I/O problem. */
    class Network(cause: Throwable) : PokemonError("Network error: ${cause.message}", cause)

    /** The server answered with a non-success code. [isRateLimited] is true for HTTP 429. */
    class Http(val code: Int) : PokemonError("Failed with $code code") {
        val isRateLimited: Boolean get() = code == 429
    }

    /** The response body was missing or couldn't be decoded. */
    class Parsing(message: String, cause: Throwable? = null) : PokemonError(message, cause)

    /** The pokemon doesn't exist locally or remotely. */
    class NotFound(message: String = "No Pokemon Found") : PokemonError(message)

    class Unknown(cause: Throwable) : PokemonError("Unexpected error: ${cause.message}", cause)
}
