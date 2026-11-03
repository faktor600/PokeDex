package com.pokedex.api_pokemon.domain.usecase

import com.pokedex.api_pokemon.domain.PokemonRepository
import com.pokedex.core.domain.model.PokemonId
import javax.inject.Inject

/** Asks the repository to download details of the given (e.g. currently visible) pokemons. */
class PrefetchPokemonsUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend fun execute(ids: List<PokemonId>) = repository.prefetchPokemons(ids)
}
