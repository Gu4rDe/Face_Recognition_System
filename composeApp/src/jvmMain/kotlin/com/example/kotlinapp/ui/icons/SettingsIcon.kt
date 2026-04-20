package com.example.kotlinapp.ui.icons

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import com.example.kotlinapp.ui.theme.DefaultIconTint
import kotlin.math.cos
import kotlin.math.sin

/**
 * Иконка шестерёнки для кнопки настроек.
 * Рисуется программно через Canvas — не требует внешних ресурсов.
 *
 * @param modifier модификатор размера и позиции
 * @param tint цвет заливки; если [Color.Unspecified] — используется серый
 */
@Composable
fun SettingsIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val iconColor = if (tint == Color.Unspecified) DefaultIconTint else tint

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outerR = size.minDimension / 2f * 0.85f  /* внешний радиус зубьев */
        val innerR = size.minDimension / 2f * 0.58f  /* внутренний радиус (между зубьями) */

        val numTeeth = 8                     /* количество зубьев шестерёнки */
        val anglePerTooth = 360f / numTeeth  /* угол между центрами зубьев */

        /* Строим контур шестерёнки: чередуем внешние (зубья) и внутренние (впадины) радиусы */
        val gearPath = Path()
        for (i in 0 until numTeeth) {
            val centerAngleDeg = i * anglePerTooth - 90f  /* начинаем сверху */
            val toothHalf = anglePerTooth * 0.28f          /* половина ширины зуба */

            /* Углы начала и конца зуба (внешний контур) */
            val a1 = Math.toRadians((centerAngleDeg - toothHalf).toDouble())
            val a2 = Math.toRadians((centerAngleDeg + toothHalf).toDouble())
            /* Угол конца впадины перед следующим зубом (внутренний контур) */
            val nextGapEnd = Math.toRadians(((i + 1) * anglePerTooth - 90f - toothHalf).toDouble())

            if (i == 0) {
                gearPath.moveTo(
                    cx + innerR * cos(a1).toFloat(),
                    cy + innerR * sin(a1).toFloat()
                )
            } else {
                gearPath.lineTo(
                    cx + innerR * cos(a1).toFloat(),
                    cy + innerR * sin(a1).toFloat()
                )
            }

            /* Внешний край зуба */
            gearPath.lineTo(
                cx + outerR * cos(a1).toFloat(),
                cy + outerR * sin(a1).toFloat()
            )
            gearPath.lineTo(
                cx + outerR * cos(a2).toFloat(),
                cy + outerR * sin(a2).toFloat()
            )

            /* Возвращаемся на внутренний радиус */
            gearPath.lineTo(
                cx + innerR * cos(a2).toFloat(),
                cy + innerR * sin(a2).toFloat()
            )

            /* Внутренняя впадина до следующего зуба */
            if (i < numTeeth - 1) {
                gearPath.lineTo(
                    cx + innerR * cos(nextGapEnd).toFloat(),
                    cy + innerR * sin(nextGapEnd).toFloat()
                )
            }
        }
        gearPath.close()

        drawPath(gearPath, color = iconColor, style = Fill)
    }
}