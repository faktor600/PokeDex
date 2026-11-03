package com.pokedex.core.network

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class DefaultNetworkEvents : NetworkEvents {

    // DROP_OLDEST + buffer: tryEmit never suspends or fails, so it is safe to call from OkHttp threads.
    private val _connectionLost = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    // replay = 1: a screen that starts collecting after the retry (e.g. a new ViewModel) still receives it.
    private val _retryRequests = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val connectionLost: SharedFlow<Unit> = _connectionLost.asSharedFlow()
    override val retryRequests: SharedFlow<Unit> = _retryRequests.asSharedFlow()

    @Volatile
    override var lastRetryRequestedAt: Long = 0L
        private set

    override fun reportConnectionLost() {
        _connectionLost.tryEmit(Unit)
    }

    override fun requestRetry() {
        lastRetryRequestedAt = System.currentTimeMillis()
        _retryRequests.tryEmit(Unit)
    }
}
