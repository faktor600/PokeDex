package com.pokedex.compose_core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.pokedex.compose_core.R
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.core.PokemonTypeEnum

@Immutable
sealed class PokemonTypePresentationModel(
    val type: PokemonTypeEnum,
    val name: TextUiModel
) {
    @get:Composable
    abstract val color: Color

    object Normal : PokemonTypePresentationModel(
        type = PokemonTypeEnum.NORMAL,
        name = TextUiModel(R.string.pokemon_type_name_normal),
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().normal
    }

    object Fighting : PokemonTypePresentationModel(
        type = PokemonTypeEnum.FIGHTING,
        name = TextUiModel(R.string.pokemon_type_name_fighting)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().fighting
    }

    object Flying : PokemonTypePresentationModel(
        type = PokemonTypeEnum.FLYING,
        name = TextUiModel(R.string.pokemon_type_name_flying)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().flying
    }

    object Poison : PokemonTypePresentationModel(
        type = PokemonTypeEnum.POISON,
        name = TextUiModel(R.string.pokemon_type_name_poison)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().poison
    }

    object Ground : PokemonTypePresentationModel(
        type = PokemonTypeEnum.GROUND,
        name = TextUiModel(R.string.pokemon_type_name_ground)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().ground
    }

    object Rock : PokemonTypePresentationModel(
        type = PokemonTypeEnum.ROCK,
        name = TextUiModel(R.string.pokemon_type_name_rock)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().rock
    }

    object Bug : PokemonTypePresentationModel(
        type = PokemonTypeEnum.BUG,
        name = TextUiModel(R.string.pokemon_type_name_bug)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().bug
    }

    object Ghost : PokemonTypePresentationModel(
        type = PokemonTypeEnum.GHOST,
        name = TextUiModel(R.string.pokemon_type_name_ghost)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().ghost
    }

    object Steel : PokemonTypePresentationModel(
        type = PokemonTypeEnum.STEEL,
        name = TextUiModel(R.string.pokemon_type_name_steel)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().steel
    }

    object Fire : PokemonTypePresentationModel(
        type = PokemonTypeEnum.FIRE,
        name = TextUiModel(R.string.pokemon_type_name_fire)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().fire
    }

    object Water : PokemonTypePresentationModel(
        type = PokemonTypeEnum.WATER,
        name = TextUiModel(R.string.pokemon_type_name_water)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().water
    }

    object Grass : PokemonTypePresentationModel(
        type = PokemonTypeEnum.GRASS,
        name = TextUiModel(R.string.pokemon_type_name_grass)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().grass
    }

    object Electric : PokemonTypePresentationModel(
        type = PokemonTypeEnum.ELECTRIC,
        name = TextUiModel(R.string.pokemon_type_name_electric)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().electric
    }

    object Psychic : PokemonTypePresentationModel(
        type = PokemonTypeEnum.PSYCHIC,
        name = TextUiModel(R.string.pokemon_type_name_psychic)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().physic
    }

    object Ice : PokemonTypePresentationModel(
        type = PokemonTypeEnum.ICE,
        name = TextUiModel(R.string.pokemon_type_name_ice)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().ice
    }

    object Dragon : PokemonTypePresentationModel(
        type = PokemonTypeEnum.DRAGON,
        name = TextUiModel(R.string.pokemon_type_name_dragon)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().dragon
    }

    object Dark : PokemonTypePresentationModel(
        type = PokemonTypeEnum.DARK,
        name = TextUiModel(R.string.pokemon_type_name_dark)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().dark
    }

    object Fairy : PokemonTypePresentationModel(
        type = PokemonTypeEnum.FAIRY,
        name = TextUiModel(R.string.pokemon_type_name_fairy)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().fairy
    }

    object Stellar : PokemonTypePresentationModel(
        type = PokemonTypeEnum.STELLAR,
        name = TextUiModel(R.string.pokemon_type_name_stellar)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.pokemonTypeColor().stellar
    }

    object Unknown : PokemonTypePresentationModel(
        type = PokemonTypeEnum.UNKNOWN,
        name = TextUiModel(R.string.pokemon_type_name_unknown)
    ) {
        @get:Composable
        override val color: Color get() = AppTheme.grayscaleColor().medium
    }
}