package com.pokedex.splash_screen.di

import com.pokedex.core.di.ComponentFactory
import com.pokedex.core.di.ComponentWithViewModel
import com.pokedex.core.di.ConcreteComponentFactory
import com.pokedex.splash_screen.viewModel.SplashScreenViewModel
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Subcomponent
interface SplashScreenComponent: ComponentWithViewModel<SplashScreenViewModel> {
    @Subcomponent.Factory
    interface Factory: ComponentFactory<SplashScreenComponent>
}

@Module
abstract class SplashScreenComponentFactoryModule {

    @Binds
    @IntoMap
    @ClassKey(SplashScreenViewModel::class)
    internal abstract fun bindMainScreenProvider(provider: SplashScreenComponent.Factory): ConcreteComponentFactory
}
