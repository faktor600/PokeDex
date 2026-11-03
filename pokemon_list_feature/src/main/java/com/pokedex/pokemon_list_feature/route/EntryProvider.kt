package com.pokedex.pokemon_list_feature.route

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pokedex.core.domain.model.PokemonId
import com.pokedex.pokemon_list_feature.screen.PokemonListScreen

@Composable
fun EntryProviderScope<NavKey>.PokemonListScreenNavProvider(
    toPokemonDetails: (
        id: PokemonId,
        ids: List<PokemonId>,
        imageUrl: String
    ) -> Unit
) {
    addEntryProvider(
        key = MainScreenRoute,
        content = {
            PokemonListScreen(
                toPokemonDetails = toPokemonDetails
            )
        }
    )
}
