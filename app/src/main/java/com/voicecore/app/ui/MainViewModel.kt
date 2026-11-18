package com.voicecore.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicecore.app.VoiceCoreApp
import com.voicecore.app.ai.AiService
import com.voicecore.app.ai.ChatMessage
import com.voicecore.app.data.MessageEntity
import com.voicecore.app.voice.SpeechHelper
import com.voicecore.app.voice.TtsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class UiMessage(
    val id: Long,
    val role: String,
    val content: String,
    val timestamp: Long
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as VoiceCoreApp
    private val messageDao = app.database.messageDao()
    private val settings = app.settingsRepository
    private val aiService = AiService()

    private val speechHelper = SpeechHelper(application)
    private val ttsHelper = TtsHelper(application)

    private val _state = MutableStateFlow(AssistantState.IDLE)
    val state: StateFlow<AssistantState> = _state.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isApiConfigured = MutableStateFlow(false)
    val isApiConfigured: StateFlow<Boolean> = _isApiConfigured.asStateFlow()

    val messages: StateFlow<List<UiMessage>> = messageDao.getAllMessages()
        .combine(MutableStateFlow(Unit)) { list, _ ->
            list.map { UiMessage(it.id, it.role, it.content, it.timestamp) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            combine(settings.apiBaseUrl, settings.apiKey) { url, key ->
                url.isNotBlank() && key.isNotBlank()
            }.collect { _isApiConfigured.value = it }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun startListening() {
        if (_state.value == AssistantState.LISTENING) {
            stopListening()
            return
        }

        _errorMessage.value = null
        _partialText.value = ""
        _state.value = AssistantState.LISTENING

        speechHelper.startListening(
            onReady = { },
            onBeginning = { },
            onPartial = { text -> _partialText.value = text },
            onResult = { text ->
                _partialText.value = ""
                _state.value = AssistantState.PROCESSING
                processUserInput(text)
            },
            onError = { msg ->
                _state.value = AssistantState.IDLE
                _errorMessage.value = msg
            },
            onEnd = { }
        )
    }

    fun stopListening() {
        speechHelper.stop()
        if (_state.value == AssistantState.LISTENING) {
            _state.value = AssistantState.IDLE
        }
    }

    fun sendText(text: String) {
        if (text.isBlank()) return
        _errorMessage.value = null
        _state.value = AssistantState.PROCESSING
        processUserInput(text.trim())
    }

    private fun processUserInput(text: String) {
        viewModelScope.launch {
            // Save user message
            messageDao.insert(MessageEntity(role = "user", content = text))

            val baseUrl = settings.apiBaseUrl.first()
            val apiKey = settings.apiKey.first()
            val model = settings.model.first()
            val systemPrompt = settings.systemPrompt.first()

            val history = messageDao.getAllMessages().first()
                .map { ChatMessage(it.role, it.content) }

            val result = aiService.chat(baseUrl, apiKey, model, systemPrompt, history)

            result.fold(
                onSuccess = { reply ->
                    messageDao.insert(MessageEntity(role = "assistant", content = reply))
                    _state.value = AssistantState.SPEAKING
                    ttsHelper.speak(
                        text = reply,
                        onStart = { },
                        onDone = { _state.value = AssistantState.IDLE },
                        onError = { _state.value = AssistantState.IDLE }
                    )
                },
                onFailure = { e ->
                    _state.value = AssistantState.IDLE
                    _errorMessage.value = e.message ?: "Unknown error"
                }
            )
        }
    }

    fun stopSpeaking() {
        ttsHelper.stop()
        _state.value = AssistantState.IDLE
    }

    fun clearHistory() {
        viewModelScope.launch {
            messageDao.clearAll()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechHelper.stop()
        ttsHelper.shutdown()
    }
}
