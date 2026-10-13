package com.pokedex.api_pokemon.domain.usecase

import com.pokedex.api_pokemon.domain.PokemonRepository
import com.pokedex.core.domain.model.PokemonId
import javax.inject.Inject

class GetPokemonUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend fun execute(id: PokemonId) = repository.getPokemon(id)
}
