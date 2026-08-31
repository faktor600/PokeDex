package com.pokedex.pokemon_list_feature.di

import com.pokedex.api_pokemon.domain.di.PokemonApiModule
import com.pokedex.core.di.ComponentFactory
import com.pokedex.core.di.ComponentWithViewModel
import com.pokedex.core.di.ConcreteComponentFactory
import com.pokedex.pokemon_list_feature.viewModel.MainViewModel
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
interface MainScreenComponent: ComponentWithViewModel<MainViewModel> {

    @Subcomponent.Factory
    interface Factory: ComponentFactory<MainScreenComponent>
}

@Module
abstract class MainScreenComponentFactoryModule {

    @Binds
    @IntoMap
    @ClassKey(MainViewModel::class)
    internal abstract fun bindMainScreenProvider(provider: MainScreenComponent.Factory): ConcreteComponentFactory
}