package com.pokedex.core_database.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.pokedex.api_pokemon.dao.PokemonDao
import com.pokedex.api_pokemon.dao.entities.PokemonEntity

@Database(
    entities = [
        PokemonEntity::class
               ],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}