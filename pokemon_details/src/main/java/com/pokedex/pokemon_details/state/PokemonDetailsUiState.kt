package com.pokedex.pokemon_details.state

import androidx.compose.runtime.Immutable
import com.pokedex.compose_core.common.PokemonTypePresentationModel

@Immutable
internal data class PokemonDetailsUiState(
    val pokemonName: String = "",
    val pokemonNumber: String = "",
    val pokemonImageUrl: String = "",
    val pokemonTypeMain: PokemonTypePresentationModel = PokemonTypePresentationModel.Unknown,
    val pokemonTypes: Set<PokemonTypePresentationModel> = emptySet(),
    val weight: String = "",
    val height: String = "",
    val moves: List<String> = emptyList(),
    val description: String = ""
)