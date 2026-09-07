package com.pokedex.api_pokemon.domain.model

import com.pokedex.core.domain.model.ItemWithUrl

sealed class PokemonHolder {
    abstract val id: Long
    abstract val name: String

    data class Pokemon(
        override val id: Long,
        override val name: String,
        val baseExperience: Int?,
        val height: Int,
        val isDefault: Boolean,
        val abilities: List<Ability>,
        val cries: Cry,
        val forms: List<String>,
        val locationAreaEncounters: String,
        val moves: List<MoveEntry>,
        val stats: List<Stat>,
        val officialArtwork: String?
    ): PokemonHolder()

    data class PokemonNotLoaded(
        override val id: Long,
        override val name: String,
    ): PokemonHolder()
}

data class Stat(
    val baseStat: Int,
    val effort: Int,
    val stat: ItemWithUrl
)

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

data class MoveEntry(
    val move: ItemWithUrl
)
