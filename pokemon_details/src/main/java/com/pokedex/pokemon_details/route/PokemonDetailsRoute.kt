package com.pokedex.pokemon_details.route

import androidx.navigation3.runtime.NavKey
import com.pokedex.core.domain.model.PokemonId
import kotlinx.serialization.Serializable

@Serializable
data class PokemonDetailsRoute(
    val id: PokemonId,
    val ids: List<PokemonId>
): NavKey