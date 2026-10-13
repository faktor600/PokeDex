package com.pokedex.api_pokemon.data.mapper

import com.pokedex.api_pokemon.data.response.FlavorTextEntry
import com.pokedex.api_pokemon.data.response.PokemonNameEntry
import com.pokedex.api_pokemon.data.response.PokemonSpeciesResponse
import com.pokedex.api_pokemon.data.response.PalParkEncounterEntry
import com.pokedex.api_pokemon.data.response.PokedexNumberEntry
import com.pokedex.api_pokemon.data.response.PokemonVarietyEntry
import com.pokedex.api_pokemon.domain.model.FlavorText
import com.pokedex.api_pokemon.domain.model.PalParkEncounter
import com.pokedex.api_pokemon.domain.model.PokemonName
import com.pokedex.api_pokemon.domain.model.PokemonSpecies
import com.pokedex.api_pokemon.domain.model.PokedexNumber
import com.pokedex.api_pokemon.domain.model.PokemonVariety
import com.pokedex.core.network.mapper.toDomain

fun PokemonSpeciesResponse.toDomain(): PokemonSpecies {
    return PokemonSpecies(
        id = id.toLong(),
        name = name,
        order = order,
        genderRate = genderRate,
        captureRate = captureRate,
        baseHappiness = baseHappiness,
        isBaby = isBaby,
        isLegendary = isLegendary,
        isMythical = isMythical,
        hatchCounter = hatchCounter,
        hasGenderDifferences = hasGenderDifferences,
        formsSwitchable = formsSwitchable,
        growthRate = growthRate.toDomain(),
        pokedexNumbers = pokedexNumbers.map { it.toDomain() },
        eggGroups = eggGroups.map { it.toDomain() },
        color = color.toDomain(),
        shape = shape.toDomain(),
        evolvesFromSpecies = evolvesFromSpecies?.toDomain(),
        evolutionChain = evolutionChain.toDomain(),
        habitat = habitat?.toDomain(),
        generation = generation.toDomain(),
        names = names.map { it.toDomain() },
        palParkEncounters = palParkEncounters.map { it.toDomain() },
        formDescriptions = formDescriptions.map { it.toDomain() },
        flavorTextEntries = flavorTextEntries.map { it.toDomain() },
        varieties = varieties.map { it.toDomain() }
    )
}

private fun PokedexNumberEntry.toDomain() = PokedexNumber(
    entryNumber = entryNumber,
    pokedex = pokedex.toDomain()
)

private fun PokemonNameEntry.toDomain() = PokemonName(
    name = name,
    language = language.name ?: ""
)

private fun PalParkEncounterEntry.toDomain() = PalParkEncounter(
    baseScore = baseScore,
    rate = rate,
    area = area.toDomain()
)

private fun FlavorTextEntry.toDomain() = FlavorText(
    flavorText = flavorText,
    language = language.name ?: "",
    version = version.name ?: ""
)

private fun PokemonVarietyEntry.toDomain() = PokemonVariety(
    isDefault = isDefault,
    pokemon = pokemon.toDomain()
)
