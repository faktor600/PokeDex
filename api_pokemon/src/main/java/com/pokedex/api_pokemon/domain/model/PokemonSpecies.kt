package com.pokedex.api_pokemon.domain.model

import com.pokedex.core.domain.model.ItemWithUrl

data class PokemonSpecies(
    val id: Long,
    val name: String,
    val order: Int,
    val genderRate: Int,
    val captureRate: Int,
    val baseHappiness: Int,
    val isBaby: Boolean,
    val isLegendary: Boolean,
    val isMythical: Boolean,
    val hatchCounter: Int,
    val hasGenderDifferences: Boolean,
    val formsSwitchable: Boolean,
    val growthRate: ItemWithUrl,
    val pokedexNumbers: List<PokedexNumber>,
    val eggGroups: List<ItemWithUrl>,
    val color: ItemWithUrl,
    val shape: ItemWithUrl,
    val evolvesFromSpecies: ItemWithUrl?,
    val evolutionChain: ItemWithUrl,
    val habitat: ItemWithUrl?,
    val generation: ItemWithUrl,
    val names: List<PokemonName>,
    val palParkEncounters: List<PalParkEncounter>,
    val formDescriptions: List<FlavorText>,
    val flavorTextEntries: List<FlavorText>,
    val varieties: List<PokemonVariety>
)

data class PokedexNumber(
    val entryNumber: Int,
    val pokedex: ItemWithUrl
)

data class PokemonName(
    val name: String,
    val language: String
)

data class PalParkEncounter(
    val baseScore: Int,
    val rate: Int,
    val area: ItemWithUrl
)

data class FlavorText(
    val flavorText: String,
    val language: String,
    val version: String
)

data class PokemonVariety(
    val isDefault: Boolean,
    val pokemon: ItemWithUrl
)
