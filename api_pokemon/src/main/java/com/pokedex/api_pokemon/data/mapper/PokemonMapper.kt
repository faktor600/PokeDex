package com.pokedex.api_pokemon.data.mapper

import com.pokedex.api_pokemon.data.response.AbilityEntry
import com.pokedex.api_pokemon.data.response.CryResponse
import com.pokedex.api_pokemon.data.response.MoveEntryResponse
import com.pokedex.api_pokemon.data.response.PokemonResponse
import com.pokedex.api_pokemon.domain.model.Ability
import com.pokedex.api_pokemon.domain.model.Cry
import com.pokedex.api_pokemon.domain.model.MoveEntry
import com.pokedex.api_pokemon.domain.model.Pokemon
import com.pokedex.core.network.mapper.toDomain

fun PokemonResponse.toDomain(): Pokemon {
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
        moves = moves.map { it.toDomain() }
    )
}

fun AbilityEntry.toDomain(): Ability {
    return Ability(
        name = ability.name ?: "",
        url = ability.url,
        isHidden = isHidden,
        slot = slot
    )
}

fun CryResponse.toDomain(): Cry {
    return Cry(
        latest = latest,
        legacy = legacy
    )
}

fun MoveEntryResponse.toDomain(): MoveEntry {
    return MoveEntry(
        move = move.toDomain()
    )
}
