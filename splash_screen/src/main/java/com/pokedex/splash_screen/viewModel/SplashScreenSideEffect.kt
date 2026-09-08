package com.pokedex.splash_screen.viewModel

internal sealed class SplashScreenSideEffect {
    data object MoveToPokemonList: SplashScreenSideEffect()

    data object MoveToWelcomeScreen: SplashScreenSideEffect()
}
