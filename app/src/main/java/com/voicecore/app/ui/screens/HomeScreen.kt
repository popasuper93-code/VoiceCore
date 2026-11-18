package com.voicecore.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Modifier.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicecore.app.ui.AssistantState
import com.voicecore.app.ui.MainViewModel
import com.voicecore.app.ui.components.MicButton
import com.voicecore.app.ui.components.VoiceWave
import com.voicecore.app.ui.theme.*

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
        AssistantState.IDLE -> if (apiConfigured) "Ready" else "API not configured"
        AssistantState.LISTENING -> "Listening..."
        AssistantState.PROCESSING -> "Thinking..."
        AssistantState.SPEAKING -> "Speaking..."
        AssistantState.ERROR -> "Error"
    }

    Box(modifier = Modifier.fillMaxSize().background(BlackDeep)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("VOICECORE", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Row {
                IconButton(onClick = onOpenHistory) { Icon(Icons.Outlined.History, null, tint = TextSecondary) }
                IconButton(onClick = onOpenChat) { Icon(Icons.Outlined.Chat, null, tint = TextSecondary) }
                IconButton(onClick = onOpenSettings) { Icon(Icons.Outlined.Settings, null, tint = TextSecondary) }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            VoiceWave(state = state, size = 240.dp)
            Spacer(Modifier.height(32.dp))
            Text(statusText, color = if (state == AssistantState.IDLE && !apiConfigured) TextMuted else TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (partial.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(partial, color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
            }
            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Box(Modifier.clip(RoundedCornerShape(12.dp)).background(GraphiteLight).padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(error ?: "", color = NeonPurple, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }
            Spacer(Modifier.height(48.dp))
            MicButton(state = state, onClick = {
                when (state) {
                    AssistantState.LISTENING -> viewModel.stopListening()
                    AssistantState.SPEAKING -> viewModel.stopSpeaking()
                    else -> viewModel.startListening()
                }
            })
            Spacer(Modifier.height(16.dp))
            Text(
                when (state) {
                    AssistantState.LISTENING -> "Tap to stop"
                    AssistantState.SPEAKING -> "Tap to stop speaking"
                    else -> "Tap to speak"
                },
                color = TextMuted, fontSize = 13.sp
            )
        }
    }
}
