package com.pokedex.pokemon_list_feature.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pokedex.api_pokemon.domain.usecase.GetFirstPokemonPageUseCase
import com.pokedex.api_pokemon.domain.usecase.GetNextPokemonPageUseCase
import com.pokedex.core.domain.model.ItemWithUrl

internal class PokemonPagingSource(
    private val getFirstPokemonPageUseCase: GetFirstPokemonPageUseCase,
    private val getNextPokemonPageUseCase: GetNextPokemonPageUseCase,
) : PagingSource<String, ItemWithUrl>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, ItemWithUrl> {
        val result = params.key?.let {
            getNextPokemonPageUseCase.execute(it)
        } ?: getFirstPokemonPageUseCase.execute()

        result.fold(
            onSuccess = { result ->
                return LoadResult.Page(
                    data = result.results,
                    nextKey = result.next,
                    prevKey = params.key,
                )
            },
            onFailure = {
                return LoadResult.Error(it)
            }
        )
    }

    override fun getRefreshKey(state: PagingState<String, ItemWithUrl>): String? = null
}