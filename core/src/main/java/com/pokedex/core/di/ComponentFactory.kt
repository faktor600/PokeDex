package com.pokedex.core.di

typealias ConcreteComponentFactory = ComponentFactory<out ComponentWithViewModel<*>>

interface ComponentFactory<T: ComponentWithViewModel<*>> {
    fun create(): T
}