package com.pokedex.core.di

/**
 * Реализуется классом Application и отдаёт карту фабрик компонентов,
 * из которой `daggerViewModel()` создаёт ViewModel.
 */
interface ComponentFactoryMapOwner {
    val componentFactories: Map<Class<*>, @JvmSuppressWildcards ConcreteComponentFactory>
}
