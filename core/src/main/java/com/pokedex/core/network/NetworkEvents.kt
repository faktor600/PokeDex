package com.pokedex.core.network

import kotlinx.coroutines.flow.SharedFlow

/**
 * Shared bus for "no internet" signals between the network layer, the screens and the navigation host.
 *
 * - [connectionLost] is emitted when a foreground request fails because there is no connection.
 * - [retryRequests] is emitted when screens should repeat the requests that failed.
 */
interface NetworkEvents {
    val connectionLost: SharedFlow<Unit>
    val retryRequests: SharedFlow<Unit>

    /** Time ([System.currentTimeMillis]) of the last [requestRetry], 0 if never. Lets caches drop failure cooldowns. */
    val lastRetryRequestedAt: Long

    fun reportConnectionLost()
    fun requestRetry()
}
