package com.pokedex.pokemon_details.di

import com.pokedex.api_pokemon.domain.di.PokemonApiModule
import com.pokedex.core.di.ComponentFactory
import com.pokedex.core.di.ComponentWithViewModel
import com.pokedex.core.di.ConcreteComponentFactory
import com.pokedex.pokemon_details.viewmodel.PokemonDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Subcomponent(
    modules = [
        PokemonApiModule::class,
    ]
)
interface PokemonDetailsComponent: ComponentWithViewModel<PokemonDetailsViewModel> {

    @Subcomponent.Factory
    interface Factory: ComponentFactory<PokemonDetailsComponent>
}

@Module
abstract class PokemonDetailsComponentFactoryModule {

    @Binds
    @IntoMap
    @ClassKey(PokemonDetailsViewModel::class)
    internal abstract fun bindMainScreenProvider(provider: PokemonDetailsComponent.Factory): ConcreteComponentFactory
}