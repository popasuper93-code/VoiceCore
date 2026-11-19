package com.voicecore.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicecore.app.VoiceCoreApp
import com.voicecore.app.ui.theme.BlackDeep
import com.voicecore.app.ui.theme.Graphite
import com.voicecore.app.ui.theme.GraphiteLight
import com.voicecore.app.ui.theme.NeonPurple
import com.voicecore.app.ui.theme.TextMuted
import com.voicecore.app.ui.theme.TextPrimary
import com.voicecore.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    app: VoiceCoreApp,
    onBack: () -> Unit
) {
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
        baseUrl = currentUrl
        apiKey = currentKey
        model = currentModel
        systemPrompt = currentPrompt
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackDeep)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
            }
            Text(
                text = "Настройки",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "AI Provider",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Любой OpenAI-compatible endpoint: OpenAI, Groq, Together, локальный Ollama через proxy.",
                color = TextMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            SettingsField("Base URL", baseUrl, { baseUrl = it; saved = false }, "https://api.openai.com/v1")
            Spacer(modifier = Modifier.height(14.dp))
            SettingsField("API Key", apiKey, { apiKey = it; saved = false }, "sk-...", isSecret = true)
            Spacer(modifier = Modifier.height(14.dp))
            SettingsField("Model", model, { model = it; saved = false }, "gpt-4o-mini")
            Spacer(modifier = Modifier.height(14.dp))
            SettingsField("System Prompt", systemPrompt, { systemPrompt = it; saved = false }, "You are VoiceCore...", singleLine = false)

            Spacer(modifier = Modifier.height(28.dp))

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
                Text(
                    text = if (saved) "Сохранено" else "Сохранить",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Распознавание речи и озвучка работают оффлайн. API нужен только для ответов AI.",
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun SettingsField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isSecret: Boolean = false,
    singleLine: Boolean = true
) {
    Text(text = label, color = TextSecondary, fontSize = 13.sp)
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextMuted) },
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 5,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonPurple,
            unfocusedBorderColor = GraphiteLight,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = NeonPurple,
            focusedContainerColor = Graphite,
            unfocusedContainerColor = Graphite
        ),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isSecret) PasswordVisualTransformation() else VisualTransformation.None
    )
}
