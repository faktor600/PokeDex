package com.pokedex.api_pokemon.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.pokedex.api_pokemon.dao.entities.PokemonEntity
import com.pokedex.api_pokemon.dao.entities.PokemonEntity.Companion.POKEMON_DATA
import com.pokedex.api_pokemon.dao.entities.PokemonEntity.Companion.POKEMON_MAIN_IMAGE_URL
import com.pokedex.api_pokemon.dao.entities.PokemonEntity.Companion.POKEMON_NAME
import com.pokedex.api_pokemon.dao.entities.PokemonEntity.Companion.POKEMON_TABLE_NAME
import com.pokedex.api_pokemon.dao.entities.PokemonEntity.Companion.POKEMON_TYPE
import com.pokedex.api_pokemon.dao.entities.PokemonListRow
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

    @Query(
        "SELECT id, $POKEMON_NAME AS name, $POKEMON_TYPE AS type, $POKEMON_MAIN_IMAGE_URL AS imageUrl, " +
            "($POKEMON_DATA IS NOT NULL) AS isLoaded FROM $POKEMON_TABLE_NAME ORDER BY id"
    )
    fun getAllRows(): Flow<List<PokemonListRow>>

    @Query("SELECT * FROM $POKEMON_TABLE_NAME WHERE id = :id")
    suspend fun getById(id: Long): PokemonEntity?

    /** Ids from [ids] that exist as stubs and whose details haven't been downloaded yet. */
    @Query("SELECT id FROM $POKEMON_TABLE_NAME WHERE id IN (:ids) AND $POKEMON_DATA IS NULL")
    suspend fun getNotLoadedIds(ids: List<Long>): List<Long>

    /**
     * [likePattern] must already be escaped with `\` for `%` and `_`.
     */
    @Query(
        "SELECT id, $POKEMON_NAME AS name, $POKEMON_TYPE AS type, $POKEMON_MAIN_IMAGE_URL AS imageUrl, " +
            "($POKEMON_DATA IS NOT NULL) AS isLoaded FROM $POKEMON_TABLE_NAME " +
            "WHERE id = :id OR $POKEMON_NAME LIKE :likePattern || '%' ESCAPE '\\' ORDER BY id"
    )
    fun searchRows(id: Long?, likePattern: String): Flow<List<PokemonListRow>>
}
