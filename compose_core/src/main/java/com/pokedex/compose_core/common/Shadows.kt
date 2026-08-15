package com.pokedex.compose_core.common

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val shadowColor = Color(0x90000000)

fun Modifier.innerShadow(
    shape: Shape
) = this.drawWithContent {
    drawContent() // Drawing the content

    drawIntoCanvas { canvas ->
        val shadowSize = Size(size.width, size.height)
        val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

        // Initialize a Paint object
        val paint = Paint()
        paint.color = shadowColor

        // Save the current layer of the canvas
        canvas.saveLayer(size.toRect(), paint)
        // Draw the outline of the shadow onto the canvas
        canvas.drawOutline(shadowOutline, paint)

        // Configure the paint to act as an eraser to clip the shadow
        paint.asFrameworkPaint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            maskFilter = BlurMaskFilter(4.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
        }

        // Set clipping color
        paint.color = Color.White

        // Translate the canvas to the offset position
        canvas.translate(0f, 2.dp.toPx())
        // Draw the outline again to clip the shadow
        canvas.drawOutline(shadowOutline, paint)
        // Restore the canvas
        canvas.restore()
    }
}

fun Modifier.dropShadow2dp(
    shape: Shape,
)  = this.drawBehind {
    val shadowSize = Size(size.width, size.height)
    val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

    // Create a Paint object
    val paint = Paint()
    // Apply specified color
    paint.color = shadowColor

    paint.asFrameworkPaint().apply {
        // Apply blur to the Paint
        maskFilter = BlurMaskFilter(4.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
    }

    drawIntoCanvas { canvas ->
        // Save the canvas state
        canvas.save()
        // Translate to specified offsets
        canvas.translate(0f, 2.dp.toPx())
        // Draw the shadow
        canvas.drawOutline(shadowOutline, paint)
        // Restore the canvas state
        canvas.restore()
    }
}

fun Modifier.dropShadow6dp(
    shape: Shape,
)  = this.drawBehind {
    val shadowSize = Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx())
    val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

    // Create a Paint object
    val paint = Paint()
    // Apply specified color
    paint.color = shadowColor

    paint.asFrameworkPaint().apply {
        // Apply blur to the Paint
        maskFilter = BlurMaskFilter(12.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
    }

    drawIntoCanvas { canvas ->
        // Save the canvas state
        canvas.save()
        // Translate to specified offsets
        canvas.translate(0f, 4.dp.toPx())
        // Draw the shadow
        canvas.drawOutline(shadowOutline, paint)
        // Restore the canvas state
        canvas.restore()
    }
}