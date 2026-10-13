package com.pokedex.pokemon_list_feature.state

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import com.pokedex.compose_core.models.PokemonCardUiModel
import kotlinx.coroutines.flow.MutableStateFlow

@Immutable
sealed class PokemonListUiState {

    data object Loading : PokemonListUiState()

    data object Empty: PokemonListUiState()

    data class ListLoaded(
        val items: List<PokemonCardUiModel> = emptyList()
    ): PokemonListUiState()

    fun getPokemonsItems() = (this as? ListLoaded)?.items ?: emptyList()
}