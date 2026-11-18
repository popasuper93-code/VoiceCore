package com.voicecore.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val apiBaseUrlKey = stringPreferencesKey("api_base_url")
    private val apiKeyKey = stringPreferencesKey("api_key")
    private val modelKey = stringPreferencesKey("model")
    private val systemPromptKey = stringPreferencesKey("system_prompt")

    val apiBaseUrl: Flow<String> = context.dataStore.data.map { it[apiBaseUrlKey] ?: "" }
    val apiKey: Flow<String> = context.dataStore.data.map { it[apiKeyKey] ?: "" }
    val model: Flow<String> = context.dataStore.data.map { it[modelKey] ?: "gpt-4o-mini" }
    val systemPrompt: Flow<String> = context.dataStore.data.map {
        it[systemPromptKey] ?: "You are VoiceCore, a helpful, concise and intelligent AI voice assistant. Answer clearly and naturally."
    }

    suspend fun setApiBaseUrl(value: String) {
        context.dataStore.edit { it[apiBaseUrlKey] = value }
    }

    suspend fun setApiKey(value: String) {
        context.dataStore.edit { it[apiKeyKey] = value }
    }

    suspend fun setModel(value: String) {
        context.dataStore.edit { it[modelKey] = value }
    }

    suspend fun setSystemPrompt(value: String) {
        context.dataStore.edit { it[systemPromptKey] = value }
    }
}
