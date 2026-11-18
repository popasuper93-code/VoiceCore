package com.voicecore.app.ai

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7
)

data class ChatChoice(
    val message: ChatMessage
)

data class ChatResponse(
    val choices: List<ChatChoice>
)

class AiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun chat(
        baseUrl: String,
        apiKey: String,
        model: String,
        systemPrompt: String,
        history: List<ChatMessage>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (baseUrl.isBlank() || apiKey.isBlank()) {
                return@withContext Result.failure(
                    IllegalStateException("API not configured. Open Settings and enter Base URL + API Key.")
                )
            }

            val messages = mutableListOf<ChatMessage>()
            if (systemPrompt.isNotBlank()) {
                messages.add(ChatMessage("system", systemPrompt))
            }
            messages.addAll(history)

            val requestBody = ChatRequest(model = model, messages = messages)
            val json = gson.toJson(requestBody)

            val cleanBase = baseUrl.trimEnd('/')
            val url = if (cleanBase.endsWith("/v1")) {
                "$cleanBase/chat/completions"
            } else {
                "$cleanBase/v1/chat/completions"
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(json.toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        Exception("API error ${response.code}: ${body.take(200)}")
                    )
                }
                val parsed = gson.fromJson(body, ChatResponse::class.java)
                val content = parsed.choices.firstOrNull()?.message?.content
                    ?: return@withContext Result.failure(Exception("Empty response from API"))
                Result.success(content.trim())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
