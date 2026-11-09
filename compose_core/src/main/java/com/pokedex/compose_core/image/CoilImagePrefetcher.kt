package com.pokedex.compose_core.image

import android.content.Context
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Size
import com.pokedex.core.domain.ImagePrefetcher

class CoilImagePrefetcher(context: Context) : ImagePrefetcher {

    private val appContext = context.applicationContext

    override fun prefetch(url: String?) {
        if (url.isNullOrBlank()) return

        val loader = SingletonImageLoader.get(appContext)
        // Already decoded in memory: nothing to do.
        if (loader.memoryCache?.get(MemoryCache.Key(url)) != null) return

        loader.enqueue(
            ImageRequest.Builder(appContext)
                .data(url)
                // ORIGINAL keeps the cached entry valid for any display size.
                .size(Size.ORIGINAL)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build()
        )
    }
}
