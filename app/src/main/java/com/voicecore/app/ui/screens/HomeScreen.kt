package com.voicecore.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicecore.app.ui.AssistantState
import com.voicecore.app.ui.MainViewModel
import com.voicecore.app.ui.components.MicButton
import com.voicecore.app.ui.components.VoiceWave
import com.voicecore.app.ui.theme.BlackDeep
import com.voicecore.app.ui.theme.GraphiteLight
import com.voicecore.app.ui.theme.NeonCyan
import com.voicecore.app.ui.theme.NeonPurple
import com.voicecore.app.ui.theme.TextMuted
import com.voicecore.app.ui.theme.TextPrimary
import com.voicecore.app.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onOpenChat: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val partial by viewModel.partialText.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val apiConfigured by viewModel.isApiConfigured.collectAsState()

    val statusText = when (state) {
        AssistantState.IDLE -> if (apiConfigured) "Готов к разговору" else "Настрой API в Settings"
        AssistantState.LISTENING -> "Слушаю..."
        AssistantState.PROCESSING -> "Думаю..."
        AssistantState.SPEAKING -> "Отвечаю..."
        AssistantState.ERROR -> "Ошибка"
    }

    val statusColor = when (state) {
        AssistantState.LISTENING -> NeonCyan
        AssistantState.PROCESSING -> NeonPurple
        AssistantState.SPEAKING -> NeonCyan
        AssistantState.ERROR -> NeonPurple
        else -> if (apiConfigured) TextPrimary else TextMuted
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackDeep)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VOICECORE",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
                Text(
                    text = "AI Voice Assistant",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            Row {
                IconButton(onClick = onOpenHistory) {
                    Icon(Icons.Outlined.History, contentDescription = "История", tint = TextSecondary)
                }
                IconButton(onClick = onOpenChat) {
                    Icon(Icons.Outlined.Chat, contentDescription = "Чат", tint = TextSecondary)
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Настройки", tint = TextSecondary)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            VoiceWave(state = state, size = 260.dp)

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = statusText,
                color = statusColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )

            if (partial.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = partial,
                    color = TextSecondary,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(GraphiteLight)
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = error ?: "",
                        color = NeonPurple,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            MicButton(
                state = state,
                onClick = {
                    when (state) {
                        AssistantState.LISTENING -> viewModel.stopListening()
                        AssistantState.SPEAKING -> viewModel.stopSpeaking()
                        else -> viewModel.startListening()
                    }
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = when (state) {
                    AssistantState.LISTENING -> "Нажми чтобы остановить"
                    AssistantState.SPEAKING -> "Нажми чтобы прервать"
                    AssistantState.PROCESSING -> "Подожди..."
                    else -> "Нажми и говори"
                },
                color = TextMuted,
                fontSize = 13.sp
            )
        }
    }
}
