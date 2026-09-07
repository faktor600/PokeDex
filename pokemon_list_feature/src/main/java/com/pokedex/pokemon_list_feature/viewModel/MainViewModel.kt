package com.pokedex.pokemon_list_feature.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.api_pokemon.domain.model.PokemonHolder
import com.pokedex.api_pokemon.domain.usecase.GetPokemonUseCase
import com.pokedex.api_pokemon.domain.usecase.SearchPokemonsUseCase
import com.pokedex.compose_core.models.PokemonCardUiModel
import com.pokedex.pokemon_list_feature.state.MainScreenUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getPokemonUseCase: GetPokemonUseCase,
    private val searchPokemonsUseCase: SearchPokemonsUseCase
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
            .map { it.searchText }
            .distinctUntilChanged()
            .debounce(200L)
            .onEach {
                val searchResult = searchPokemonsUseCase.execute(it)

                if (searchResult.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = searchResult.getOrNull()?.map { result ->
                                when (result) {
                                    is PokemonHolder.Pokemon -> PokemonCardUiModel.Pokemon(
                                        id = result.id,
                                        pokemonNumber = "#${result.id}",
                                        pokemonName = result.name.replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                        },
                                        url = result.officialArtwork ?: ""
                                    )

                                    is PokemonHolder.PokemonNotLoaded -> {
                                        PokemonCardUiModel.PokemonSkeleton(
                                            pokemonName = result.name,
                                            id = result.id
                                        )
                                    }
                                }
                            } ?: emptyList()
                        )
                    }

                    _uiState.value.items.filterIsInstance<PokemonCardUiModel.PokemonSkeleton>().onEach { skeleton ->
                        viewModelScope.launch {
                            val pokemon = getPokemonUseCase.execute(skeleton.id)

                            if (pokemon.isSuccess) {
                                pokemon.getOrNull()?.let { p ->
                                    val uiModel = PokemonCardUiModel.Pokemon(
                                        id = p.id,
                                        pokemonNumber = "#${p.id}",
                                        pokemonName = p.name.replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                        },
                                        url = p.officialArtwork ?: ""
                                    )
                                    _uiState.update { currentState ->
                                        // Находим индекс скелетона с тем же URL
                                        val index = currentState.items.indexOfFirst { item ->
                                            item is PokemonCardUiModel.PokemonSkeleton && item.id == skeleton.id
                                        }

                                        if (index != -1) {
                                            // Создаем новый список, заменяя элемент по индексу
                                            val newList = currentState.items.toMutableList()
                                            newList[index] = uiModel
                                            currentState.copy(items = newList)
                                        } else {
                                            currentState // Если вдруг не нашли (защита от ошибок)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            items = emptyList()
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
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