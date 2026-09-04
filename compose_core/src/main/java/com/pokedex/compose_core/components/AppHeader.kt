package com.pokedex.compose_core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokedex.compose_core.R
import com.pokedex.compose_core.theme.AppTheme

@Composable
fun AppHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(R.drawable.ic_pokeball_colored),
            contentDescription = null
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = stringResource(R.string.app_name),
            style = AppTheme.headerTypography().header,
            color = AppTheme.grayscaleColor().dark
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = stringResource(R.string.app_header_description),
            style = AppTheme.bodyTypography().body1,
            color = AppTheme.grayscaleColor().medium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
private fun AppHeaderPreview() {
    AppTheme {
        AppHeader()
    }
}