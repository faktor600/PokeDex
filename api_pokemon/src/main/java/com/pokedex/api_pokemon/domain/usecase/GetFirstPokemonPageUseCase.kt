package com.pokedex.api_pokemon.domain.usecase

import com.pokedex.api_pokemon.domain.PokemonRepository
import com.pokedex.core.domain.model.Pagination
import javax.inject.Inject

class GetFirstPokemonPageUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend fun execute(): Result<Pagination> = repository.getFirstPokemonPage()
}
