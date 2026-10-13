package com.pokedex.core

import java.util.logging.Level
import java.util.logging.Logger

enum class PokemonTypeEnum {
    NORMAL,
    FIGHTING,
    FLYING,
    POISON,
    GROUND,
    ROCK,
    BUG,
    GHOST,
    STEEL,
    FIRE,
    WATER,
    GRASS,
    ELECTRIC,
    PSYCHIC,
    ICE,
    DRAGON,
    DARK,
    FAIRY,
    STELLAR,
    SHADOW,
    UNKNOWN
}

fun PokemonTypeEnum.asStringBackend() = name.lowercase()

fun String.getTypeEnum() = try {
    PokemonTypeEnum.valueOf(this.uppercase())
} catch (e: Exception) {
    Logger.getGlobal().log(
        Level.WARNING, e.message
    )
    PokemonTypeEnum.UNKNOWN
}