package com.pokedex.splash_screen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.data_store.prefs.PokedexWelcomePreferences
import com.pokedex.splash_screen.state.SplashScreenUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


class SplashScreenViewModel @Inject constructor(
    private val pokedexWelcomePreferences: PokedexWelcomePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashScreenUiState>(SplashScreenUiState.Loading)
    internal val uiState: StateFlow<SplashScreenUiState>
        get() = _uiState

    private val _sideEffect = MutableSharedFlow<SplashScreenSideEffect>()
    internal val sideEffect: SharedFlow<SplashScreenSideEffect>
        get() = _sideEffect

    fun initialize() {
        viewModelScope.launch {
            _uiState.update {
                SplashScreenUiState.Ready(
                    isPokedexDatabaseFilled = pokedexWelcomePreferences.isPokemonDataCachedFlow.first()
                )
            }
        }
    }

    internal fun handleIntent(intent: SplashScreenScreenIntent) {
        when (intent) {
            SplashScreenScreenIntent.MoveToNextScreen -> {
                viewModelScope.launch {
                    if ((uiState.value as? SplashScreenUiState.Ready)?.isPokedexDatabaseFilled == true) {
                        _sideEffect.emit(SplashScreenSideEffect.MoveToPokemonList)
                    } else {
                        _sideEffect.emit(SplashScreenSideEffect.MoveToWelcomeScreen)
                    }
                }
            }
        }
    }
}

