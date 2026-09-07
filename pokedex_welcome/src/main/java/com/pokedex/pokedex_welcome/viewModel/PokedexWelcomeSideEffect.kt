package com.pokedex.pokedex_welcome.viewModel

internal sealed class PokedexWelcomeSideEffect {
    data object MoveToPokemonList: PokedexWelcomeSideEffect()
}