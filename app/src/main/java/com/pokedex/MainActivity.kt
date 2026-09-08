package com.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.pokedex.compose_core.di.daggerViewModel
import com.pokedex.compose_core.provider.LocalComponentFactoryMapProvider
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.pokedex_welcome.route.PokedexWelcomeEntryProvider
import com.pokedex.pokedex_welcome.route.PokedexWelcomeScreenRoute
import com.pokedex.pokemon_list_feature.route.MainScreenNavProvider
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
                CompositionLocalProvider(
                    LocalComponentFactoryMapProvider provides (applicationContext as App).appComponent.factoriesProvider(),
                ) {
                    MainActivityScreen()
                }
            }
        }
    }
}

@Composable
private fun MainActivityScreen(
    viewModel: MainActivityViewModel = daggerViewModel()
) {
    val backStack = remember { mutableStateListOf<NavKey>(SplashScreenScreenRoute) }

    val entryProvider: ((NavKey) -> NavEntry<NavKey>) = entryProvider {
        MainScreenNavProvider()
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
    }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider
    )
}