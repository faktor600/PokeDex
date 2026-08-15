package com.pokedex.compose_core.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults.colors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pokedex.compose_core.theme.AppTheme

@Composable
fun SortButton(
    text: String,
    onRadioClick: () -> Unit
) {
    Row(
        modifier = Modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = false,
            colors = colors(
                selectedColor = AppTheme.identityColor().primary,
                unselectedColor = AppTheme.identityColor().primary,
            ),
            onClick = onRadioClick
        )

        Text(
            text = text,
            style = AppTheme.bodyTypography().body3,
            color = AppTheme.grayscaleColor().dark
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun SortButtonPreview() {
    AppTheme {
        SortButton(
            text = "Some button",
            onRadioClick = {}
        )
    }
}