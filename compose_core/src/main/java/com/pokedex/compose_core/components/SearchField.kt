package com.pokedex.compose_core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokedex.compose_core.R
import com.pokedex.compose_core.common.dropShadow2dp
import com.pokedex.compose_core.common.innerShadow
import com.pokedex.compose_core.theme.AppTheme

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (newText: String) -> Unit,
    onClearText: () -> Unit
) {
    val hasFocus = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .height(32.dp)
            .then(
                if (hasFocus.value) Modifier.dropShadow2dp(
                    shape = RoundedCornerShape(16.dp),
                ) else Modifier.innerShadow(
                    shape = RoundedCornerShape(16.dp),
                )
            )
            .background(
                color = AppTheme.grayscaleColor().white,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        BasicTextField(
            modifier = Modifier
                .onFocusChanged {
                    hasFocus.value = it.isFocused
                }
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            value = text,
            onValueChange = onTextChanged,
            textStyle = AppTheme.bodyTypography().body3.copy(
                color = AppTheme.grayscaleColor().dark
            ),
            cursorBrush = SolidColor(
                value = AppTheme.grayscaleColor().dark
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "Icon search",
                        tint = AppTheme.identityColor().primary
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                    ) {
                        if (hasFocus.value.not() && text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_field_placeholder),
                                style = AppTheme.bodyTypography().body3,
                                color = AppTheme.grayscaleColor().medium
                            ) }
                        else innerTextField()
                    }

                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    if (text.isNotEmpty()) {
                        Icon(
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    onClearText()
                                    focusManager.clearFocus()
                                },
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Icon delete",
                            tint = AppTheme.identityColor().primary
                        )
                    }
                }
            }
        )
    }
}

@Composable
@Preview(
    backgroundColor = 0xFFFFFFFF,
    showBackground = true
)
private fun SearchFieldPreview() {
    AppTheme {
        SearchField(
            modifier = Modifier.padding(
                16.dp
            ),
            text = "",
            onTextChanged = {},
            onClearText = {}
        )
    }
}