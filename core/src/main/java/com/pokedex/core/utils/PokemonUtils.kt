package com.pokedex.core.utils

import com.pokedex.core.domain.model.PokemonId
import java.util.Locale

fun String.getPokemonNameForPresentation() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(
    Locale.getDefault()) else it.toString() }
    .replace('-', ' ')

fun PokemonId.getPokemonNumberForPresentation() = "#${this.id.toString().padStart(3, '0')}"