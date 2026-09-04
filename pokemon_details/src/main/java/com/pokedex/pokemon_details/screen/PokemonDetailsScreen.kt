package com.pokedex.pokemon_details.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.pokedex.compose_core.R as ComposeCoreR
import com.pokedex.compose_core.common.PokemonTypePresentationModel
import com.pokedex.compose_core.components.PokemonTypeChip
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.pokemon_details.R
import com.pokedex.pokemon_details.state.PokemonDetailsUiState

@Composable
internal fun PokemonDetailsScreen() {

    PokemonDetailsScreenContent(
        uiState = PokemonDetailsUiState(),
        onBackClick = {}
    )
}

@Composable
private fun PokemonDetailsScreenContent(
    uiState: PokemonDetailsUiState,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                pokemonName = uiState.pokemonName,
                pokemonNumber = uiState.pokemonNumber,
                onBackClick = onBackClick
            )
        },
        containerColor = if (uiState.pokemonTypeMain == PokemonTypePresentationModel.Unknown) {
            AppTheme.grayscaleColor().whireframe
        } else {
            uiState.pokemonTypeMain.color
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(
                                top = 4.dp,
                                end = 4.dp,
                                bottom = 8.dp
                            ).aspectRatio(
                                ratio = 1f,
                                matchHeightConstraintsFirst = true
                            )
                            .align(Alignment.TopEnd),
                        painter = painterResource(ComposeCoreR.drawable.ic_pokeball),
                        contentDescription = null,
                        tint = AppTheme.grayscaleColor().white.copy(
                            alpha = 0.1f
                        )
                    )
                }

                Content(
                    modifier = Modifier
                        .weight(2.85f)
                        .fillMaxSize()
                        .padding(
                            horizontal = 4.dp,
                        ).padding(
                            bottom = 4.dp + contentPadding.calculateBottomPadding(),
                        ).background(
                            color = AppTheme.grayscaleColor().white,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    uiState = uiState
                )
            }

            ImageWithScroll(
                modifier = Modifier.padding(
                    top = contentPadding.calculateTopPadding()
                ),
                imageUrl = uiState.pokemonImageUrl
            )
        }
    }
}

@Composable
private fun ImageWithScroll(
    modifier: Modifier = Modifier,
    imageUrl: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(
            modifier = Modifier.width(24.dp)
        )

        Icon(
            modifier = Modifier.size(32.dp),
            painter = painterResource(ComposeCoreR.drawable.ic_chevron_left),
            contentDescription = null,
            tint = AppTheme.grayscaleColor().white,
        )

        Spacer(
            modifier = Modifier.width(32.dp)
        )

        AsyncImage(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            model = imageUrl,
            placeholder = painterResource(ComposeCoreR.drawable.pokemon_placeholder),
            error = painterResource(ComposeCoreR.drawable.pokemon_placeholder),
            fallback = painterResource(ComposeCoreR.drawable.pokemon_placeholder),
            contentDescription = null
        )

        Spacer(
            modifier = Modifier.width(32.dp)
        )

        Icon(
            modifier = Modifier.size(32.dp),
            painter = painterResource(ComposeCoreR.drawable.ic_chevron_right),
            contentDescription = null,
            tint = AppTheme.grayscaleColor().white,
        )

        Spacer(
            modifier = Modifier.width(24.dp)
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    uiState: PokemonDetailsUiState
) {
    Box(
        modifier = modifier
            .background(
                color = AppTheme.grayscaleColor().white, shape = RoundedCornerShape(8.dp)
            )
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(
                modifier = Modifier.height(86.dp)
            )

            TypeRow(
                pokemonTypes = uiState.pokemonTypes
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.details_about_title),
                style = AppTheme.headerTypography().subtitle1,
                color = uiState.pokemonTypeMain.color
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            AboutRow(
                uiState = uiState
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp
                    ),
                text = uiState.description,
                style = AppTheme.bodyTypography().body3,
                color = AppTheme.grayscaleColor().dark
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.base_stats_title),
                style = AppTheme.headerTypography().subtitle1,
                color = uiState.pokemonTypeMain.color
            )
        }
    }
}

@Composable
private fun ColumnScope.TypeRow(pokemonTypes: Set<PokemonTypePresentationModel>) {
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        pokemonTypes.onEach { type ->
            PokemonTypeChip(
                pokemonType = type
            )
        }
    }
}

@Composable
private fun AboutRow(
    uiState: PokemonDetailsUiState
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Spacer(
            modifier = Modifier.width(20.dp)
        )

        WeightItem(
            weight = uiState.weight
        )

        VerticalDivider(
            color = AppTheme.grayscaleColor().light
        )

        HeightItem(
            height = uiState.height
        )

        VerticalDivider(
            color = AppTheme.grayscaleColor().light
        )

        MovesItem(
            moves = uiState.moves
        )

        Spacer(
            modifier = Modifier.width(20.dp)
        )
    }
}

@Composable
private fun RowScope.WeightItem(
    weight: String,
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        Spacer(
            Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(ComposeCoreR.drawable.ic_weight),
                contentDescription = null,
                tint = AppTheme.grayscaleColor().dark,
            )

            Spacer(
                Modifier.width(8.dp)
            )

            Text(
                text = weight,
                color = AppTheme.grayscaleColor().dark,
                style = AppTheme.bodyTypography().body3
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.weight_details),
            color = AppTheme.grayscaleColor().medium,
            style = AppTheme.bodyTypography().caption,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.HeightItem(
    height: String,
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        Spacer(
            Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .rotate(90f),
                painter = painterResource(ComposeCoreR.drawable.ic_straighten),
                contentDescription = null,
                tint = AppTheme.grayscaleColor().dark,
            )

            Spacer(
                Modifier.width(8.dp)
            )

            Text(
                text = height,
                color = AppTheme.grayscaleColor().dark,
                style = AppTheme.bodyTypography().body3
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.height_details),
            color = AppTheme.grayscaleColor().medium,
            style = AppTheme.bodyTypography().caption,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.MovesItem(
    moves: List<String>,
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {

        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            moves.onEach {
                Text(
                    text = it,
                    style = AppTheme.bodyTypography().body3,
                    color = AppTheme.grayscaleColor().dark
                )
            }
        }

        Spacer(
            Modifier.weight(1f)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.moves_details),
            color = AppTheme.grayscaleColor().medium,
            style = AppTheme.bodyTypography().caption,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TopBar(
    pokemonName: String,
    pokemonNumber: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(
            top = 24.dp,
            bottom = 24.dp
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier.width(24.dp)
        )

        Icon(
            modifier = Modifier
                .size(32.dp)
                .clickable(
                    onClick = onBackClick
                ),
            painter = painterResource(ComposeCoreR.drawable.ic_arrow_back),
            contentDescription = null,
            tint = AppTheme.grayscaleColor().white
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            modifier = Modifier.weight(1f),
            text = pokemonName,
            color = AppTheme.grayscaleColor().white,
            style = AppTheme.headerTypography().headline
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = pokemonNumber,
            color = AppTheme.grayscaleColor().white,
            style = AppTheme.headerTypography().subtitle2
        )

        Spacer(
            modifier = Modifier.width(24.dp)
        )
    }
}

@Composable
@Preview
private fun PokemonDetailsScreenContentPreview() {
    AppTheme {
        PokemonDetailsScreenContent(
            uiState = PokemonDetailsUiState(
                pokemonName = "Pokémon Name",
                pokemonNumber = "#999",
                pokemonImageUrl = "",
                weight = "6,9 kg",
                height = "0,7 m",
                moves = listOf(
                    "Chlorophyll", "Overgrow"
                ),
                description = "There is a plant seed on its back right from the day this Pokémon is born. The seed slowly grows larger.",
                pokemonTypeMain = PokemonTypePresentationModel.Bug,
                pokemonTypes = setOf(PokemonTypePresentationModel.Grass, PokemonTypePresentationModel.Poison)
            ),
            onBackClick = {}
        )
    }
}