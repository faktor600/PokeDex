package com.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.pokedex.compose_core.provider.LocalComponentFactoryMapProvider
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.pokemon_list_feature.route.MainScreenNavProvider
import com.pokedex.pokemon_list_feature.route.MainScreenRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val backStack = remember { mutableStateListOf<NavKey>(MainScreenRoute) }

            val entryProvider: ((NavKey) -> NavEntry<NavKey>) = entryProvider {
                MainScreenNavProvider()
            }

            AppTheme {
                CompositionLocalProvider(
                    LocalComponentFactoryMapProvider provides (application as App).appComponent.factoriesProvider(),
                ) {
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider
                    )
                }
            }
        }
    }
}