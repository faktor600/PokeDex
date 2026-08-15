package com.pokedex.pokemon_list_feature.viewModel

sealed class MainScreenSideEffect {
    data object ShowFilters: MainScreenSideEffect()
}