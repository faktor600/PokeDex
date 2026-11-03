package com.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.CompositionLocalProvider
import com.pokedex.compose_core.transition.LocalSharedTransitionScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.pokedex.compose_core.di.daggerViewModel
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.pokedex_welcome.route.PokedexWelcomeEntryProvider
import com.pokedex.pokedex_welcome.route.PokedexWelcomeScreenRoute
import com.pokedex.pokemon_details.route.PokemonDetailsRoute
import com.pokedex.pokemon_details.route.PokemonDetailsScreenNavProvider
import com.pokedex.pokemon_list_feature.route.PokemonListScreenNavProvider
import com.pokedex.pokemon_list_feature.route.MainScreenRoute
import com.pokedex.splash_screen.route.SplashScreenEntryProvider
import com.pokedex.splash_screen.route.SplashScreenScreenRoute
import com.pokedex.viewmodel.MainActivityViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                MainActivityContainer()
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MainActivityContainer(
    viewModel: MainActivityViewModel = daggerViewModel()
) {
    val backStack = remember { mutableStateListOf<NavKey>(SplashScreenScreenRoute) }

    val entryProvider: ((NavKey) -> NavEntry<NavKey>) = entryProvider {
        PokemonListScreenNavProvider(
            toPokemonDetails = { id, ids, imageUrl ->
                backStack.add(PokemonDetailsRoute(id, ids, imageUrl))
            }
        )
        PokedexWelcomeEntryProvider(
            navigateToList = {
                backStack[backStack.lastIndex] = MainScreenRoute
            }
        )
        SplashScreenEntryProvider(
            navigateToWelcomeScreen = {
                backStack[backStack.lastIndex] = PokedexWelcomeScreenRoute
            },
            navigateToPokemonList = {
                backStack[backStack.lastIndex] = MainScreenRoute
            }
        )
        PokemonDetailsScreenNavProvider(
            onBackClick = {
                backStack.removeLastOrNull()
            }
        )
    }
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider
            )
        }
    }
}