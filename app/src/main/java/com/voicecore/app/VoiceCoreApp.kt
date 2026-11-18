package com.voicecore.app

import android.app.Application
import com.voicecore.app.data.AppDatabase
import com.voicecore.app.data.SettingsRepository

class VoiceCoreApp : Application() {
    lateinit var database: AppDatabase
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        settingsRepository = SettingsRepository(this)
    }
}
