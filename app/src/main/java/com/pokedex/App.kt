package com.pokedex

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.util.DebugLogger
import com.pokedex.di.AppComponent
import com.pokedex.di.AppContextModule
import com.pokedex.di.DaggerAppComponent

internal class App: Application(), SingletonImageLoader.Factory {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder().appContextModule(
            AppContextModule(this)
        ).build()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(this)
            .logger(DebugLogger())
            .build()
    }
}