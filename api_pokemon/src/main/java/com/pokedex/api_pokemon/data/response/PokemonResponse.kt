package com.pokedex.api_pokemon.data.response

import com.pokedex.core.network.models.ItemWithUrlResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PokemonResponse(
    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("base_experience")
    val baseExperience: Int?,

    @SerialName("height")
    val height: Int,

    @SerialName("is_default")
    val isDefault: Boolean,

    @SerialName("abilities")
    val abilities: List<AbilityEntry>,

    @SerialName("cries")
    val cries: CryResponse,

    @SerialName("forms")
    val forms: List<ItemWithUrlResponse>,

    @SerialName("location_area_encounters")
    val locationAreaEncounters: String,

    @SerialName("moves")
    val moves: List<MoveEntryResponse>
)

@Serializable
class AbilityEntry(
    @SerialName("ability")
    val ability: ItemWithUrlResponse,

    @SerialName("is_hidden")
    val isHidden: Boolean,

    @SerialName("slot")
    val slot: Int
)


@Serializable
class CryResponse(
    @SerialName("latest")
    val latest: String,

    @SerialName("legacy")
    val legacy: String
)

@Serializable
class MoveEntryResponse(
    @SerialName("move")
    val move: ItemWithUrlResponse
)

