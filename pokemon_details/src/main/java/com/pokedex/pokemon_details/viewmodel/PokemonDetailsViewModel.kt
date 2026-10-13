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
                currentPokemonById = route.id
            )
        }
        viewModelScope.launch {
            val pokemon = getPokemonUseCase.execute(route.id)

            pokemon.getOrNull()?.let { pokemon ->
                val pokemonSpecies = getPokemonSpecies.execute(pokemon.speciesLink).getOrThrow()
                val pokemonDetails = getPokemonDetails(
                    pokemon = pokemon,
                    pokemonSpecies = pokemonSpecies
                )

                _uiState.update {
                    it.copy(
                        currentPokemonById = route.id,
                        pokemonMap = mutableMapOf<PokemonId, PokemonDetails>().apply {
                            putAll(
                                route.ids.associateWith {
                                if (it == route.id) pokemonDetails else PokemonDetails.Loading
                            }
                            )
                        },
                        firstId = route.ids.first(),
                        lastId = route.ids.last()
                    )
                }
            }
        }
    }

    fun onPrevClick() {
        val collection = _uiState.value.pokemonMap.asIterable().withIndex()
        val old = collection.firstOrNull { it.value.key == _uiState.value.currentPokemonById }

        if (old?.index != null) {
            val prevIndex = (old.index - 1).coerceIn(0, _uiState.value.pokemonMap.size - 1)

            if (prevIndex == old.index) return

            val prev = collection.firstOrNull { it.index == prevIndex } ?: return
            _uiState.update {
                it.copy(
                    currentPokemonById = prev.value.key
                )
            }
            if (prev.value.value == PokemonDetails.Loading) {
                viewModelScope.launch {
                    val pokemon = getPokemonUseCase.execute(prev.value.key)

                    pokemon.getOrNull()?.let { pokemon ->
                        val pokemonSpecies = try {
                            getPokemonSpecies.execute(pokemon.speciesLink).getOrThrow()
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e

                            _uiState.update {
                                it.copy(
                                    currentPokemonById = prev.value.key
                                )
                            }

                            return@launch
                        }
                        val pokemonDetails = getPokemonDetails(
                            pokemon = pokemon,
                            pokemonSpecies = pokemonSpecies
                        )

                        _uiState.update {
                            it.copy(
                                currentPokemonById = prev.value.key,
                                pokemonMap = it.pokemonMap.toMutableMap().apply {
                                    put(prev.value.key, pokemonDetails)
                                }
                            )
                        }
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        currentPokemonById = prev.value.key
                    )
                }
            }
        }
    }

    fun onNextClick() {
        val collection = _uiState.value.pokemonMap.asIterable().withIndex()
        val old = collection.firstOrNull { it.value.key == _uiState.value.currentPokemonById }

        if (old?.index != null) {
            val nextIndex = old.index + 1

            val next = collection.firstOrNull { it.index == nextIndex } ?: return

            _uiState.update {
                it.copy(
                    currentPokemonById = next.value.key
                )
            }

            if (next.value.value == PokemonDetails.Loading) {
                viewModelScope.launch {
                    val pokemon = getPokemonUseCase.execute(next.value.key)

                    pokemon.getOrNull()?.let { pokemon ->
                        val pokemonSpecies = try {
                            getPokemonSpecies.execute(pokemon.speciesLink).getOrThrow()
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e

                            _uiState.update {
                                it.copy(
                                    currentPokemonById = next.value.key
                                )
                            }

                            return@launch
                        }
                        val pokemonDetails = getPokemonDetails(
                            pokemon = pokemon,
                            pokemonSpecies = pokemonSpecies
                        )

                        _uiState.update {
                            it.copy(
                                currentPokemonById = next.value.key,
                                pokemonMap = it.pokemonMap.toMutableMap().apply {
                                    put(next.value.key, pokemonDetails)
                                }
                            )
                        }
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        currentPokemonById = next.value.key
                    )
                }
            }
        }
    }

    private fun getPokemonDetails(pokemon: Pokemon, pokemonSpecies: PokemonSpecies) = PokemonDetails.Loaded(
        pokemonName = pokemon.name.getPokemonNameForPresentation(),
        pokemonNumber = pokemon.id.getPokemonNumberForPresentation(),
        pokemonImageUrl = pokemon.officialArtwork ?: "",
        pokemonTypeMain = pokemon.types.first().typeEnum.toPresentationModel(),
        pokemonTypes = pokemon.types.map {
            it.typeEnum.toPresentationModel()
        }.toSet(),
        weight = "${decimalFormat.format(pokemon.weight / 10f)} kg",
        height = "${decimalFormat.format(pokemon.height / 10f)} m",
        description = pokemonSpecies.flavorTextEntries.filter { it.language == "en" }.random().flavorText.replace("\n", " "),
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