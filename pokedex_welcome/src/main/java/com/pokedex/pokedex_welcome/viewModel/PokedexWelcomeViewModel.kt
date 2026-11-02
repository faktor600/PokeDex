package com.pokedex.pokedex_welcome.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.api_pokemon.domain.usecase.LoadPokemonPageUseCase
import com.pokedex.data_store.prefs.PokedexWelcomePreferences
import com.pokedex.pokedex_welcome.state.PokedexWelcomeUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PokedexWelcomeViewModel @Inject constructor(
    private val pokedexWelcomePreferences: PokedexWelcomePreferences,
    private val loadPokemonPageUseCase: LoadPokemonPageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokedexWelcomeUiState>(PokedexWelcomeUiState.Welcome)
    internal val uiState: StateFlow<PokedexWelcomeUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<PokedexWelcomeSideEffect>(Channel.BUFFERED)
    internal val sideEffect: Flow<PokedexWelcomeSideEffect> = _sideEffect.receiveAsFlow()

    private var loadJob: Job? = null

    /**
     * Resets the screen to [PokedexWelcomeUiState.Welcome] only when nothing is in progress,
     * so re-entering composition during a download doesn't drop the loading UI.
     */
    internal fun initialize() {
        if (loadJob?.isActive == true) return
        _uiState.value = PokedexWelcomeUiState.Welcome
    }

    internal fun handleIntent(intent: PokedexWelcomeScreenIntent) {
        when (intent) {
            PokedexWelcomeScreenIntent.MoveToDownloadDatabaseStep -> loadData()
            PokedexWelcomeScreenIntent.MoveToPokemonsList ->
                _sideEffect.trySend(PokedexWelcomeSideEffect.MoveToPokemonList)
        }
    }

    private fun loadData() {
        if (loadJob?.isActive == true) return

        _uiState.value = PokedexWelcomeUiState.LoadingDatabase()

        loadJob = viewModelScope.launch {
            var url: String? = null
            var totalLoaded = 0

            do {
                val page = loadPokemonPageUseCase.execute(url).getOrElse { error ->
                    if (error is CancellationException) throw error
                    _uiState.value = PokedexWelcomeUiState.DatabaseLoadFailed
                    return@launch
                }

                totalLoaded += page.loadedCount
                val progress = if (page.totalCount > 0) {
                    (totalLoaded / page.totalCount.toFloat()).coerceIn(0f, 1f)
                } else {
                    0f
                }
                _uiState.value = PokedexWelcomeUiState.LoadingDatabase(
                    progress = progress,
                    progressText = "${(progress * 100).toInt()}%"
                )

                url = page.nextUrl
            } while (url != null)

            try {
                pokedexWelcomePreferences.setPokemonDataCached(true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = PokedexWelcomeUiState.DatabaseLoadFailed
                return@launch
            }

            _uiState.value = PokedexWelcomeUiState.DatabaseLoadedSuccessfully
        }
    }
}
