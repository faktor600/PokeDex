package com.pokedex.pokemon_details.route

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.pokedex.pokemon_details.screen.PokemonDetailsScreen

@Composable
fun EntryProviderScope<NavKey>.PokemonDetailsScreenNavProvider(
    onBackClick: () -> Unit
) {
    entry<PokemonDetailsRoute> {
        PokemonDetailsScreen(
            route = it,
            onBackClick = onBackClick
        )
    }
}