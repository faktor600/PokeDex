package com.pokedex.api_pokemon.data.response

import com.pokedex.core.network.models.ItemWithUrlResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonSpeciesResponse(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("order")
    val order: Int,

    @SerialName("gender_rate")
    val genderRate: Int,

    @SerialName("capture_rate")
    val captureRate: Int,

    @SerialName("base_happiness")
    val baseHappiness: Int,

    @SerialName("is_baby")
    val isBaby: Boolean,

    @SerialName("is_legendary")
    val isLegendary: Boolean,

    @SerialName("is_mythical")
    val isMythical: Boolean,

    @SerialName("hatch_counter")
    val hatchCounter: Int,

    @SerialName("has_gender_differences")
    val hasGenderDifferences: Boolean,

    @SerialName("forms_switchable")
    val formsSwitchable: Boolean,

    @SerialName("growth_rate")
    val growthRate: ItemWithUrlResponse,

    @SerialName("pokedex_numbers")
    val pokedexNumbers: List<PokedexNumberEntry>,

    @SerialName("egg_groups")
    val eggGroups: List<ItemWithUrlResponse>,

    @SerialName("color")
    val color: ItemWithUrlResponse,

    @SerialName("shape")
    val shape: ItemWithUrlResponse,

    @SerialName("evolves_from_species")
    val evolvesFromSpecies: ItemWithUrlResponse?,

    @SerialName("evolution_chain")
    val evolutionChain: ItemWithUrlResponse,

    @SerialName("habitat")
    val habitat: ItemWithUrlResponse?,

    @SerialName("generation")
    val generation: ItemWithUrlResponse,

    @SerialName("names")
    val names: List<PokemonNameEntry>,

    @SerialName("pal_park_encounters")
    val palParkEncounters: List<PalParkEncounterEntry>,

    @SerialName("form_descriptions")
    val formDescriptions: List<FlavorTextEntry>,

    @SerialName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>,
/*
    @SerialName("genera")
    val genera: List<ItemWithUrlResponse>,*/

    @SerialName("varieties")
    val varieties: List<PokemonVarietyEntry>
)

@Serializable
data class PokedexNumberEntry(
    @SerialName("entry_number")
    val entryNumber: Int,

    @SerialName("pokedex")
    val pokedex: ItemWithUrlResponse
)

@Serializable
data class PokemonNameEntry(
    @SerialName("name")
    val name: String,

    @SerialName("language")
    val language: ItemWithUrlResponse
)

@Serializable
data class PalParkEncounterEntry(
    @SerialName("base_score")
    val baseScore: Int,

    @SerialName("rate")
    val rate: Int,

    @SerialName("area")
    val area: ItemWithUrlResponse
)

@Serializable
data class FlavorTextEntry(
    @SerialName("flavor_text")
    val flavorText: String,

    @SerialName("language")
    val language: ItemWithUrlResponse,

    @SerialName("version")
    val version: ItemWithUrlResponse
)

@Serializable
data class PokemonVarietyEntry(
    @SerialName("is_default")
    val isDefault: Boolean,

    @SerialName("pokemon")
    val pokemon: ItemWithUrlResponse
)
