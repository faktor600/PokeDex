package com.pokedex.di

import com.pokedex.core.di.ConcreteComponentFactory
import com.pokedex.core.network.di.NetworkModule
import com.pokedex.data_store.di.PokedexWelcomeDataStoreModule
import com.pokedex.pokedex_welcome.di.PokedexWelcomeComponent
import com.pokedex.pokedex_welcome.di.PokedexWelcomeComponentFactoryModule
import com.pokedex.pokemon_details.di.PokemonDetailsComponent
import com.pokedex.pokemon_details.di.PokemonDetailsComponentFactoryModule
import com.pokedex.pokemon_list_feature.di.MainScreenComponentFactoryModule
import com.pokedex.pokemon_list_feature.di.MainScreenComponent
import com.pokedex.splash_screen.di.SplashScreenComponent
import com.pokedex.splash_screen.di.SplashScreenComponentFactoryModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        PokedexWelcomeDataStoreModule::class,
        MainScreenComponentFactoryModule::class,
        PokedexWelcomeComponentFactoryModule::class,
        ActivityComponentComponentFactoryModule::class,
        SplashScreenComponentFactoryModule::class,
        PokemonDetailsComponentFactoryModule::class,
        AppContextModule::class,
        ChuckerModule::class,
        DatabaseModule::class
    ]
)
interface AppComponent {

    fun pokemonDetailsComponentFactory(): PokemonDetailsComponent.Factory

    fun splashScreenComponentFactory(): SplashScreenComponent.Factory

    fun mainActivityComponentFactory(): ActivityComponent.Factory

    fun pokedexWelcomeComponentFactory(): PokedexWelcomeComponent.Factory

    fun mainScreenComponentFactory(): MainScreenComponent.Factory

    fun factoriesProvider(): Map<Class<*>, ConcreteComponentFactory>
}