package com.pokedex.core.domain.model

data class Pagination(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ItemWithUrl>
)

data class ItemWithUrl(
    val name: String?,
    val url: String
)
