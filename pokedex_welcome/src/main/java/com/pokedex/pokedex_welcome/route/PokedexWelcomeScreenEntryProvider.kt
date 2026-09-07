package com.pokedex.pokedex_welcome.route

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pokedex.pokedex_welcome.screen.PokedexWelcomeScreen

@Composable
fun EntryProviderScope<NavKey>.PokedexWelcomeEntryProvider(
    navigateToList: () -> Unit,
) {
    addEntryProvider(
        key = PokedexWelcomeScreenRoute,
        content = {
            PokedexWelcomeScreen(
                navigateToList = navigateToList
            )
        }
    )
}