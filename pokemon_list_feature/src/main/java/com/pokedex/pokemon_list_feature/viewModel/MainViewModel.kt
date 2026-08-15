package com.pokedex.pokemon_list_feature.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.api_pokemon.domain.usecase.GetFirstPokemonPageUseCase
import com.pokedex.api_pokemon.domain.usecase.GetPokemonUseCase
import com.pokedex.compose_core.models.PokemonCardUiModel
import com.pokedex.pokemon_list_feature.state.MainScreenUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getFirstPokemonPageUseCase: GetFirstPokemonPageUseCase,
    private val getPokemonUseCase: GetPokemonUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState>
        get() = _uiState

    private val _sideEffect = MutableSharedFlow<MainScreenSideEffect>()
    val sideEffect: SharedFlow<MainScreenSideEffect>
        get() = _sideEffect

    @OptIn(FlowPreview::class)
    fun initialize() {
        uiState
            .debounce(500L)
            .onEach {
                // Поиск по строке
                println(it.searchText)
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            val result = getFirstPokemonPageUseCase.execute()

            _uiState.update {
                uiState.value.copy(
                    items = result.getOrNull()?.results?.mapNotNull {
                        val item = getPokemonUseCase.execute(it.url)
                        item.getOrNull()?.let {
                            PokemonCardUiModel(
                                pokemonId = it.id,
                                pokemonNumber = it.id.toString(),
                                pokemonName = it.name,
                                url = ""
                            )
                        }
                    } ?: listOf()
                )
            }
        }
    }

    fun handleIntent(intent: MainScreenIntent) {
        when (intent) {
            MainScreenIntent.ClearText -> clearText()
            MainScreenIntent.FiltersClick -> filtersClick()
            is MainScreenIntent.TextChange -> updateSearchText(intent.newText)
        }
    }

    private fun filtersClick() {
        _uiState.update {
            it.copy(
                isFiltersClicked = true
            )
        }
        // Показ экрана фильтров
    }

    private fun clearText() {
        _uiState.update {
            it.copy(
                searchText = ""
            )
        }
    }

    private fun updateSearchText(newText: String) {
        _uiState.update {
            it.copy(
                searchText = newText
            )
        }
    }
}