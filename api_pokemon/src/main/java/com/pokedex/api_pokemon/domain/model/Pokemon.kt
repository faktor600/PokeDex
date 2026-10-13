package com.pokedex.api_pokemon.domain.model

import com.pokedex.core.PokemonTypeEnum
import com.pokedex.core.domain.model.ItemWithUrl
import com.pokedex.core.domain.model.PokemonId

data class Pokemon(
    val id: PokemonId,
    val name: String,
    val speciesLink: String,
    val weight: Int,
    val baseExperience: Int?,
    val height: Int,
    val isDefault: Boolean,
    val abilities: List<Ability>,
    val cries: Cry,
    val forms: List<String>,
    val locationAreaEncounters: String,
    val moves: List<MoveEntry>,
    val stats: List<Stat>,
    val types: List<Type>,
    val officialArtwork: String?
)

data class Stat(
    val baseStat: Int,
    val effort: Int,
    val stat: ItemWithUrl
) {
    companion object {
        const val HP = "hp"
        const val ATTACK = "attack"
        const val DEFENCE = "defence"
        const val SP_ATTACK = "special-attack"
        const val SP_DEFENCE = "special-defense"
        const val SPEED = "speed"
    }
}

data class Ability(
    val name: String,
    val url: String,
    val isHidden: Boolean,
    val slot: Int
)

data class Cry(
    val latest: String,
    val legacy: String
)

data class  MoveEntry(
    val move: ItemWithUrl
)

data class Type(
    val name: String,
    val typeEnum: PokemonTypeEnum,
    val url: String
)
