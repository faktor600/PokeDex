package com.pokedex.splash_screen.route

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pokedex.splash_screen.screen.SplashScreen

@Composable
fun EntryProviderScope<NavKey>.SplashScreenEntryProvider(
    navigateToPokemonList: () -> Unit,
    navigateToWelcomeScreen: () -> Unit
) {
    addEntryProvider(
        key = SplashScreenScreenRoute,
        content = {
            SplashScreen(
                navigateToPokemonList = navigateToPokemonList,
                navigateToWelcomeScreen = navigateToWelcomeScreen
            )
        }
    )
}
