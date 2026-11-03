package com.pokedex.pokemon_list_feature.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.api_pokemon.domain.model.PokemonSearchItem
import com.pokedex.api_pokemon.domain.usecase.PrefetchPokemonsUseCase
import com.pokedex.api_pokemon.domain.usecase.SearchPokemonsUseCase
import com.pokedex.core.domain.model.PokemonId
import com.pokedex.compose_core.models.PokemonCardUiModel
import com.pokedex.core.utils.getPokemonNameForPresentation
import com.pokedex.core.utils.getPokemonNumberForPresentation
import com.pokedex.pokemon_list_feature.state.PokemonListUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_DELAY = 300L

class PokemonListViewModel @Inject constructor(
    private val searchPokemonsUseCase: SearchPokemonsUseCase,
    private val prefetchPokemonsUseCase: PrefetchPokemonsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<PokemonListUiState>(PokemonListUiState.Loading)
    val uiState: StateFlow<PokemonListUiState>
        get() = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<MainScreenSideEffect>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sideEffect: SharedFlow<MainScreenSideEffect>
        get() = _sideEffect.asSharedFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String>
        get() = _searchText.asStateFlow()

    init {
        initialize()
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun initialize() {
        viewModelScope.launch {
            _searchText
                .debounce(SEARCH_DELAY)
                .distinctUntilChanged()
                .flatMapLatest { searchPokemonsUseCase.execute(it) }
                .collectLatest { searchResult ->
                    val newState = if (searchResult.isEmpty()) {
                        PokemonListUiState.Empty
                    } else {
                        PokemonListUiState.ListLoaded(
                            items = searchResult.map { result ->
                                viewModelScope.async {
                                    ensureActive()

                                    when (result) {
                                        is PokemonSearchItem.LoadedPokemon -> PokemonCardUiModel.Pokemon(
                                            id = result.id,
                                            pokemonNumber = result.id.getPokemonNumberForPresentation(),
                                            pokemonName = result.name.getPokemonNameForPresentation(),
                                            url = result.pokemonMainImageUrl ?: ""
                                        )

                                        is PokemonSearchItem.NotLoaded -> PokemonCardUiModel.PokemonSkeleton(
                                            pokemonName = result.name.getPokemonNameForPresentation(),
                                            id = result.id
                                        )
                                    }
                                }
                            }.awaitAll()
                        )
                    }

                    _uiState.update { newState }
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

    /** Downloads details only for the cards the user is about to see (not for the whole Pokédex). */
    fun onPokemonsNeeded(ids: List<PokemonId>) {
        if (ids.isEmpty()) return
        viewModelScope.launch { prefetchPokemonsUseCase.execute(ids) }
    }

    private fun filtersClick() {
        _sideEffect.tryEmit(MainScreenSideEffect.ShowFilters)
    }

    private fun clearText() {
        _searchText.value = ""
        _uiState.update {
            PokemonListUiState.Loading
        }
    }

    private fun updateSearchText(newText: String) {
        _searchText.value = newText
        _uiState.update {
            PokemonListUiState.Loading
        }
    }
}