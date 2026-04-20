package com.example.kotlinapp.ui.icons

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.kotlinapp.ui.theme.DefaultIconTint

private fun drawEyePath(
    center: Offset,
    eyeWidth: Float,
    eyeHeight: Float
): Path = Path().apply {
    moveTo(center.x - eyeWidth / 2f, center.y)
    cubicTo(
        center.x - eyeWidth / 4f, center.y - eyeHeight,
        center.x + eyeWidth / 4f, center.y - eyeHeight,
        center.x + eyeWidth / 2f, center.y
    )
    cubicTo(
        center.x + eyeWidth / 4f, center.y + eyeHeight,
        center.x - eyeWidth / 4f, center.y + eyeHeight,
        center.x - eyeWidth / 2f, center.y
    )
    close()
}

@Composable
fun VisibilityIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val iconColor = if (tint == Color.Unspecified) DefaultIconTint else tint

    Canvas(modifier = modifier) {
        val eyeWidth = size.width * 0.75f
        val eyeHeight = size.height * 0.45f
        val center = Offset(size.width / 2f, size.height / 2f)

        val eyePath = drawEyePath(center, eyeWidth, eyeHeight)

        drawPath(
            path = eyePath,
            color = iconColor,
            style = Stroke(width = size.width * 0.065f)
        )

        drawCircle(
            color = iconColor,
            radius = size.width * 0.11f,
            center = center
        )
    }
}

@Composable
fun VisibilityOffIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val iconColor = if (tint == Color.Unspecified) DefaultIconTint else tint

    Canvas(modifier = modifier) {
        val eyeWidth = size.width * 0.75f
        val eyeHeight = size.height * 0.45f
        val center = Offset(size.width / 2f, size.height / 2f)

        val eyePath = drawEyePath(center, eyeWidth, eyeHeight)

        drawPath(
            path = eyePath,
            color = iconColor,
            style = Stroke(width = size.width * 0.065f)
        )

        drawCircle(
            color = iconColor,
            radius = size.width * 0.11f,
            center = center
        )

        val slashPadding = size.width * 0.1f
        drawLine(
            color = iconColor,
            start = Offset(slashPadding, size.height - slashPadding),
            end = Offset(size.width - slashPadding, slashPadding),
            strokeWidth = size.width * 0.06f,
            cap = StrokeCap.Round
        )
    }
}