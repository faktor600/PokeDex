package com.pokedex.api_pokemon.domain.usecase

import com.pokedex.api_pokemon.domain.PokemonRepository
import javax.inject.Inject

class SearchPokemonsUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend fun execute(text: String) = repository.searchForPokemonsByNameOrId(text)
}