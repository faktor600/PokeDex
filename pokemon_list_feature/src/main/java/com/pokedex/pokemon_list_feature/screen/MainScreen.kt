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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokedex.compose_core.components.FiltersButton
import com.pokedex.compose_core.components.PokemonCardSmall
import com.pokedex.compose_core.components.SearchField
import com.pokedex.compose_core.di.daggerViewModel
import com.pokedex.compose_core.models.PokemonCardUiModel
import com.pokedex.compose_core.R as ComposeCoreR
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.pokemon_list_feature.R
import com.pokedex.pokemon_list_feature.di.DaggerMainScreenComponent
import com.pokedex.pokemon_list_feature.state.MainScreenUiState
import com.pokedex.pokemon_list_feature.viewModel.MainScreenIntent
import com.pokedex.pokemon_list_feature.viewModel.MainScreenSideEffect
import com.pokedex.pokemon_list_feature.viewModel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = daggerViewModel<MainViewModel> {
        DaggerMainScreenComponent.create().viewModel()
    }
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when(sideEffect) {
                MainScreenSideEffect.ShowFilters -> Unit
            }
        }
    }

    MainScreenContent(
        uiState = uiState,
        onFiltersButtonClick = {
            viewModel.handleIntent(MainScreenIntent.FiltersClick)
        },
        onTextChange = {
            viewModel.handleIntent(MainScreenIntent.TextChange(it))
        },
        onClearText = {
            viewModel.handleIntent(MainScreenIntent.ClearText)
        },
        onCardClick = {}
    )
}

@Composable
private fun MainScreenContent(
    uiState: MainScreenUiState,
    onFiltersButtonClick: () -> Unit,
    onTextChange: (newText: String) -> Unit,
    onClearText: () -> Unit,
    onCardClick: (item: PokemonCardUiModel) -> Unit
) {
    Scaffold(
        containerColor = AppTheme.identityColor().primary,
        topBar = {
            TopBar(
                searchText = uiState.searchText,
                filtersButtonClicked = uiState.isFiltersClicked,
                onFiltersButtonClick = onFiltersButtonClick,
                onTextChange = onTextChange,
                onClearText = onClearText
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
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
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 24.dp
                )
            ) {
                items(
                    count = uiState.items.size,
                    key = {
                        uiState.items[it].pokemonId
                    }
                ) {
                    val item = uiState.items[it]
                    PokemonCardSmall(
                        model = item,
                        onCardClick = {
                            onCardClick.invoke(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    searchText: String,
    filtersButtonClicked: Boolean,
    onFiltersButtonClick: () -> Unit,
    onTextChange: (newText: String) -> Unit,
    onClearText: () -> Unit
) {
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
                modifier = Modifier.Companion.weight(1f),
                text = searchText,
                onTextChanged = onTextChange,
                onClearText = onClearText
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            FiltersButton(
                clicked = filtersButtonClicked,
                onClick = onFiltersButtonClick
            )
        }
    }
}

@Composable
@Preview
private fun MainScreenContentPreview() {
    AppTheme {
        MainScreenContent(
            uiState = MainScreenUiState(
                items = listOf(
                    PokemonCardUiModel(
                        pokemonId = 1,
                        pokemonNumber = "1",
                        pokemonName = "name",
                        url = ""
                    ),
                    PokemonCardUiModel(
                        pokemonId = 2,
                        pokemonNumber = "1",
                        pokemonName = "name",
                        url = ""
                    ),
                    PokemonCardUiModel(
                        pokemonId = 3,
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
        )
    }
}