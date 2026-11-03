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
import androidx.compose.runtime.LaunchedEffect
import com.pokedex.no_internet.route.NoInternetEntryProvider
import com.pokedex.no_internet.route.NoInternetRoute
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

/** Removes the "no internet" screen only if it is on top, so a second pop can't remove another screen. */
private fun popNoInternetIfOnTop(backStack: MutableList<NavKey>) {
    if (backStack.lastOrNull() == NoInternetRoute) {
        backStack.removeLastOrNull()
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MainActivityContainer(
    viewModel: MainActivityViewModel = daggerViewModel()
) {
    val backStack = remember { mutableStateListOf<NavKey>(SplashScreenScreenRoute) }

    // Many parallel requests can fail at once, so the screen is pushed only if it isn't already on top.
    LaunchedEffect(viewModel) {
        viewModel.connectionLost.collect {
            if (backStack.lastOrNull() != NoInternetRoute) {
                backStack.add(NoInternetRoute)
            }
        }
    }

    // The network is back: leave the "no internet" screen (the retry signal itself comes from the ViewModel).
    LaunchedEffect(viewModel) {
        viewModel.isOnline.collect { isOnline ->
            if (isOnline == true) popNoInternetIfOnTop(backStack)
        }
    }

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
        NoInternetEntryProvider(
            onRetryClick = {
                popNoInternetIfOnTop(backStack)
                viewModel.onRetryRequested()
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