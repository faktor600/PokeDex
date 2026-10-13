package com.pokedex.api_pokemon.dao.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.pokedex.api_pokemon.dao.entities.PokemonEntity.Companion.POKEMON_TABLE_NAME
import com.pokedex.core.domain.model.PokemonId

@Entity(POKEMON_TABLE_NAME)
data class PokemonEntity(
    @PrimaryKey val id: Long,

    @ColumnInfo(POKEMON_NAME)
    val name: String,

    @ColumnInfo(POKEMON_TYPE)
    val type: String,

    @ColumnInfo(POKEMON_MAIN_IMAGE_URL)
    val pokemonMainImageUrl: String?,

    @ColumnInfo(POKEMON_DATA)
    val json: String?
) {
    companion object {
        const val POKEMON_TABLE_NAME = "pokemon"
        const val POKEMON_NAME = "pokemon_name"
        const val POKEMON_DATA = "pokemon_json_data"
        const val POKEMON_TYPE = "pokemon_type"
        const val POKEMON_MAIN_IMAGE_URL = "pokemon_image_url"
    }
}