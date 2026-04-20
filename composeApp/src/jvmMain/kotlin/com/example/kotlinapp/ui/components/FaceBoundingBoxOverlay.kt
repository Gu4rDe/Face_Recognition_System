package com.example.kotlinapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinapp.domain.model.FaceResult

private data class BestMatchInfo(
    val similarity: Double?,
    val label: String?
)

private fun bestMatchInfo(faceResult: FaceResult): BestMatchInfo {
    val best = faceResult.matches.maxByOrNull { it.similarity }
    return BestMatchInfo(
        similarity = best?.similarity,
        label = best?.let { "${it.username} (${(it.similarity * 100).toInt()}%)" }
    )
}

private fun bboxColor(faceResult: FaceResult, threshold: Float): Color {
    val info = bestMatchInfo(faceResult)
    if (info.similarity == null) return Color.Gray
    val sim = info.similarity
    return when {
        sim > threshold -> Color(0xFF4CAF50)
        sim >= threshold * 0.75 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
}

private data class FitTransform(
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float
)

private fun computeFitTransform(
    imageWidth: Int,
    imageHeight: Int,
    canvasWidth: Float,
    canvasHeight: Float
): FitTransform {
    val scaleX = canvasWidth / imageWidth
    val scaleY = canvasHeight / imageHeight
    val scale = minOf(scaleX, scaleY)
    val offsetX = (canvasWidth - imageWidth * scale) / 2f
    val offsetY = (canvasHeight - imageHeight * scale) / 2f
    return FitTransform(scale, offsetX, offsetY)
}

@Composable
fun FaceBoundingBoxOverlay(
    imageBitmap: ImageBitmap,
    faceResults: List<FaceResult>,
    modifier: Modifier = Modifier,
    matchThreshold: Float = 0.8f
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(fontSize = 12.sp, color = Color.White)

    Box(modifier = modifier) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Предпросмотр",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val imageWidth = imageBitmap.width
            val imageHeight = imageBitmap.height
            if (imageWidth <= 0 || imageHeight <= 0) return@Canvas

            val transform = computeFitTransform(
                imageWidth, imageHeight,
                size.width, size.height
            )

            for (face in faceResults) {
                val color = bboxColor(face, matchThreshold)
                val info = bestMatchInfo(face)
                val rectLeft = face.bbox.x * transform.scale + transform.offsetX
                val rectTop = face.bbox.y * transform.scale + transform.offsetY
                val rectWidth = face.bbox.width * transform.scale
                val rectHeight = face.bbox.height * transform.scale

                drawRect(
                    color = color,
                    topLeft = Offset(rectLeft, rectTop),
                    size = Size(rectWidth, rectHeight),
                    style = Stroke(width = 3f)
                )

                info.label?.let { label ->
                    val measured = textMeasurer.measure(
                        text = AnnotatedString(label),
                        style = textStyle
                    )
                    val labelX = rectLeft
                    val labelY = rectTop - measured.size.height - 2f

                    drawRect(
                        color = color,
                        topLeft = Offset(labelX, labelY),
                        size = Size(measured.size.width.toFloat() + 8f, measured.size.height.toFloat() + 4f)
                    )
                    drawText(
                        textLayoutResult = measured,
                        topLeft = Offset(labelX + 4f, labelY + 2f),
                        color = Color.White
                    )
                }
            }
        }
    }
}