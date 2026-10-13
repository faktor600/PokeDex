package com.pokedex.api_pokemon.domain.usecase

import com.pokedex.api_pokemon.domain.PokemonRepository
import javax.inject.Inject

class GetPokemonSpeciesByUrlUseCase @Inject constructor(
    private val repository: PokemonRepository
) {

    suspend fun execute(url: String) = repository.getPokemonSpecies(url)
}