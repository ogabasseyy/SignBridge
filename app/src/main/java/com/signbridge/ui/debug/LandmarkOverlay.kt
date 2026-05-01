package com.signbridge.ui.debug

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.signbridge.landmarks.LandmarkFrame
import com.signbridge.landmarks.LandmarkPoint

@Composable
fun LandmarkOverlay(
    frame: LandmarkFrame?,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        if (frame == null) return@Canvas

        drawPoints(frame.pose, Color.Cyan)
        drawPoints(frame.leftHand.orEmpty(), Color.Green)
        drawPoints(frame.rightHand.orEmpty(), Color.Yellow)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPoints(
    points: List<LandmarkPoint?>,
    color: Color,
) {
    points.forEach { point ->
        if (point != null) {
            drawCircle(
                color = color,
                radius = 5f,
                center = Offset(point.x * size.width, point.y * size.height),
            )
        }
    }
}
