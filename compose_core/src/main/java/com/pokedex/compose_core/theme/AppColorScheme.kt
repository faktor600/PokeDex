package com.pokedex.compose_core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
internal fun colorScheme(
    identity: Identity = Identity(),
    pokemonTypeColor: PokemonTypeColor = PokemonTypeColor(),
    grayscale: Grayscale = Grayscale()
) = AppColorScheme(
    identity = identity,
    pokemonTypeColor = pokemonTypeColor,
    grayscale = grayscale
)

@Immutable
class AppColorScheme(
    val identity: Identity = Identity(),
    val pokemonTypeColor: PokemonTypeColor = PokemonTypeColor(),
    val grayscale: Grayscale = Grayscale()
)

@Immutable
class Identity(
    val primary: Color = Color(0xFFDC0A2D)
)

@Immutable
class PokemonTypeColor(
    val bug: Color = Color(0xFFA7B723),
    val dark: Color = Color(0xFF75574C),
    val dragon: Color = Color(0xFF7037FF),
    val electric: Color = Color(0xFFF9CF30),
    val fairy: Color = Color(0xFFE69EAC),
    val fighting: Color = Color(0xFFC12239),
    val fire: Color = Color(0xFFF57D31),
    val flying: Color = Color(0xFFA891EC),
    val ghost: Color = Color(0xFF70559B),
    val grass: Color = Color(0xFF74CB48),
    val ground: Color = Color(0xFFDEC16B),
    val ice: Color = Color(0xFF9AD6DF),
    val normal: Color = Color(0xFFAAA67F),
    val poison: Color = Color(0xFFA43E9E),
    val physic: Color = Color(0xFFFB5584),
    val rock: Color = Color(0xFFB69E31),
    val steel: Color = Color(0xFFB7B9D0),
    val water: Color = Color(0xFF6493EB),
    val stellar: Color = Color(0xFF40B5A5)
)

@Immutable
class Grayscale(
    val dark: Color = Color(0xFF1D1D1D),
    val medium: Color = Color(0xFF666666),
    val light: Color = Color(0xFFE0E0E0),
    val background: Color = Color(0xFFEFEFEF),
    val white: Color = Color.White,
    val whireframe: Color = Color(0xFFB8B8B8)
)