package com.pokedex.pokedex_welcome.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.pokedex_welcome.R

@Composable
internal fun PokedexWelcomeLoadingEndedContent(
    onStarClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(64.dp)
        )

        Text(
            text = stringResource(R.string.welcome_load_ended_title),
            style = AppTheme.headerTypography().headline,
            color = AppTheme.grayscaleColor().dark,
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.welcome_load_ended_description),
            style = AppTheme.bodyTypography().body1,
            color = AppTheme.grayscaleColor().medium,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonColors(
                containerColor = AppTheme.identityColor().primary,
                contentColor = AppTheme.grayscaleColor().white,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            onClick = onStarClick
        ) {
            Text(
                text = stringResource(R.string.welcome_load_ended_button),
                style = AppTheme.buttonTypography().button1,
                color = AppTheme.grayscaleColor().white,
            )
        }
    }
}

@Composable
@Preview
private fun PokedexWelcomeLoadingEndedContentPreview() {
    AppTheme {
        PokedexWelcomeLoadingEndedContent(
            onStarClick = {}
        )
    }
}