package com.pokedex.splash_screen.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.pokedex.splash_screen.viewModel.SplashScreenViewModel
import com.pokedex.splash_screen.viewModel.SplashScreenSideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.pokedex.compose_core.di.daggerViewModel
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.splash_screen.state.SplashScreenUiState
import com.pokedex.splash_screen.viewModel.SplashScreenScreenIntent

@Composable
fun SplashScreen(
    navigateToPokemonList: () -> Unit,
    navigateToWelcomeScreen: () -> Unit,
    viewModel: SplashScreenViewModel = daggerViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                SplashScreenSideEffect.MoveToPokemonList -> navigateToPokemonList()

                SplashScreenSideEffect.MoveToWelcomeScreen -> navigateToWelcomeScreen()
            }
        }
    }

    SplashScreenContent(
        uiState = uiState,
        moveToNextScreen = {
            viewModel.handleIntent(SplashScreenScreenIntent.MoveToNextScreen)
        }
    )
}

@Composable
private fun SplashScreenContent(
    uiState: SplashScreenUiState,
    moveToNextScreen: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.identityColor().primary
    ) { contentPaddings ->
        if (uiState is SplashScreenUiState.Ready) {
            val composition = rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(
                    com.pokedex.compose_core.R.raw.splash_short
                )
            )
            val progress by animateLottieCompositionAsState(
                composition = composition.value,
                iterations = 1
            )

            LaunchedEffect(progress) {
                if (progress == 1f) {
                    moveToNextScreen()
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(contentPaddings)
            ) {
                LottieAnimation(
                    modifier = Modifier.align(Alignment.Center),
                    composition = composition.value,
                    progress = { progress }
                )
            }
        }
    }
}

@Composable
@Preview
private fun SplashScreenContentPreview() {
    AppTheme {
        SplashScreenContent(
            uiState = SplashScreenUiState.Ready(),
            moveToNextScreen = {}
        )
    }
}
