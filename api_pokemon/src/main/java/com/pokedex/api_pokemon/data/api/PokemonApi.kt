package com.pokedex.api_pokemon.data.api

import com.pokedex.api_pokemon.data.response.PokemonResponse
import com.pokedex.core.network.models.PaginationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface PokemonApi {

    @GET("pokemon")
    suspend fun getPokemonPaginationStart(): Response<PaginationResponse>

    @GET
    suspend fun getNextPokemonPagination(@Url url: String): Response<PaginationResponse>

    @GET
    suspend fun getSinglePokemon(@Url url: String): Response<PokemonResponse>
}