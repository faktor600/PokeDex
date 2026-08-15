package com.pokedex.pokemon_list_feature.viewModel

sealed class MainScreenIntent {

    data object FiltersClick: MainScreenIntent()

    data class TextChange(
        val newText: String
    ): MainScreenIntent()

    data object ClearText: MainScreenIntent()
}