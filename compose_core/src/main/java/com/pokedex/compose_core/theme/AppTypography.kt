package com.pokedex.compose_core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.pokedex.compose_core.R

internal val fontBold = Font(R.font.poppins_bold)

internal val fontRegular = Font(R.font.poppins_regular)


@Stable
internal fun appTypography(
    header: Header = Header(),
    body: Body = Body(),
    button: Button = Button()
) = AppTypography(
    header = header,
    body = body,
    button = button
)

@Immutable
class AppTypography(
    val header: Header = Header(),
    val body: Body = Body(),
    val button: Button = Button()
)

@Immutable
class Header(
    val header: TextStyle = TextStyle(
        fontFamily = FontFamily(fontBold),
        lineHeight = 44.sp,
        fontSize = 36.sp,
        letterSpacing = 0.sp
    ),
    val headline: TextStyle = TextStyle(
        fontFamily = FontFamily(fontBold),
        lineHeight = 32.sp,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    val subtitle1: TextStyle = TextStyle(
        fontFamily = FontFamily(fontBold),
        lineHeight = 16.sp,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),
    val subtitle2: TextStyle = TextStyle(
        fontFamily = FontFamily(fontBold),
        lineHeight = 16.sp,
        fontSize = 12.sp,
        letterSpacing = 0.sp
    ),
    val subtitle3: TextStyle = TextStyle(
        fontFamily = FontFamily(fontBold),
        lineHeight = 16.sp,
        fontSize = 10.sp,
        letterSpacing = 0.sp
    ),
)

@Immutable
class Body(
    val body1: TextStyle = TextStyle(
        fontFamily = FontFamily(fontRegular),
        lineHeight = 16.sp,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),
    val body2: TextStyle = TextStyle(
        fontFamily = FontFamily(fontRegular),
        lineHeight = 16.sp,
        fontSize = 12.sp,
        letterSpacing = 0.sp
    ),
    val body3: TextStyle = TextStyle(
        fontFamily = FontFamily(fontRegular),
        lineHeight = 16.sp,
        fontSize = 10.sp,
        letterSpacing = 0.sp
    ),
    val caption: TextStyle = TextStyle(
        fontFamily = FontFamily(fontRegular),
        lineHeight = 12.sp,
        fontSize = 8.sp,
        letterSpacing = 0.sp
    ),
)

@Immutable
class Button(
    val button1: TextStyle = TextStyle(
        fontFamily = FontFamily(fontBold),
        lineHeight = 24.sp,
        fontSize = 16.sp,
        letterSpacing = 0.sp
    ),
)