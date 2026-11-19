package com.voicecore.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.voicecore.app.ui.AssistantState
import com.voicecore.app.ui.theme.NeonCyan
import com.voicecore.app.ui.theme.NeonPurple
import com.voicecore.app.ui.theme.TextPrimary

@Composable
fun MicButton(
    state: AssistantState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = state == AssistantState.LISTENING || state == AssistantState.SPEAKING
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = tween(220),
        label = "micScale"
    )

    val colors = when (state) {
        AssistantState.LISTENING -> listOf(NeonCyan, NeonPurple)
        AssistantState.SPEAKING -> listOf(NeonPurple, NeonCyan)
        else -> listOf(NeonPurple, NeonPurple.copy(alpha = 0.65f))
    }

    Box(
        modifier = modifier
            .size(88.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Brush.linearGradient(colors))
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        TextPrimary.copy(alpha = 0.35f),
                        TextPrimary.copy(alpha = 0.05f)
                    )
                ),
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (state == AssistantState.LISTENING) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(34.dp)
        )
    }
}
