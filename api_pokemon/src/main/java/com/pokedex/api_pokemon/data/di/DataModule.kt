package com.pokedex.api_pokemon.data.di

import com.pokedex.api_pokemon.data.PokemonRepositoryImpl
import com.pokedex.api_pokemon.domain.PokemonRepository
import dagger.Binds
import dagger.Module

@Module(
    includes = [
        ApiModule::class
    ]
)
internal abstract class DataModule {

    @Binds
    internal abstract fun bindRepository(impl: PokemonRepositoryImpl): PokemonRepository
}