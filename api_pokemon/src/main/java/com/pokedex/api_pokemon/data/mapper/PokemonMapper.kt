package com.pokedex.api_pokemon.data.mapper

import com.pokedex.api_pokemon.data.response.AbilityEntry
import com.pokedex.api_pokemon.data.response.CryResponse
import com.pokedex.api_pokemon.data.response.MoveEntryResponse
import com.pokedex.api_pokemon.data.response.PokemonResponse
import com.pokedex.api_pokemon.data.response.StatEntry
import com.pokedex.api_pokemon.data.response.TypeEntry
import com.pokedex.api_pokemon.domain.model.Ability
import com.pokedex.api_pokemon.domain.model.Cry
import com.pokedex.api_pokemon.domain.model.MoveEntry
import com.pokedex.api_pokemon.domain.model.Pokemon
import com.pokedex.api_pokemon.domain.model.Stat
import com.pokedex.api_pokemon.domain.model.Type
import com.pokedex.core.PokemonTypeEnum
import com.pokedex.core.domain.model.PokemonId
import com.pokedex.core.network.mapper.toDomain

internal fun PokemonResponse.toDomain(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        baseExperience = baseExperience,
        height = height,
        isDefault = isDefault,
        abilities = abilities.map { it.toDomain() },
        cries = cries.toDomain(),
        forms = forms.map { it.name ?: "" },
        locationAreaEncounters = locationAreaEncounters,
        moves = moves.map { it.toDomain() },
        stats = stats.map { it.toDomain() },
        types = types.map { it.toDomain() },
        officialArtwork = sprites.other.officialArtwork?.frontDefault ?: "",
        weight = weight,
        speciesLink = species.url
    )
}

internal fun TypeEntry.toDomain(): Type {
    return Type(
        name = type.name ?: "",
        url = type.url,
        typeEnum = type.name?.let {
            PokemonTypeEnum.valueOf(it.uppercase())
        } ?: PokemonTypeEnum.UNKNOWN
    )
}

internal fun StatEntry.toDomain(): Stat {
    return Stat(
        baseStat = baseStat,
        effort = effort,
        stat = stat.toDomain()
    )
}

internal fun AbilityEntry.toDomain(): Ability {
    return Ability(
        name = ability.name ?: "",
        url = ability.url,
        isHidden = isHidden,
        slot = slot
    )
}

internal fun CryResponse.toDomain(): Cry {
    return Cry(
        latest = latest,
        legacy = legacy ?: ""
    )
}

internal fun MoveEntryResponse.toDomain(): MoveEntry {
    return MoveEntry(
        move = move.toDomain()
    )
}
