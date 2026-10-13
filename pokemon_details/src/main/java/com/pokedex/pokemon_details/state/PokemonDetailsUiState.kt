package com.pokedex.pokemon_details.state

import androidx.compose.runtime.Immutable
import com.pokedex.compose_core.common.PokemonTypePresentationModel
import com.pokedex.core.domain.model.PokemonId

@Immutable
internal data class PokemonDetailsUiState(
    val currentPokemonById: PokemonId = PokemonId(0),
    val pokemonMap: Map<PokemonId, PokemonDetails> = mapOf(),
    val lastId: PokemonId = PokemonId(Long.MAX_VALUE),
    val firstId: PokemonId = PokemonId(0),
) {
    val isPrevVisible: Boolean = currentPokemonById != firstId
    val isNextVisible: Boolean = currentPokemonById != lastId
}

@Immutable
internal sealed class PokemonDetails {

    data class Loaded(
        val pokemonName: String = "",
        val pokemonNumber: String = "",
        val pokemonImageUrl: String = "",
        val pokemonTypeMain: PokemonTypePresentationModel = PokemonTypePresentationModel.Unknown,
        val pokemonTypes: Set<PokemonTypePresentationModel> = emptySet(),
        val weight: String = "",
        val height: String = "",
        val moves: List<String> = emptyList(),
        val description: String = "",
        val statHp: Int = 0,
        val statAtk: Int = 0,
        val statDef: Int = 0,
        val statSAtk: Int = 0,
        val statSDef: Int = 0,
        val statSpd: Int = 0
    ): PokemonDetails()

    data object Loading: PokemonDetails()
}