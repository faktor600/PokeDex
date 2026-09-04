package com.pokedex.pokedex_welcome.viewModel

internal sealed class PokedexWelcomeScreenIntent {

    data object MoveToDownloadDatabaseStep: PokedexWelcomeScreenIntent()

    data object MoveToFinishDownloadingStep: PokedexWelcomeScreenIntent()
}