package com.pokedex.di

import com.pokedex.core.network.di.NetworkModule
import com.pokedex.pokemon_list_feature.di.MainScreenComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class
    ]
)
interface AppComponent {
}