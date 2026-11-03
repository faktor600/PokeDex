package com.pokedex.no_internet.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokedex.compose_core.common.dropShadow6dp
import com.pokedex.compose_core.components.AppHeader
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.no_internet.R
import com.pokedex.compose_core.R as CoreR

@Composable
internal fun NoInternetScreen(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
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
                .fillMaxSize()
                .padding(contentPaddings)
                .padding(horizontal = 24.dp)
        ) {
            NoInternetContent(
                onRetryClick = onRetryClick,
                modifier = Modifier.padding(top = 44.dp)
            )
        }
    }
}

@Composable
private fun NoInternetContent(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = AppTheme.identityColor().secondaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(CoreR.drawable.ic_wifi_off),
                tint = AppTheme.identityColor().primary,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.no_internet_title),
            style = AppTheme.headerTypography().headline,
            color = AppTheme.grayscaleColor().dark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.no_internet_description),
            style = AppTheme.bodyTypography().body1,
            color = AppTheme.grayscaleColor().medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        val buttonShape = RoundedCornerShape(32.dp)
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .dropShadow6dp(buttonShape),
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.identityColor().primary,
                contentColor = AppTheme.grayscaleColor().white
            ),
            onClick = onRetryClick
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(CoreR.drawable.ic_refresh_cw),
                    tint = AppTheme.grayscaleColor().white,
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.no_internet_retry),
                    style = AppTheme.buttonTypography().button1,
                    color = AppTheme.grayscaleColor().white
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoInternetScreenPreview() {
    AppTheme {
        NoInternetScreen(onRetryClick = {})
    }
}
