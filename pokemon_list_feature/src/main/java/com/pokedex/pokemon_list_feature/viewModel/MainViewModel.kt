package com.pokedex.pokemon_list_feature.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.api_pokemon.domain.usecase.GetFirstPokemonPageUseCase
import com.pokedex.api_pokemon.domain.usecase.GetNextPokemonPageUseCase
import com.pokedex.api_pokemon.domain.usecase.GetPokemonUseCase
import com.pokedex.api_pokemon.domain.usecase.SearchPokemonsUseCase
import com.pokedex.compose_core.models.PokemonCardUiModel
import com.pokedex.pokemon_list_feature.state.MainScreenUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
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
import kotlinx.coroutines.sync.withPermit
import java.util.Locale
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getFirstPokemonPageUseCase: GetFirstPokemonPageUseCase,
    private val getPokemonUseCase: GetPokemonUseCase,
    private val getNextPokemonPageUseCase: GetNextPokemonPageUseCase,
    private val searchPokemonsUseCase: SearchPokemonsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState>
        get() = _uiState

    private val _sideEffect = MutableSharedFlow<MainScreenSideEffect>()
    val sideEffect: SharedFlow<MainScreenSideEffect>
        get() = _sideEffect

    private var nextPageJob: Job? = null

    val semaphore = kotlinx.coroutines.sync.Semaphore(20)

    @OptIn(FlowPreview::class)
    fun initialize() {
        uiState
            .map { it.searchText }
            .distinctUntilChanged()
            .debounce(200L)
            .onEach {
                if (it.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            items = emptyList(),
                            nextPageUrl = ""
                        )
                    }
                    loadNextPage()
                } else {
                    val searchResult = searchPokemonsUseCase.execute(it)

                    if (searchResult.isSuccess) {
                        _uiState.update {
                            it.copy(
                                items = searchResult.getOrNull()?.map {
                                    PokemonCardUiModel.Pokemon(
                                        urlId = it.id.toString(),
                                        pokemonNumber = "#${it.id}",
                                        pokemonName = it.name.replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                        },
                                        url = it.officialArtwork ?: ""
                                    )
                                } ?: emptyList()
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                items = emptyList()
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadNextPage() {
        if (nextPageJob == null) {
            nextPageJob = viewModelScope.launch {
                val nextPage = when {
                    _uiState.value.nextPageUrl?.isEmpty() == true -> getFirstPokemonPageUseCase.execute()

                    _uiState.value.nextPageUrl != null -> getNextPokemonPageUseCase.execute(_uiState.value.nextPageUrl ?: return@launch)

                    else -> return@launch
                }


                val pokemonList = nextPage.getOrNull()?.results ?: listOf()
                val skeletons = pokemonList.map {
                    PokemonCardUiModel.PokemonSkeleton(
                        urlId = it.url,
                        pokemonName = it.name ?: ""
                    )
                }

                _uiState.update {
                    it.copy(
                        items = it.items + skeletons,
                        nextPageUrl = nextPage.getOrNull()?.next,
                        isLoading = false
                    )
                }

                nextPageJob = null

                // 2. Запускаем загрузку каждого покемона с ограничением на количество параллельных запросов
                pokemonList.forEach { item ->
                    launch {
                        semaphore.withPermit {
                            val pokemon = getPokemonUseCase.execute(item.url)

                            if (pokemon.isFailure) {
                                error("${item.url} error ${pokemon}")
                            }

                            pokemon.getOrNull()?.let { p ->
                                val uiModel = PokemonCardUiModel.Pokemon(
                                    urlId = item.url,
                                    pokemonNumber = "#${p.id}",
                                    pokemonName = p.name.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                    },
                                    url = p.officialArtwork ?: ""
                                )

                                // 3. АТОМАРНО добавляем покемона в текущий список
                                // update() гарантирует, что изменения будут применены безопасно,
                                // даже если 20 корутины вызовут его одновременно.
                                _uiState.update { currentState ->
                                    // Находим индекс скелетона с тем же URL
                                    val index = currentState.items.indexOfFirst {
                                        it is PokemonCardUiModel.PokemonSkeleton && it.urlId == item.url
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