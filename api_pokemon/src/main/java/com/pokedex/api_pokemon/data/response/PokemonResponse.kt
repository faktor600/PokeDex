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
    val moves: List<MoveEntryResponse>,

    @SerialName("sprites")
    val sprites: PokemonSprites
)

@Serializable
data class PokemonSprites(
    @SerialName("back_default")
    val backDefault: String?,

    @SerialName("back_female")
    val backFemale: String?,

    @SerialName("back_shiny")
    val backShiny: String?,

    @SerialName("back_shiny_female")
    val backShinyFemale: String?,

    @SerialName("front_default")
    val frontDefault: String?,

    @SerialName("front_female")
    val frontFemale: String?,

    @SerialName("front_shiny")
    val frontShiny: String?,

    @SerialName("front_shiny_female")
    val frontShinyFemale: String?,

    @SerialName("other")
    val other: PokemonOtherSprites
)

@Serializable
data class PokemonOtherSprites(
    @SerialName("dream_world")
    val dreamWorld: DreamWorldSprites?,

    @SerialName("home")
    val home: HomeSprites?,

    @SerialName("official-artwork")
    val officialArtwork: OfficialArtworkSprites?,

    @SerialName("showdown")
    val showdown: ShowdownSprites?
)

@Serializable
data class DreamWorldSprites(
    @SerialName("front_default")
    val frontDefault: String?,

    @SerialName("front_female")
    val frontFemale: String?
)

@Serializable
data class HomeSprites(
    @SerialName("front_default")
    val frontDefault: String?,

    @SerialName("front_female")
    val frontFemale: String?,

    @SerialName("front_shiny")
    val frontShiny: String?,

    @SerialName("front_shiny_female")
    val frontShinyFemale: String?
)

@Serializable
data class OfficialArtworkSprites(
    @SerialName("front_default")
    val frontDefault: String?,

    @SerialName("front_shiny")
    val frontShiny: String?
)

@Serializable
data class ShowdownSprites(
    @SerialName("back_default")
    val backDefault: String?,

    @SerialName("back_female")
    val backFemale: String?,

    @SerialName("back_shiny")
    val backShiny: String?,

    @SerialName("back_shiny_female")
    val backShinyFemale: String?,

    @SerialName("front_default")
    val frontDefault: String?,

    @SerialName("front_female")
    val frontFemale: String?,

    @SerialName("front_shiny")
    val frontShiny: String?,

    @SerialName("front_shiny_female")
    val frontShinyFemale: String?
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
    val legacy: String?
)

@Serializable
class MoveEntryResponse(
    @SerialName("move")
    val move: ItemWithUrlResponse
)

