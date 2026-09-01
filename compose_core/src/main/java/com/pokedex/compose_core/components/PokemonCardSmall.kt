package com.pokedex.compose_core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.pokedex.compose_core.R
import com.pokedex.compose_core.common.dropShadow2dp
import com.pokedex.compose_core.models.PokemonCardUiModel
import com.pokedex.compose_core.theme.AppTheme

@Composable
fun PokemonCardSmall(
    model: PokemonCardUiModel.Pokemon,
    onCardClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .clickable(
                onClick = onCardClick
            )
            .dropShadow2dp(
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = AppTheme.grayscaleColor().white,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .fillMaxWidth()
                .background(
                    color = AppTheme.grayscaleColor().background,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        horizontal = 8.dp
                    )
                    .padding(
                        bottom = 4.dp,
                        top = 24.dp
                    )
                    .zIndex(30f)
                    .fillMaxWidth(),
                text = model.pokemonName,
                style = AppTheme.bodyTypography().body3,
                color = AppTheme.grayscaleColor().dark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        AsyncImage(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center)
                .aspectRatio(1f),
            model = model.url,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            placeholder = painterResource(R.drawable.pokemon_placeholder),
            error = painterResource(R.drawable.pokemon_placeholder),
            fallback = painterResource(R.drawable.pokemon_placeholder)
        )

        Text(
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(
                    top = 4.dp,
                    end = 8.dp
                ),
            text = model.pokemonNumber,
            style = AppTheme.bodyTypography().caption,
            color = AppTheme.grayscaleColor().medium
        )
    }
}

@Composable
fun PokemonCardSmallSkeleton(
    pokemonSkeleton: PokemonCardUiModel.PokemonSkeleton?
) {
    Box(
        modifier = Modifier
            .dropShadow2dp(
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = AppTheme.grayscaleColor().white,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .fillMaxWidth()
                .background(
                    color = AppTheme.grayscaleColor().background,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        horizontal = 8.dp
                    )
                    .padding(
                        bottom = 4.dp,
                        top = 24.dp
                    )
                    .fillMaxWidth(),
                text = pokemonSkeleton?.pokemonName ?: "",
                style = AppTheme.bodyTypography().body3,
                color = AppTheme.grayscaleColor().dark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        Image(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center)
                .aspectRatio(1f),
            painter = painterResource(R.drawable.pokemon_placeholder),
            contentScale = ContentScale.Fit,
            contentDescription = null
        )
    }
}

@Composable
@Preview(
    widthDp = 200,
)
private fun PokemonCardSmallSkeletonPreview() {
    AppTheme {
        PokemonCardSmallSkeleton(
            pokemonSkeleton = PokemonCardUiModel.PokemonSkeleton(
                pokemonName = "Pokémon Name",
                urlId = "das"
            )
        )
    }
}

@Composable
@Preview(
    widthDp = 200,
    showBackground = true
)
private fun PokemonCardSmallPreview() {
    AppTheme {
        PokemonCardSmall(
            model = PokemonCardUiModel.Pokemon(
                pokemonNumber = "#999",
                pokemonName = "Pokémon Name",
                url = "",
                urlId = "das"
            ),
            onCardClick = {}
        )
    }
}