package com.pokedex.core.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PaginationResponse(
    @SerialName("count")
    val count: Int,

    @SerialName("next")
    val next: String?,

    @SerialName("previous")
    val previous: String?,

    @SerialName("results")
    val results: List<ItemWithUrlResponse>
)

@Serializable
class ItemWithUrlResponse(
    @SerialName("name")
    val name: String?,

    @SerialName("url")
    val url: String
)