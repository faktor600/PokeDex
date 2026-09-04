package com.pokedex.pokedex_welcome.di

import com.pokedex.api_pokemon.domain.di.PokemonApiModule
import com.pokedex.core.di.ComponentFactory
import com.pokedex.core.di.ComponentWithViewModel
import com.pokedex.core.di.ConcreteComponentFactory
import com.pokedex.pokedex_welcome.viewModel.PokedexWelcomeViewModel
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
interface PokedexWelcomeComponent: ComponentWithViewModel<PokedexWelcomeViewModel> {
    @Subcomponent.Factory
    interface Factory: ComponentFactory<PokedexWelcomeComponent>
}

@Module
abstract class PokedexWelcomeComponentFactoryModule {

    @Binds
    @IntoMap
    @ClassKey(PokedexWelcomeViewModel::class)
    internal abstract fun bindMainScreenProvider(provider: PokedexWelcomeComponent.Factory): ConcreteComponentFactory
}