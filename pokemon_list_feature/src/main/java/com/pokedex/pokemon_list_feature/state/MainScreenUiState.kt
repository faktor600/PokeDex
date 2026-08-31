package com.pokedex.pokemon_list_feature.state

import androidx.compose.runtime.Immutable
import com.pokedex.compose_core.models.PokemonCardUiModel

@Immutable
data class MainScreenUiState(
    val isLoading: Boolean = true,
    val isFiltersClicked: Boolean = false,
    val searchText: String = "",
    val nextPageUrl: String? = "",
    val items: List<PokemonCardUiModel> = emptyList()
)