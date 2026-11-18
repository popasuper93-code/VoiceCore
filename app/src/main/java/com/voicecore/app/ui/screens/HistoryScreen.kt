package com.voicecore.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicecore.app.ui.MainViewModel
import com.voicecore.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val messages by viewModel.messages.collectAsState()
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize().background(BlackDeep)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextPrimary)
                }
                Text("History", color = TextPrimary, fontSize = 18.sp, modifier = Modifier.padding(start = 8.dp))
            }
            IconButton(onClick = { viewModel.clearHistory() }) {
                Icon(Icons.Outlined.Delete, null, tint = TextSecondary)
            }
        }

        if (messages.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No messages yet", color = TextMuted, fontSize = 15.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(GraphiteLight).padding(14.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                if (msg.role == "user") "You" else "VoiceCore",
                                color = if (msg.role == "user") NeonPurple else TextSecondary,
                                fontSize = 12.sp, fontWeight = FontWeight.Medium
                            )
                            Text(dateFormat.format(Date(msg.timestamp)), color = TextMuted, fontSize = 11.sp)
                        }
                        Text(msg.content, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp), lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}
