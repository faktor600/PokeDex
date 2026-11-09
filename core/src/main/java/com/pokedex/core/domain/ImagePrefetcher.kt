package com.pokedex.core.domain

/**
 * Warms the image caches for [url] so the UI can show it without a placeholder.
 * Fire-and-forget: never throws and never blocks the caller.
 */
interface ImagePrefetcher {
    fun prefetch(url: String?)
}
