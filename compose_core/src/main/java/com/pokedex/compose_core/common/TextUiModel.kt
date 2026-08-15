package com.pokedex.compose_core.common

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

@Immutable
sealed interface TextUiModel {

    data class Text(val value: String) : TextUiModel

    data class TextRes(
        @StringRes val value: Int,
    ) : TextUiModel

    data class TextResWithArgs(
        @StringRes val value: Int,
        val formatArgs: List<Any>,
    ) : TextUiModel {

        override fun equals(other: Any?): Boolean = other is TextResWithArgs &&
            other.value == value &&
            other.formatArgs.joinToString() == formatArgs.joinToString()

        override fun hashCode(): Int = value + formatArgs.joinToString().hashCode()
    }

    data class PluralisedText(
        @PluralsRes val value: Int,
        val count: Int,
    ) : TextUiModel
}

fun TextUiModel(value: String): TextUiModel = TextUiModel.Text(value)

fun TextUiModel(
    @StringRes value: Int,
): TextUiModel = TextUiModel.TextRes(value)

fun TextUiModel(
    @StringRes value: Int,
    vararg formatArgs: Any,
): TextUiModel =
    TextUiModel.TextResWithArgs(value, formatArgs.toList())

fun TextUiModel(
    @PluralsRes pluralsRes: Int,
    count: Int,
): TextUiModel = TextUiModel.PluralisedText(pluralsRes, count)

@Composable
@ReadOnlyComposable
fun TextUiModel.string() = when (this) {
    is TextUiModel.Text -> value
    is TextUiModel.TextRes -> stringResource(value)
    is TextUiModel.TextResWithArgs -> stringResource(value, *formatArgs.toTypedArray())
    is TextUiModel.PluralisedText -> pluralStringResource(value, count, count)
}