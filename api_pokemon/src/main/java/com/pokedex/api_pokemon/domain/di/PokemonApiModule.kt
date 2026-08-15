package com.pokedex.api_pokemon.domain.di

import com.pokedex.api_pokemon.data.di.DataModule
import dagger.Module

@Module(
    includes = [
        DataModule::class
    ]
)
class PokemonApiModule