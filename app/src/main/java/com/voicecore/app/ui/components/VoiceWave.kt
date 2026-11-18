package com.voicecore.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.voicecore.app.ui.AssistantState
import com.voicecore.app.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VoiceWave(state: AssistantState, size: Dp = 220.dp, modifier: Modifier = Modifier) {
    val t = rememberInfiniteTransition(label = "w")
    val phase by t.animateFloat(0f, 360f, infiniteRepeatable(tween(3000, easing = LinearEasing)), label = "p")
    val pulse by t.animateFloat(0.85f, 1.15f, infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse), label = "u")
    val color = when (state) {
        AssistantState.LISTENING -> NeonCyan
        AssistantState.PROCESSING -> NeonPurple
        AssistantState.SPEAKING -> NeonBlue
        AssistantState.ERROR -> Color(0xFFEF4444)
        else -> NeonPurple.copy(alpha = 0.6f)
    }
    val intens = when (state) {
        AssistantState.IDLE -> 0.4f
        AssistantState.LISTENING -> 1f
        AssistantState.PROCESSING -> 0.8f
        AssistantState.SPEAKING -> 0.9f
        else -> 0.5f
    }
    Canvas(modifier = modifier.size(size)) {
        val c = Offset(this.size.width / 2, this.size.height / 2)
        val maxR = this.size.minDimension / 2
        for (i in 1..3) {
            drawCircle(color.copy(alpha = 0.08f * intens / i), maxR * (0.55f + i * 0.12f) * (if (state != AssistantState.IDLE) pulse else 1f), c, style = Stroke(2.dp.toPx()))
        }
        drawCircle(
            Brush.radialGradient(listOf(color.copy(alpha = 0.35f * intens), color.copy(alpha = 0.05f), Color.Transparent), c, maxR * 0.5f),
            maxR * 0.48f * (if (state != AssistantState.IDLE) pulse else 1f), c
        )
        for (i in 0 until 8) {
            val a = (phase + i * 45f) * (Math.PI / 180f).toFloat()
            val r = maxR * 0.62f
            drawLine(color.copy(alpha = 0.4f * intens), Offset(c.x + cos(a) * r * 0.7f, c.y + sin(a) * r * 0.7f), Offset(c.x + cos(a) * r, c.y + sin(a) * r), 3.dp.toPx(), StrokeCap.Round)
        }
        drawCircle(color.copy(alpha = 0.9f * intens), 12.dp.toPx() * (if (state != AssistantState.IDLE) pulse else 1f), c)
    }
}
