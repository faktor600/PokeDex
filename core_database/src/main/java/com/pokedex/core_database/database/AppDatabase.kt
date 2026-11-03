package com.pokedex.core_database.database

import androidx.room3.AutoMigration
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.pokedex.api_pokemon.dao.PokemonDao
import com.pokedex.api_pokemon.dao.PokemonSpeciesDao
import com.pokedex.api_pokemon.dao.entities.PokemonEntity
import com.pokedex.api_pokemon.dao.entities.PokemonSpeciesEntity

@Database(
    entities = [
        PokemonEntity::class,
        PokemonSpeciesEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonSpeciesDao(): PokemonSpeciesDao
}
