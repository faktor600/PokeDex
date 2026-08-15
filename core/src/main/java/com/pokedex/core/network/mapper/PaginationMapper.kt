package com.pokedex.core.network.mapper

import com.pokedex.core.domain.model.ItemWithUrl
import com.pokedex.core.domain.model.Pagination
import com.pokedex.core.network.models.ItemWithUrlResponse
import com.pokedex.core.network.models.PaginationResponse

fun PaginationResponse.toDomain(): Pagination {
    return Pagination(
        count = count,
        next = next,
        previous = previous,
        results = results.map { it.toDomain() }
    )
}

fun ItemWithUrlResponse.toDomain(): ItemWithUrl {
    return ItemWithUrl(
        name = name, url = url
    )
}
