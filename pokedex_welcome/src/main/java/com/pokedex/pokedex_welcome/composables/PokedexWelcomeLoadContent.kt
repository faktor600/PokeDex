package com.pokedex.pokedex_welcome.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.pokedex_welcome.R

@Composable
internal fun PokedexWelcomeLoadContent(
    percentText: String,
    progress: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(64.dp)
        )

        Text(
            text = stringResource(R.string.welcome_loading_title),
            style = AppTheme.headerTypography().headline,
            color = AppTheme.grayscaleColor().dark,
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.welcome_loading_description),
            style = AppTheme.bodyTypography().body1,
            color = AppTheme.grayscaleColor().medium,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.welcome_loading_progress_bar_title),
                style = AppTheme.bodyTypography().body1,
                color = AppTheme.grayscaleColor().medium
            )

            Text(
                text = percentText,
                style = AppTheme.bodyTypography().body1,
                color = AppTheme.identityColor().primary
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            progress = { progress },
            gapSize = 0.dp,
            drawStopIndicator = {},
            color = AppTheme.identityColor().primary,
            trackColor = AppTheme.identityColor().secondaryContainer
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PokedexWelcomeLoadContentPreview() {
    AppTheme {
        PokedexWelcomeLoadContent(
            percentText = "64%",
            progress = 0.1f
        )
    }
}