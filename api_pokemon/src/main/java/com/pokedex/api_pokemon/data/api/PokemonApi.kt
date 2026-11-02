package com.pokedex.api_pokemon.data.api

import com.pokedex.api_pokemon.data.response.PokemonResponse
import com.pokedex.core.domain.model.PokemonId
import com.pokedex.core.network.models.PaginationResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface PokemonApi {

    /**
     * [url] is either a relative path (`pokemon` for the first page) or an absolute `next` url.
     */
    @GET
    suspend fun getPokemonPage(@Url url: String): Response<PaginationResponse>

    @GET("pokemon/{id}")
    suspend fun getSinglePokemon(@Path("id") id: PokemonId): Response<PokemonResponse>

    @GET("pokemon/{id}")
    suspend fun getSinglePokemonRaw(@Path("id") id: PokemonId): Response<ResponseBody>

    @GET
    suspend fun getSpeciesByUrl(@Url url: String): Response<ResponseBody>
}