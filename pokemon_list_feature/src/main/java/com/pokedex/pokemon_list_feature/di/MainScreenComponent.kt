package com.pokedex.pokemon_list_feature.di

import com.pokedex.api_pokemon.domain.di.PokemonApiModule
import com.pokedex.core.di.ComponentWithViewModel
import com.pokedex.core.network.di.NetworkModule
import com.pokedex.pokemon_list_feature.viewModel.MainViewModel
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Scope

@Component(
    modules = [
        MainViewModelModule::class, NetworkModule::class
    ]
)
interface MainScreenComponent: ComponentWithViewModel<MainViewModel> {

}

@Module(
    includes = [
        PokemonApiModule::class
    ]
)
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ClassKey(MainViewModel::class)
    abstract fun provideViewModel(impl: MainViewModel): MainViewModel
}