package com.pokedex.pokedex_welcome.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pokedex.compose_core.components.AppHeader
import com.pokedex.compose_core.di.daggerViewModel
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.pokedex_welcome.R
import com.pokedex.pokedex_welcome.composables.PokedexWelcomeLoadContent
import com.pokedex.pokedex_welcome.composables.PokedexWelcomeLoadingEndedContent
import com.pokedex.pokedex_welcome.composables.PokedexWelcomeStartContent
import com.pokedex.pokedex_welcome.state.PokedexWelcomeUiState
import com.pokedex.pokedex_welcome.viewModel.PokedexWelcomeScreenIntent
import com.pokedex.pokedex_welcome.viewModel.PokedexWelcomeViewModel

@Composable
internal fun PokedexWelcomeScreen(
    viewModel: PokedexWelcomeViewModel = daggerViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PokedexWelcomeScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent
    )
}

@Composable
private fun PokedexWelcomeScreenContent(
    uiState: PokedexWelcomeUiState,
    handleIntent: (intent: PokedexWelcomeScreenIntent) -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.statusBars
                )
            )
        },
        containerColor = AppTheme.grayscaleColor().white
    ) { contentPaddings ->
        Box(
            modifier = Modifier
                .padding(contentPaddings)
                .padding(
                    horizontal = 24.dp
                ),
        ) {
            when (uiState) {
                is PokedexWelcomeUiState.Welcome -> {
                    PokedexWelcomeStartContent(
                        onStartLoadClick = {
                            handleIntent(PokedexWelcomeScreenIntent.MoveToDownloadDatabaseStep)
                        }
                    )
                }

                is PokedexWelcomeUiState.LoadingDatabase -> {
                    PokedexWelcomeLoadContent(
                        percentText = uiState.progressText,
                        progress = uiState.progress
                    )
                }

                is PokedexWelcomeUiState.DatabaseLoadedSuccessfully -> {
                    PokedexWelcomeLoadingEndedContent(
                        onStarClick = {
                            handleIntent(PokedexWelcomeScreenIntent.MoveToFinishDownloadingStep)
                        }
                    )
                }
            }
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true)
@Composable
private fun PreviewWelcomeState() {
    AppTheme {
        PokedexWelcomeScreenContent(
            uiState = PokedexWelcomeUiState.Welcome,
            handleIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoadingState() {
    AppTheme {
        PokedexWelcomeScreenContent(
            uiState = PokedexWelcomeUiState.LoadingDatabase(
                progress = 0.5f,
                progressText = "50%"
            ),
            handleIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSuccessState() {
    AppTheme {
        PokedexWelcomeScreenContent(
            uiState = PokedexWelcomeUiState.DatabaseLoadedSuccessfully,
            handleIntent = {}
        )
    }
}