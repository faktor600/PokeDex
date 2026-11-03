package com.pokedex.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.UnknownHostException

/**
 * Header that marks a request as background (e.g. list prefetch). Failures of such requests are not
 * reported to [NetworkEvents], because the screen already has data to show. The header is never sent.
 */
const val BACKGROUND_REQUEST_HEADER = "X-Background-Request"

/**
 * Reports "no connection" failures to [NetworkEvents] and rethrows them unchanged, so the callers'
 * own retry and error handling keeps working.
 */
internal class ConnectionLostInterceptor(
    private val networkEvents: NetworkEvents
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val isBackground = original.header(BACKGROUND_REQUEST_HEADER) != null
        val request = if (isBackground) {
            original.newBuilder().removeHeader(BACKGROUND_REQUEST_HEADER).build()
        } else {
            original
        }

        try {
            return chain.proceed(request)
        } catch (e: IOException) {
            if (!isBackground && e.isConnectionProblem()) {
                networkEvents.reportConnectionLost()
            }
            throw e
        }
    }

    // Timeouts are not included: a slow but working connection must not show the "no internet" screen.
    private fun IOException.isConnectionProblem() =
        this is UnknownHostException ||
            this is ConnectException ||
            this is NoRouteToHostException
}
