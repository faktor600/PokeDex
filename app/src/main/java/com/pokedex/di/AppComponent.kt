package com.pokedex.di

import com.pokedex.core.di.ConcreteComponentFactory
import com.pokedex.core.network.di.NetworkModule
import com.pokedex.pokedex_welcome.di.PokedexWelcomeComponent
import com.pokedex.pokedex_welcome.di.PokedexWelcomeComponentFactoryModule
import com.pokedex.pokemon_list_feature.di.MainScreenComponentFactoryModule
import com.pokedex.pokemon_list_feature.di.MainScreenComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        MainScreenComponentFactoryModule::class,
        PokedexWelcomeComponentFactoryModule::class,
        AppContextModule::class,
        ChuckerModule::class,
        DatabaseModule::class
    ]
)
interface AppComponent {

    fun pokedexWelcomeComponentFactory(): PokedexWelcomeComponent.Factory

    fun mainScreenComponentFactory(): MainScreenComponent.Factory

    fun factoriesProvider(): Map<Class<*>, ConcreteComponentFactory>
}