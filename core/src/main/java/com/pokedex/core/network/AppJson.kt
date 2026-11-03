package com.pokedex.core.network

import kotlinx.serialization.json.Json

/** The single [Json] configuration shared by Retrofit and by code that decodes raw JSON itself. */
val AppJson: Json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}
