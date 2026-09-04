package com.pokedex.pokemon_details.route

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pokedex.pokemon_details.screen.PokemonDetailsScreen

@Composable
fun EntryProviderScope<NavKey>.PokemonDetailsScreenNavProvider() {
    addEntryProvider(
        key = PokemonDetailsRoute,
        content = {
            PokemonDetailsScreen()
        }
    )
}