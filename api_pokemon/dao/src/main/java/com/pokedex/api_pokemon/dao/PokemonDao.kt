package com.pokedex.api_pokemon.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.pokedex.api_pokemon.dao.entities.PokemonEntity
import com.pokedex.api_pokemon.dao.entities.PokemonEntity.Companion.POKEMON_NAME
import com.pokedex.api_pokemon.dao.entities.PokemonEntity.Companion.POKEMON_TABLE_NAME
import com.pokedex.core.domain.model.PokemonId
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemons(items: List<PokemonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(item: PokemonEntity)

    @Query("SELECT * FROM $POKEMON_TABLE_NAME")
    fun getAll(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM $POKEMON_TABLE_NAME WHERE id = :id OR $POKEMON_NAME LIKE :name || '%'")
    suspend fun getPokemon(id: Long?, name: String?): PokemonEntity?

    @Query("SELECT * FROM $POKEMON_TABLE_NAME WHERE id = :text OR $POKEMON_NAME LIKE :text || '%'")
    fun searchForPokemons(text: String): Flow<List<PokemonEntity>>
}