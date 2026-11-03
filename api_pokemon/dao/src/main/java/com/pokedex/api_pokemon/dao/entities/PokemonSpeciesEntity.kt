package com.pokedex.api_pokemon.dao.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.pokedex.api_pokemon.dao.entities.PokemonSpeciesEntity.Companion.SPECIES_TABLE_NAME

/**
 * Raw species response is stored as is: if the data needed by the UI changes, the schema doesn't have to.
 */
@Entity(SPECIES_TABLE_NAME)
data class PokemonSpeciesEntity(
    @PrimaryKey val id: Long,

    @ColumnInfo(SPECIES_NAME)
    val name: String,

    @ColumnInfo(SPECIES_DATA)
    val json: String
) {
    companion object {
        const val SPECIES_TABLE_NAME = "pokemon_species"
        const val SPECIES_NAME = "species_name"
        const val SPECIES_DATA = "species_json_data"
    }
}
