package com.pokedex.di

import android.content.Context
import com.pokedex.compose_core.image.CoilImagePrefetcher
import com.pokedex.core.domain.ImagePrefetcher
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ImageModule {

    @Provides
    @Singleton
    fun imagePrefetcher(context: Context): ImagePrefetcher = CoilImagePrefetcher(context)
}
