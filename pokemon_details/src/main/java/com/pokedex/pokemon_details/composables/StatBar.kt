package com.pokedex.pokemon_details.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokedex.compose_core.common.PokemonTypePresentationModel
import com.pokedex.compose_core.theme.AppTheme

private const val PROGRESS_FINAL = 999

@Composable
internal fun StatBar(
    pokemonTypePresentationModel: PokemonTypePresentationModel,
    statValue: Int,
    modifier: Modifier = Modifier
) {
    val colorBackground = pokemonTypePresentationModel.color.copy(
        alpha = 0.2f
    )
    val colorTrack = pokemonTypePresentationModel.color

    val density = LocalDensity.current

    val cornerRadius = with(density) {
        CornerRadius(4.dp.toPx(), 4.dp.toPx())
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(statValue) {
        progress.snapTo(0f)

        progress.animateTo(
            targetValue = statValue / PROGRESS_FINAL.toFloat(),
            animationSpec = tween(durationMillis = 500)
        )
    }

    Canvas(
        modifier = modifier
            .height(4.dp)
    ) {
        val path = Path().apply {
            addRoundRect(
                roundRect = RoundRect(
                    rect = Rect(
                        offset = Offset.Zero,
                        size = Size(
                            width = size.width * progress.value,
                            height = size.height
                        )
                    ),
                    topLeft = cornerRadius,
                    bottomLeft = cornerRadius,
                    topRight = if (progress.value == 1f) {
                        cornerRadius
                    } else {
                        CornerRadius.Zero
                    },
                    bottomRight = if (progress.value == 1f) {
                        cornerRadius
                    } else {
                        CornerRadius.Zero
                    },
                )
            )
        }

        drawRoundRect(
            color = colorBackground,
            cornerRadius = cornerRadius
        )

        drawPath(
            path = path,
            color = colorTrack
        )

    }
}

@Composable
@Preview(showBackground = true)
private fun StatBarPreview() {
    AppTheme {
        StatBar(
            pokemonTypePresentationModel = PokemonTypePresentationModel.Grass,
            statValue = 700
        )
    }
}