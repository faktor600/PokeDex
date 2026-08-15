package com.pokedex.pokemon_list_feature.route

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.pokedex.pokemon_list_feature.screen.MainScreen

@Composable
fun EntryProviderScope<NavKey>.MainScreenNavProvider() {
    addEntryProvider(
        key = MainScreenRoute,
        content = {
            MainScreen()
        }
    )
}