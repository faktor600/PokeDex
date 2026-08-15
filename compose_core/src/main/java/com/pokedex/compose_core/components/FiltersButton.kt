package com.pokedex.compose_core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokedex.compose_core.R
import com.pokedex.compose_core.common.dropShadow2dp
import com.pokedex.compose_core.common.innerShadow
import com.pokedex.compose_core.theme.AppTheme

@Composable
fun FiltersButton(
    clicked: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .then(
                if (clicked) Modifier.dropShadow2dp(
                    shape = CircleShape
                ) else Modifier.innerShadow(
                    shape = CircleShape
                )
            )
            .background(
                color = AppTheme.grayscaleColor().white,
                shape = CircleShape
            ).clickable(
                indication = null,
                interactionSource = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(R.drawable.ic_sort),
            contentDescription = null,
            tint = AppTheme.identityColor().primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FiltersButtonPreview() {
    AppTheme {
        FiltersButton(
            clicked = false,
            onClick = {}
        )
    }
}