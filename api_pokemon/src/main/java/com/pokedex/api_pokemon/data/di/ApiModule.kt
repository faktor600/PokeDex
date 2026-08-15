package com.pokedex.api_pokemon.data.di

import com.pokedex.api_pokemon.data.api.PokemonApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.create

@Module
internal class ApiModule {

    @Provides
    fun provideApi(
        retrofit: Retrofit
    ) = retrofit.create<PokemonApi>()
}