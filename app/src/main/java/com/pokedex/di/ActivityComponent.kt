package com.pokedex.di

import com.pokedex.core.di.ComponentFactory
import com.pokedex.core.di.ComponentWithViewModel
import com.pokedex.core.di.ConcreteComponentFactory
import com.pokedex.viewmodel.MainActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Subcomponent
interface ActivityComponent: ComponentWithViewModel<MainActivityViewModel> {

    @Subcomponent.Factory
    interface Factory: ComponentFactory<ActivityComponent>
}

@Module
abstract class ActivityComponentComponentFactoryModule {

    @Binds
    @IntoMap
    @ClassKey(MainActivityViewModel::class)
    internal abstract fun bindMainScreenProvider(provider: ActivityComponent.Factory): ConcreteComponentFactory
}