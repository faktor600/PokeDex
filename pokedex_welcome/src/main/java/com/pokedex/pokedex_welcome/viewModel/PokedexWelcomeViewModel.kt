package com.pokedex.pokedex_welcome.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.api_pokemon.domain.usecase.GetFirstPokemonPageUseCase
import com.pokedex.api_pokemon.domain.usecase.GetNextPokemonPageUseCase
import com.pokedex.data_store.prefs.PokedexWelcomePreferences
import com.pokedex.pokedex_welcome.state.PokedexWelcomeUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class PokedexWelcomeViewModel @Inject constructor(
    private val pokedexWelcomePreferences: PokedexWelcomePreferences,
    private val getFirstPokemonPageUseCase: GetFirstPokemonPageUseCase,
    private val getNextPokemonPageUseCase: GetNextPokemonPageUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<PokedexWelcomeUiState>(PokedexWelcomeUiState.Welcome)
    internal val uiState: StateFlow<PokedexWelcomeUiState>
        get() = _uiState

    private val _sideEffect = MutableSharedFlow<PokedexWelcomeSideEffect>()
    internal val sideEffect: SharedFlow<PokedexWelcomeSideEffect>
        get() = _sideEffect

    fun initialize() {
        _uiState.update {
            PokedexWelcomeUiState.Welcome
        }
    }

    internal fun handleIntent(intent: PokedexWelcomeScreenIntent) {
        when (intent) {
            PokedexWelcomeScreenIntent.MoveToDownloadDatabaseStep -> loadData()
            PokedexWelcomeScreenIntent.MoveToPokemonsList -> viewModelScope.launch {
                _sideEffect.emit(
                    PokedexWelcomeSideEffect.MoveToPokemonList
                )
            }
        }
    }

    private fun loadData() {
        _uiState.update {
            PokedexWelcomeUiState.LoadingDatabase()
        }

        viewModelScope.launch {
            val firstPage = getFirstPokemonPageUseCase.execute().getOrNull()
            var nextPageUrl = firstPage?.nextUrl
            val totalCount = firstPage?.pokemonCount ?: 0
            var totalLoaded = 0

            while (nextPageUrl?.isNotEmpty() == true){
                val nextPage = getNextPokemonPageUseCase.execute(nextPageUrl).getOrNull()
                totalLoaded += nextPage?.loadedCount ?: 0

                _uiState.update {
                    (it as PokedexWelcomeUiState.LoadingDatabase).copy(
                        progress = totalLoaded / totalCount.toFloat(),
                        progressText = "${((totalLoaded / totalCount.toFloat()) * 100).toInt()}%"
                    )
                }

                nextPageUrl = nextPage?.nextUrl
            }

            pokedexWelcomePreferences.setPokemonDataCached(true)

            _uiState.update {
                PokedexWelcomeUiState.DatabaseLoadedSuccessfully
            }
        }
    }
}