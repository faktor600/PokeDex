package com.pokedex.pokemon_list_feature.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.pokedex.core.domain.model.PokemonId
import com.pokedex.compose_core.components.FiltersButton
import com.pokedex.compose_core.components.PokemonCardSmall
import com.pokedex.compose_core.components.PokemonCardSmallSkeleton
import com.pokedex.compose_core.components.SearchField
import com.pokedex.compose_core.di.daggerViewModel
import com.pokedex.compose_core.models.PokemonCardUiModel
import com.pokedex.compose_core.R as ComposeCoreR
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.pokemon_list_feature.R
import com.pokedex.pokemon_list_feature.state.PokemonListUiState
import com.pokedex.pokemon_list_feature.viewModel.MainScreenIntent
import com.pokedex.pokemon_list_feature.viewModel.MainScreenSideEffect
import com.pokedex.pokemon_list_feature.viewModel.PokemonListViewModel

private const val PREFETCH_AHEAD = 9

@Composable
fun PokemonListScreen(
    toPokemonDetails: (
        id: PokemonId,
        ids: List<PokemonId>,
        imageUrl: String
    ) -> Unit,
    viewModel: PokemonListViewModel = daggerViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when(sideEffect) {
                MainScreenSideEffect.ShowFilters -> Unit
            }
        }
    }

    PokemonListScreenContent(
        uiState = uiState,
        searchText = searchText,
        onFiltersButtonClick = {
            viewModel.handleIntent(MainScreenIntent.FiltersClick)
        },
        onTextChange = {
            viewModel.handleIntent(MainScreenIntent.TextChange(it))
        },
        onClearText = {
            viewModel.handleIntent(MainScreenIntent.ClearText)
        },
        onPokemonsNeeded = viewModel::onPokemonsNeeded,
        onCardClick = { cardUiModel ->
            toPokemonDetails.invoke(
                cardUiModel.id,
                uiState.getPokemonsItems().map { pokemon -> pokemon.id },
                (cardUiModel as? PokemonCardUiModel.Pokemon)?.url.orEmpty()
            )
        }
    )
}

@Composable
private fun PokemonListScreenContent(
    uiState: PokemonListUiState,
    searchText: String,
    onFiltersButtonClick: () -> Unit,
    onTextChange: (newText: String) -> Unit,
    onClearText: () -> Unit,
    onCardClick: (item: PokemonCardUiModel) -> Unit,
    onPokemonsNeeded: (ids: List<PokemonId>) -> Unit = {},
) {
    val lazyGridState = rememberSaveable(
        searchText,
        saver = LazyGridState.Saver
    ) {
        LazyGridState(0, 0)
    }

    Scaffold(
        containerColor = AppTheme.identityColor().primary,
        topBar = {
            TopBar(
                searchText = searchText,
                onFiltersButtonClick = onFiltersButtonClick,
                onTextChange = onTextChange,
                onClearText = onClearText
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 4.dp,
                )
                .padding(
                    bottom = 4.dp + paddingValues.calculateBottomPadding(),
                    top = paddingValues.calculateTopPadding()
                )
                .background(
                    color = AppTheme.grayscaleColor().white,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(
                    horizontal = 12.dp
                )
        ) {
            when (uiState) {
                PokemonListUiState.Empty -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .weight(0.15f)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                                text = stringResource(R.string.no_pokemon_title),
                                style = AppTheme.headerTypography().headline,
                                color = AppTheme.grayscaleColor().dark
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = stringResource(R.string.no_pokemon_description),
                                style = AppTheme.bodyTypography().body1,
                                color = AppTheme.grayscaleColor().medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                is PokemonListUiState.ListLoaded -> {
                    val currentOnPokemonsNeeded by rememberUpdatedState(onPokemonsNeeded)
                    LaunchedEffect(lazyGridState, uiState.items) {
                        snapshotFlow {
                            val visible = lazyGridState.layoutInfo.visibleItemsInfo
                            (visible.firstOrNull()?.index ?: 0) to (visible.lastOrNull()?.index ?: -1)
                        }.collect { (first, last) ->
                            // Visible cards plus a screenful ahead, so scrolling rarely shows skeletons.
                            val from = first.coerceAtLeast(0)
                            val to = (last + PREFETCH_AHEAD).coerceAtMost(uiState.items.lastIndex)
                            if (from <= to) {
                                currentOnPokemonsNeeded(
                                    uiState.items.subList(from, to + 1)
                                        .filterIsInstance<PokemonCardUiModel.PokemonSkeleton>()
                                        .map { it.id }
                                )
                            }
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        state = lazyGridState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(
                            vertical = 24.dp
                        )
                    ) {
                        items(
                            count = uiState.getPokemonsItems().size,
                            key = {
                                uiState.getPokemonsItems()[it].id.id
                            },
                        ) {
                            when (val item = uiState.getPokemonsItems()[it]) {
                                is PokemonCardUiModel.PokemonSkeleton -> PokemonCardSmallSkeleton(
                                    pokemonSkeleton = item
                                )

                                is PokemonCardUiModel.Pokemon -> PokemonCardSmall(
                                    model = item,
                                    onCardClick = {
                                        onCardClick.invoke(item)
                                    }
                                )
                            }
                        }
                    }
                }

                PokemonListUiState.Loading -> {
                    val composition = rememberLottieComposition(
                        spec = LottieCompositionSpec.RawRes(
                            ComposeCoreR.raw.pokeball_loading
                        )
                    )
                    val progress by animateLottieCompositionAsState(
                        composition = composition.value,
                        iterations = LottieConstants.IterateForever
                    )

                    LottieAnimation(
                        modifier = Modifier
                            .aspectRatio(3f)
                            .align(Alignment.Center),
                        composition = composition.value,
                        progress = { progress }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    searchText: String,
    onFiltersButtonClick: () -> Unit,
    onTextChange: (newText: String) -> Unit,
    onClearText: () -> Unit,
) {
    val isButtonClicked = rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
            )
            .padding(
                top = 16.dp + WindowInsets.safeContent.asPaddingValues().calculateTopPadding(),
                bottom = 24.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(ComposeCoreR.drawable.ic_pokeball),
                contentDescription = null,
                tint = AppTheme.grayscaleColor().white
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Text(
                text = stringResource(R.string.main_screen_top_bar),
                style = AppTheme.headerTypography().headline,
                color = AppTheme.grayscaleColor().white
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchField(
                modifier = Modifier.weight(1f),
                text = searchText,
                onTextChanged = onTextChange,
                onClearText = onClearText
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            FiltersButton(
                clicked = isButtonClicked.value,
                onClick = {
                    isButtonClicked.value = true
                    onFiltersButtonClick.invoke()
                }
            )
        }
    }
}

@Composable
@Preview
private fun PokemonListScreenContentEmptyPreview() {
    AppTheme {
        PokemonListScreenContent(
            uiState = PokemonListUiState.Empty,
            searchText = "",
            onFiltersButtonClick = {},
            onTextChange = {},
            onClearText = {},
            onCardClick = {}
        )
    }
}

@Composable
@Preview
private fun PokemonListScreenContentLoadingPreview() {
    AppTheme {
        PokemonListScreenContent(
            uiState = PokemonListUiState.Loading,
            searchText = "",
            onFiltersButtonClick = {},
            onTextChange = {},
            onClearText = {},
            onCardClick = {}
        )
    }
}

@Composable
@Preview
private fun PokemonListScreenContentPreview() {
    AppTheme {
        PokemonListScreenContent(
            uiState = PokemonListUiState.ListLoaded(
                items = listOf(
                    PokemonCardUiModel.Pokemon(
                        id = PokemonId(1),
                        pokemonNumber = "1",
                        pokemonName = "name",
                        url = ""
                    ),
                    PokemonCardUiModel.Pokemon(
                        id = PokemonId(12),
                        pokemonNumber = "1",
                        pokemonName = "name",
                        url = ""
                    ),
                    PokemonCardUiModel.Pokemon(
                        id = PokemonId(13),
                        pokemonNumber = "1",
                        pokemonName = "name",
                        url = ""
                    )
                )
            ),
            onFiltersButtonClick = {},
            onTextChange = {},
            onClearText = {},
            onCardClick = {},
            searchText = ""
        )
    }
}