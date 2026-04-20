package com.example.kotlinapp.ui.icons

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.kotlinapp.ui.theme.DefaultIconTint

@Composable
fun DashboardIcon(modifier: Modifier = Modifier, tint: Color = Color.Unspecified) {
    val iconColor = if (tint == Color.Unspecified) DefaultIconTint else tint
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val gap = w * 0.08f
        val cr = CornerRadius(w * 0.06f, w * 0.06f)
        val colW = (w - 3 * gap) / 2f
        val topLeftH = (h - 3 * gap) * 0.55f
        val bottomLeftH = h - 2 * gap - topLeftH
        val rightH = h - gap

        drawRoundRect(iconColor, topLeft = Offset(0f, 0f), size = Size(colW, topLeftH), cornerRadius = cr)
        drawRoundRect(iconColor, topLeft = Offset(colW + gap, 0f), size = Size(colW, rightH), cornerRadius = cr)
        drawRoundRect(iconColor, topLeft = Offset(0f, topLeftH + gap), size = Size(colW, bottomLeftH), cornerRadius = cr)
    }
}

@Composable
fun PeopleIcon(modifier: Modifier = Modifier, tint: Color = Color.Unspecified) {
    val iconColor = if (tint == Color.Unspecified) DefaultIconTint else tint
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val headR = w * 0.13f
        drawCircle(iconColor, radius = headR, center = Offset(w * 0.35f, h * 0.28f))
        drawOval(iconColor, topLeft = Offset(w * 0.1f, h * 0.55f), size = Size(w * 0.5f, h * 0.35f))

        drawCircle(iconColor, radius = w * 0.10f, center = Offset(w * 0.68f, h * 0.22f))
        drawOval(iconColor, topLeft = Offset(w * 0.49f, h * 0.42f), size = Size(w * 0.38f, h * 0.28f))
    }
}

@Composable
fun FaceIcon(modifier: Modifier = Modifier, tint: Color = Color.Unspecified) {
    val iconColor = if (tint == Color.Unspecified) DefaultIconTint else tint
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val r = w * 0.42f

        drawCircle(iconColor, radius = r, center = Offset(cx, cy))
        drawCircle(Color.White, radius = w * 0.06f, center = Offset(cx - r * 0.38f, cy - r * 0.15f))
        drawCircle(Color.White, radius = w * 0.06f, center = Offset(cx + r * 0.38f, cy - r * 0.15f))

        val smilePath = Path().apply {
            val sy = cy + r * 0.15f
            moveTo(cx - r * 0.3f, sy)
            quadraticTo(cx, cy + r * 0.55f, cx + r * 0.3f, sy)
        }
        drawPath(smilePath, color = Color.White, style = Stroke(width = w * 0.04f))
    }
}

@Composable
fun LogoutIcon(modifier: Modifier = Modifier, tint: Color = Color.Unspecified) {
    val iconColor = if (tint == Color.Unspecified) DefaultIconTint else tint
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cr = CornerRadius(w * 0.06f, w * 0.06f)

        drawRoundRect(iconColor, topLeft = Offset(0f, h * 0.1f), size = Size(w * 0.55f, h * 0.8f), cornerRadius = cr)

        val arrowY = h / 2f
        val arrowStartX = w * 0.6f
        val arrowEndX = w * 0.95f
        drawLine(iconColor, Offset(arrowStartX, arrowY), Offset(arrowEndX, arrowY), strokeWidth = w * 0.07f)
        drawPath(Path().apply {
            moveTo(arrowEndX - w * 0.15f, arrowY - h * 0.18f)
            lineTo(arrowEndX, arrowY)
            lineTo(arrowEndX - w * 0.15f, arrowY + h * 0.18f)
            close()
        }, color = iconColor)
    }
}