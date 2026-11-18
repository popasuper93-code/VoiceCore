package com.voicecore.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.voicecore.app.ui.AssistantState
import com.voicecore.app.ui.theme.NeonBlue
import com.voicecore.app.ui.theme.NeonCyan
import com.voicecore.app.ui.theme.NeonPurple
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VoiceWave(
    state: AssistantState,
    size: Dp = 220.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val activeColor = when (state) {
        AssistantState.LISTENING -> NeonCyan
        AssistantState.PROCESSING -> NeonPurple
        AssistantState.SPEAKING -> NeonBlue
        AssistantState.ERROR -> Color(0xFFEF4444)
        else -> NeonPurple.copy(alpha = 0.6f)
    }

    val intensity = when (state) {
        AssistantState.IDLE -> 0.4f
        AssistantState.LISTENING -> 1.0f
        AssistantState.PROCESSING -> 0.8f
        AssistantState.SPEAKING -> 0.9f
        AssistantState.ERROR -> 0.5f
    }

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2, this.size.height / 2)
        val maxRadius = this.size.minDimension / 2

        // Outer glow rings
        for (i in 1..3) {
            val ringRadius = maxRadius * (0.55f + i * 0.12f) * (if (state != AssistantState.IDLE) pulse else 1f)
            drawCircle(
                color = activeColor.copy(alpha = 0.08f * intensity / i),
                radius = ringRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Core circle
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    activeColor.copy(alpha = 0.35f * intensity),
                    activeColor.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = center,
                radius = maxRadius * 0.5f
            ),
            radius = maxRadius * 0.48f * (if (state != AssistantState.IDLE) pulse else 1f),
            center = center
        )

        // Animated arcs
        val arcCount = 8
        for (i in 0 until arcCount) {
            val angle = (phase + i * (360f / arcCount)) * (Math.PI / 180f).toFloat()
            val radius = maxRadius * 0.62f
            val start = Offset(
                center.x + cos(angle) * radius * 0.7f,
                center.y + sin(angle) * radius * 0.7f
            )
            val end = Offset(
                center.x + cos(angle) * radius,
                center.y + sin(angle) * radius
            )
            drawLine(
                color = activeColor.copy(alpha = 0.4f * intensity),
                start = start,
                end = end,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Inner core
        drawCircle(
            color = activeColor.copy(alpha = 0.9f * intensity),
            radius = 12.dp.toPx() * (if (state != AssistantState.IDLE) pulse else 1f),
            center = center
        )
    }
}
