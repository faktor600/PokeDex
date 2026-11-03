package com.pokedex.no_internet.route

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pokedex.no_internet.screen.NoInternetScreen

@Composable
fun EntryProviderScope<NavKey>.NoInternetEntryProvider(
    onRetryClick: () -> Unit,
) {
    addEntryProvider(
        key = NoInternetRoute,
        content = {
            NoInternetScreen(
                onRetryClick = onRetryClick
            )
        }
    )
}
