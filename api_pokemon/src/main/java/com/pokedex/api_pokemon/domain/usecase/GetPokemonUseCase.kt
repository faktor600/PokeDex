package com.pokedex.api_pokemon.domain.usecase

import com.pokedex.api_pokemon.domain.PokemonRepository
import com.pokedex.api_pokemon.domain.model.Pokemon
import javax.inject.Inject

class GetPokemonUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend fun execute(url: String): Result<Pokemon> = repository.getPokemon(url)
}
