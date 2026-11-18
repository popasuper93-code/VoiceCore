package com.voicecore.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicecore.app.VoiceCoreApp
import com.voicecore.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(app: VoiceCoreApp, onBack: () -> Unit) {
    val settings = app.settingsRepository
    val scope = rememberCoroutineScope()
    val currentUrl by settings.apiBaseUrl.collectAsState(initial = "")
    val currentKey by settings.apiKey.collectAsState(initial = "")
    val currentModel by settings.model.collectAsState(initial = "gpt-4o-mini")
    val currentPrompt by settings.systemPrompt.collectAsState(initial = "")
    var baseUrl by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    LaunchedEffect(currentUrl, currentKey, currentModel, currentPrompt) {
        baseUrl = currentUrl; apiKey = currentKey; model = currentModel; systemPrompt = currentPrompt
    }

    Column(modifier = Modifier.fillMaxSize().background(BlackDeep).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextPrimary) }
            Text("Settings", color = TextPrimary, fontSize = 18.sp, modifier = Modifier.padding(start = 8.dp))
        }
        Column(modifier = Modifier.padding(20.dp)) {
            Text("AI Provider", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text("Supports any OpenAI-compatible endpoint (OpenAI, Groq, Together, local Ollama with proxy, etc.)", color = TextMuted, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(20.dp))
            SettingsField("Base URL", baseUrl, { baseUrl = it; saved = false }, "https://api.openai.com/v1")
            Spacer(Modifier.height(14.dp))
            SettingsField("API Key", apiKey, { apiKey = it; saved = false }, "sk-...", isSecret = true)
            Spacer(Modifier.height(14.dp))
            SettingsField("Model", model, { model = it; saved = false }, "gpt-4o-mini")
            Spacer(Modifier.height(14.dp))
            SettingsField("System Prompt", systemPrompt, { systemPrompt = it; saved = false }, "You are VoiceCore...", singleLine = false)
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    scope.launch {
                        settings.setApiBaseUrl(baseUrl.trim())
                        settings.setApiKey(apiKey.trim())
                        settings.setModel(model.trim().ifBlank { "gpt-4o-mini" })
                        settings.setSystemPrompt(systemPrompt.trim())
                        saved = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                Text(if (saved) "Saved" else "Save", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
            Text("Voice recognition and text-to-speech work offline. Only the AI replies require a configured API.", color = TextMuted, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun SettingsField(
    label: String, value: String, onValueChange: (String) -> Unit, placeholder: String,
    isSecret: Boolean = false, singleLine: Boolean = true
) {
    Text(label, color = TextSecondary, fontSize = 13.sp)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextMuted) },
        singleLine = singleLine, maxLines = if (singleLine) 1 else 5,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonPurple, unfocusedBorderColor = GraphiteLight,
            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
            cursorColor = NeonPurple, focusedContainerColor = Graphite, unfocusedContainerColor = Graphite
        ),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isSecret) PasswordVisualTransformation() else VisualTransformation.None
    )
}
