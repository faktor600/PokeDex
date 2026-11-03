package com.pokedex.api_pokemon.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.pokedex.api_pokemon.dao.entities.PokemonSpeciesEntity
import com.pokedex.api_pokemon.dao.entities.PokemonSpeciesEntity.Companion.SPECIES_TABLE_NAME

@Dao
interface PokemonSpeciesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PokemonSpeciesEntity)

    @Query("SELECT * FROM $SPECIES_TABLE_NAME WHERE id = :id")
    suspend fun getById(id: Long): PokemonSpeciesEntity?

    @Query("DELETE FROM $SPECIES_TABLE_NAME WHERE id = :id")
    suspend fun deleteById(id: Long)
}
