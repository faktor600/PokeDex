package com.pokedex.pokedex_welcome.viewModel

import androidx.lifecycle.ViewModel
import com.pokedex.pokedex_welcome.state.PokedexWelcomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class PokedexWelcomeViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow<PokedexWelcomeUiState>(PokedexWelcomeUiState.Welcome)
    internal val uiState: StateFlow<PokedexWelcomeUiState>
        get() = _uiState

    fun initialize() {
        _uiState.update {
            PokedexWelcomeUiState.Welcome
        }
    }

    internal fun handleIntent(intent: PokedexWelcomeScreenIntent) {
        when (intent) {
            PokedexWelcomeScreenIntent.MoveToDownloadDatabaseStep -> _uiState.update {
                PokedexWelcomeUiState.LoadingDatabase()
            }
            PokedexWelcomeScreenIntent.MoveToFinishDownloadingStep -> _uiState.update {
                PokedexWelcomeUiState.DatabaseLoadedSuccessfully
            }
        }
    }
}