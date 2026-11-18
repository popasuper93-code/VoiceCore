package com.voicecore.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicecore.app.ui.MainViewModel
import com.voicecore.app.ui.theme.*

@Composable
fun ChatScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val messages by viewModel.messages.collectAsState()
    val state by viewModel.state.collectAsState()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(Modifier = Modifier.fillMaxSize().background(BlackDeep)) {
        Row(Modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextPrimary)
            }
            Text("Chat", color = TextPrimary, fontSize = 18.sp, modifier = Modifier.padding(start = 8.dp))
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.role == "user"
                Row(Modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
                    Box(
                        modifier = Modifier.widthIn(max = 300.dp).clip(RoundedCornerShape(16.dp))
                            .background(if (isUser) NeonPurple.copy(alpha = 0.25f) else GraphiteLight)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(msg.content, color = TextPrimary, fontSize = 15.sp, lineHeight = 20.sp)
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message...", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple, unfocusedBorderColor = GraphiteLight,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                    cursorColor = NeonPurple, focusedContainerColor = Graphite, unfocusedContainerColor = Graphite
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (input.isNotBlank() && state != com.voicecore.app.ui.AssistantState.PROCESSING) {
                        viewModel.sendText(input); input = ""
                    }
                })
            )
            Spacer(Modifier = Modifier.size(8.dp))
            IconButton(onClick = {
                if (input.isNotBlank() && state != com.voicecore.app.ui.AssistantState.PROCESSING) {
                    viewModel.sendText(input); input = ""
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, null, tint = NeonPurple)
            }
        }
    }
}
