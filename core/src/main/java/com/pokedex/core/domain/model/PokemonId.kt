package com.pokedex.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PokemonId(val id: Long)