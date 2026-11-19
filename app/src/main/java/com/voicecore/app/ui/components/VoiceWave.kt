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
import androidx.compose.ui.Modifier
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
    size: Dp = 240.dp,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "wave")

    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val pulse by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val activeColor = when (state) {
        AssistantState.LISTENING -> NeonCyan
        AssistantState.PROCESSING -> NeonPurple
        AssistantState.SPEAKING -> NeonBlue
        AssistantState.ERROR -> Color(0xFFEF4444)
        else -> NeonPurple.copy(alpha = 0.55f)
    }

    val intensity = when (state) {
        AssistantState.IDLE -> 0.45f
        AssistantState.LISTENING -> 1f
        AssistantState.PROCESSING -> 0.85f
        AssistantState.SPEAKING -> 0.95f
        AssistantState.ERROR -> 0.55f
    }

    val isActive = state != AssistantState.IDLE

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val maxR = this.size.minDimension / 2f
        val p = if (isActive) pulse else 1f

        for (i in 1..4) {
            drawCircle(
                color = activeColor.copy(alpha = (0.12f * intensity) / i),
                radius = maxR * (0.48f + i * 0.11f) * p,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    activeColor.copy(alpha = 0.4f * intensity),
                    activeColor.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = center,
                radius = maxR * 0.55f
            ),
            radius = maxR * 0.5f * p,
            center = center
        )

        val count = 10
        for (i in 0 until count) {
            val angle = (phase + i * (360f / count)) * (Math.PI.toFloat() / 180f)
            val r = maxR * 0.68f
            val start = Offset(
                center.x + cos(angle) * r * 0.72f,
                center.y + sin(angle) * r * 0.72f
            )
            val end = Offset(
                center.x + cos(angle) * r,
                center.y + sin(angle) * r
            )
            drawLine(
                color = activeColor.copy(alpha = 0.45f * intensity),
                start = start,
                end = end,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        drawCircle(
            color = activeColor.copy(alpha = 0.95f * intensity),
            radius = 14.dp.toPx() * p,
            center = center
        )
    }
}
