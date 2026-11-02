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

    /**
     * Inserts stub rows. Existing rows (possibly already fully loaded) are kept untouched.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemons(items: List<PokemonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(item: PokemonEntity)

    @Query("SELECT * FROM $POKEMON_TABLE_NAME")
    fun getAll(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM $POKEMON_TABLE_NAME WHERE id = :id")
    suspend fun getById(id: Long): PokemonEntity?

    /**
     * [likePattern] must already be escaped with `\` for `%` and `_`.
     */
    @Query(
        "SELECT * FROM $POKEMON_TABLE_NAME WHERE id = :id OR $POKEMON_NAME LIKE :likePattern || '%' ESCAPE '\\'"
    )
    fun searchForPokemons(id: Long?, likePattern: String): Flow<List<PokemonEntity>>
}