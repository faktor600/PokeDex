package com.pokedex.compose_core.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.pokedex.compose_core.common.ScaleIndication

val LocalAppColorScheme = compositionLocalOf { colorScheme() }
val LocalAppTypography = compositionLocalOf { appTypography() }

// Тема обертка для приложения
@Composable
fun AppTheme(
    colorScheme: AppColorScheme = AppColorScheme(),
    appTypography: AppTypography = AppTypography(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        values = arrayOf(
            LocalAppColorScheme provides colorScheme,
            LocalAppTypography provides appTypography,
            LocalIndication provides ScaleIndication
        ),
        content = content
    )
}

// Основная тема
object AppTheme {
    @get:Composable
    val colorScheme: AppColorScheme
        @ReadOnlyComposable get() = LocalAppColorScheme.current

    @get:Composable
    val appTypography: AppTypography
        @ReadOnlyComposable get() = LocalAppTypography.current

    // Colors
    @Composable
    @ReadOnlyComposable
    fun identityColor() = colorScheme.identity

    @Composable
    @ReadOnlyComposable
    fun grayscaleColor() = colorScheme.grayscale

    @Composable
    @ReadOnlyComposable
    fun pokemonTypeColor() = colorScheme.pokemonTypeColor

    // Typography
    @Composable
    @ReadOnlyComposable
    fun headerTypography() = appTypography.header

    @Composable
    @ReadOnlyComposable
    fun bodyTypography() = appTypography.body

    @Composable
    @ReadOnlyComposable
    fun buttonTypography() = appTypography.button
}