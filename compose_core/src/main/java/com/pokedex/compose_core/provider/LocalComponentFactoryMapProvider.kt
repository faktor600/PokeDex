package com.pokedex.compose_core.provider

import androidx.compose.runtime.compositionLocalOf
import com.pokedex.core.di.ConcreteComponentFactory

val LocalComponentFactoryMapProvider = compositionLocalOf<Map<Class<*>, @JvmSuppressWildcards ConcreteComponentFactory>> {
    error("No map with component factory provided")
}