package com.pokedex.compose_core.transition

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.pokedex.core.domain.model.PokemonId

/** Scope общего перехода, который создаётся вокруг NavDisplay в :app. */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

private data class PokemonImageKey(val id: Long)

/**
 * Помечает картинку покемона как общий элемент между списком и экраном деталей.
 * Без [LocalSharedTransitionScope] (превью, тесты) ничего не делает.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.pokemonSharedImage(id: PokemonId): Modifier {
    val sharedScope = LocalSharedTransitionScope.current ?: return this
    val animatedScope = LocalNavAnimatedContentScope.current
    return with(sharedScope) {
        // ScaleToBounds масштабирует уже отрисованное содержимое, а не перемеряет его каждый кадр:
        // иначе AsyncImage на каждом кадре получает новый размер и Coil перезапускает загрузку
        this@pokemonSharedImage.sharedBounds(
            sharedContentState = rememberSharedContentState(PokemonImageKey(id.id)),
            animatedVisibilityScope = animatedScope,
            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center
            ),
        )
    }
}
