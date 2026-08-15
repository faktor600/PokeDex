package com.pokedex.compose_core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.pokedex.compose_core.common.PokemonTypePresentationModel
import com.pokedex.compose_core.common.string
import com.pokedex.compose_core.theme.AppTheme

@Composable
fun PokemonTypeChip(
    pokemonType: PokemonTypePresentationModel
) {
    Box(
        modifier = Modifier.background(
            color = pokemonType.color,
            shape = RoundedCornerShape(10.dp)
        )
    ) {
        Text(
            modifier = Modifier.padding(
                vertical = 2.dp,
                horizontal = 8.dp
            ),
            text = pokemonType.name.string(),
            color = AppTheme.grayscaleColor().white,
            style = AppTheme.headerTypography().subtitle3
        )
    }
}

private class PokemonTypePreviewProvider : PreviewParameterProvider<PokemonTypePresentationModel> {
    private val valuesSequence = sequenceOf(
        PokemonTypePresentationModel.Normal,
        PokemonTypePresentationModel.Fighting,
        PokemonTypePresentationModel.Flying,
        PokemonTypePresentationModel.Poison,
        PokemonTypePresentationModel.Ground,
        PokemonTypePresentationModel.Rock,
        PokemonTypePresentationModel.Bug,
        PokemonTypePresentationModel.Ghost,
        PokemonTypePresentationModel.Steel,
        PokemonTypePresentationModel.Fire,
        PokemonTypePresentationModel.Water,
        PokemonTypePresentationModel.Grass,
        PokemonTypePresentationModel.Electric,
        PokemonTypePresentationModel.Psychic,
        PokemonTypePresentationModel.Ice,
        PokemonTypePresentationModel.Dragon,
        PokemonTypePresentationModel.Dark,
        PokemonTypePresentationModel.Fairy,
        PokemonTypePresentationModel.Stellar,
        PokemonTypePresentationModel.Unknown
    )
    override val values: Sequence<PokemonTypePresentationModel>
        get() = valuesSequence
}

@Composable
@Preview
private fun PokemonTypeChipPreview(
    @PreviewParameter(provider = PokemonTypePreviewProvider::class) param: PokemonTypePresentationModel
) {
    AppTheme {
        PokemonTypeChip(
            pokemonType = param
        )
    }
}