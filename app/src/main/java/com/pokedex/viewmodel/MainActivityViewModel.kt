package com.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.core.network.NetworkEvents
import com.pokedex.network.ConnectivityObserver
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject internal constructor(
    private val networkEvents: NetworkEvents,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    /** A foreground request failed because there is no connection. */
    val connectionLost: SharedFlow<Unit> = networkEvents.connectionLost

    /**
     * Validated internet availability of the device; null until the system reports it.
     * Shared, so there is a single NetworkCallback no matter how many collectors there are.
     */
    internal val isOnline: StateFlow<Boolean?> = connectivityObserver.isOnline
        .map<Boolean, Boolean?> { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        // Every offline -> online transition asks the screens to repeat what failed in the background
        // (e.g. list prefetch), not only when the "no internet" screen was shown.
        viewModelScope.launch {
            var wasOnline: Boolean? = null
            isOnline.collect { online ->
                if (online == true && wasOnline == false) networkEvents.requestRetry()
                if (online != null) wasOnline = online
            }
        }
    }

    /** Asks the screens below the "no internet" screen to repeat their failed requests. */
    fun onRetryRequested() {
        networkEvents.requestRetry()
    }
}
