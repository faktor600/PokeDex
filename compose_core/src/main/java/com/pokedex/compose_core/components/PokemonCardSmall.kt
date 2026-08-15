package com.pokedex.compose_core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.pokedex.compose_core.R
import com.pokedex.compose_core.common.dropShadow2dp
import com.pokedex.compose_core.models.PokemonCardUiModel
import com.pokedex.compose_core.theme.AppTheme

@Composable
fun PokemonCardSmall(
    model: PokemonCardUiModel,
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
                    ),
                text = model.pokemonName,
                style = AppTheme.bodyTypography().body3,
                color = AppTheme.grayscaleColor().dark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Image(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            painter = rememberAsyncImagePainter(
                model = model.url,
                placeholder = painterResource(R.drawable.pokemon_placeholder),
                error = painterResource(R.drawable.pokemon_placeholder),
                fallback = painterResource(R.drawable.pokemon_placeholder)
            ),
            contentDescription = null
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
@Preview(
    widthDp = 200,
    showBackground = true
)
private fun PokemonCardSmallPreview() {
    AppTheme {
        PokemonCardSmall(
            model = PokemonCardUiModel(
                pokemonNumber = "#999",
                pokemonName = "Pokémon Name",
                url = "",
                pokemonId = 1
            ),
            onCardClick = {}
        )
    }
}