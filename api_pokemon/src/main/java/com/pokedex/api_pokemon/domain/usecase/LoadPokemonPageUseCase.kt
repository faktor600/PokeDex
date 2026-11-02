package com.pokedex.api_pokemon.domain.usecase

import com.pokedex.api_pokemon.domain.PokemonRepository
import javax.inject.Inject

class LoadPokemonPageUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    /** [url] == null loads the first page. */
    suspend fun execute(url: String? = null) = repository.loadPokemonPageAndSave(url)
}
