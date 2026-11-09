package com.pokedex.compose_core.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.compose.asPainter
import coil3.memory.MemoryCache
import coil3.request.ImageRequest

/**
 * [AsyncImage] that draws an image already decoded in the Coil memory cache in the very first frame,
 * so no placeholder is shown for it. Otherwise falls back to a regular [AsyncImage].
 *
 * The memory cache is filled by [com.pokedex.core.domain.ImagePrefetcher] and by earlier screens.
 */
@Composable
fun CachedAsyncImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
    error: Painter? = placeholder,
    fallback: Painter? = error,
    contentScale: ContentScale = ContentScale.Fit
) {
    val context = LocalContext.current
    val key = url?.takeIf { it.isNotBlank() }

    val cachedPainter = remember(key) {
        key?.let { SingletonImageLoader.get(context).memoryCache?.get(MemoryCache.Key(it))?.image }
            ?.asPainter(context)
    }

    if (cachedPainter != null) {
        Image(
            modifier = modifier,
            painter = cachedPainter,
            contentDescription = contentDescription,
            contentScale = contentScale
        )
    } else {
        AsyncImage(
            modifier = modifier,
            model = ImageRequest.Builder(context)
                .data(key)
                // while the full image loads, show the one cached from the list
                .placeholderMemoryCacheKey(key)
                .build(),
            contentDescription = contentDescription,
            placeholder = placeholder,
            error = error,
            fallback = fallback,
            contentScale = contentScale
        )
    }
}
