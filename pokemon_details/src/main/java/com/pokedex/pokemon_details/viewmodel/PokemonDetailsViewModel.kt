package com.pokedex.pokemon_details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokedex.api_pokemon.domain.model.Pokemon
import com.pokedex.api_pokemon.domain.model.PokemonSpecies
import com.pokedex.api_pokemon.domain.usecase.GetPokemonSpeciesByUrlUseCase
import com.pokedex.api_pokemon.domain.usecase.GetPokemonUseCase
import com.pokedex.compose_core.common.toPresentationModel
import com.pokedex.core.domain.model.PokemonId
import com.pokedex.core.utils.getPokemonNameForPresentation
import com.pokedex.core.utils.getPokemonNumberForPresentation
import com.pokedex.pokemon_details.route.PokemonDetailsRoute
import com.pokedex.pokemon_details.state.PokemonDetails
import com.pokedex.pokemon_details.state.PokemonDetailsUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Locale
import javax.inject.Inject

class PokemonDetailsViewModel @Inject constructor(
    private val getPokemonUseCase: GetPokemonUseCase,
    private val getPokemonSpecies: GetPokemonSpeciesByUrlUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<PokemonDetailsUiState>(PokemonDetailsUiState())
    internal val uiState: StateFlow<PokemonDetailsUiState>
        get() = _uiState

    private val decimalFormat = DecimalFormat("#.#")

    fun initialize(route: PokemonDetailsRoute) {
        _uiState.update {
            it.copy(
                currentPokemonById = route.id,
                pokemonMap = route.ids.associateWith { id ->
                    it.pokemonMap[id] ?: PokemonDetails.Loading
                },
                firstId = route.ids.first(),
                lastId = route.ids.last()
            )
        }
        viewModelScope.launch {
            loadDetails(route.id)
        }
    }

    fun onPrevClick() = navigate(-1)

    fun onNextClick() = navigate(1)

    private fun navigate(offset: Int) {
        val state = _uiState.value
        val ids = state.pokemonMap.keys.toList()
        val currentIndex = ids.indexOf(state.currentPokemonById)
        if (currentIndex < 0) return

        val targetId = ids.getOrNull(currentIndex + offset) ?: return

        _uiState.update { it.copy(currentPokemonById = targetId) }

        if (state.pokemonMap[targetId] == PokemonDetails.Loading) {
            viewModelScope.launch { loadDetails(targetId) }
        }
    }

    /**
     * Загружает покемона в два этапа: сначала публикует всё, что есть в ответе pokemon
     * (тип, имя, картинка, статы), чтобы UI сразу получил правильный цвет,
     * затем дополняет описание из species.
     */
    private suspend fun loadDetails(id: PokemonId) {
        val pokemon = getPokemonUseCase.execute(id).getOrNull() ?: return
        val partial = getPokemonDetails(pokemon = pokemon, description = "")
        putDetails(id, partial)

        val species = try {
            getPokemonSpecies.execute(pokemon.speciesLink).getOrThrow()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return
        }

        putDetails(id, partial.copy(description = species.getDescription()))
    }

    private fun putDetails(id: PokemonId, details: PokemonDetails.Loaded) {
        // `+` на LinkedHashMap сохраняет порядок ключей, на нём строится навигация по индексу
        _uiState.update { it.copy(pokemonMap = it.pokemonMap + (id to details)) }
    }

    private fun PokemonSpecies.getDescription() = flavorTextEntries
        .filter { it.language == "en" }
        .random()
        .flavorText
        .replace("\n", " ")

    private fun getPokemonDetails(pokemon: Pokemon, description: String) = PokemonDetails.Loaded(
        pokemonName = pokemon.name.getPokemonNameForPresentation(),
        pokemonNumber = pokemon.id.getPokemonNumberForPresentation(),
        pokemonImageUrl = pokemon.officialArtwork ?: "",
        pokemonTypeMain = pokemon.types.first().typeEnum.toPresentationModel(),
        pokemonTypes = pokemon.types.map {
            it.typeEnum.toPresentationModel()
        }.toSet(),
        weight = "${decimalFormat.format(pokemon.weight / 10f)} kg",
        height = "${decimalFormat.format(pokemon.height / 10f)} m",
        description = description,
        moves = pokemon.abilities.map {
            it.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()) else it.toString() }
                .replace('-', ' ')
        },
        statHp = pokemon.stats.first { it.stat.name == "hp" }.baseStat,
        statAtk = pokemon.stats.first { it.stat.name == "attack" }.baseStat,
        statDef = pokemon.stats.first { it.stat.name == "defense" }.baseStat,
        statSAtk = pokemon.stats.first { it.stat.name == "special-attack" }.baseStat,
        statSDef = pokemon.stats.first { it.stat.name == "special-defense" }.baseStat,
        statSpd = pokemon.stats.first { it.stat.name == "speed" }.baseStat
    )
}
