package com.pokedex.api_pokemon.domain.usecase

import com.pokedex.api_pokemon.domain.PokemonRepository
import com.pokedex.core.domain.model.Pagination
import javax.inject.Inject

class GetNextPokemonPageUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend fun execute(url: String): Result<Pagination> = repository.getNextPokemonPage(url)
}
