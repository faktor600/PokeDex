package com.pokedex.splash_screen.viewModel

internal sealed class SplashScreenScreenIntent {
    data object MoveToNextScreen: SplashScreenScreenIntent()
}
