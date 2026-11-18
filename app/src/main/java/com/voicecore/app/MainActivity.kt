package com.voicecore.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.voicecore.app.ui.MainViewModel
import com.voicecore.app.ui.screens.ChatScreen
import com.voicecore.app.ui.screens.HistoryScreen
import com.voicecore.app.ui.screens.HomeScreen
import com.voicecore.app.ui.screens.SettingsScreen
import com.voicecore.app.ui.theme.BlackDeep
import com.voicecore.app.ui.theme.VoiceCoreTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result handled by system */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            VoiceCoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BlackDeep
                ) {
                    VoiceCoreAppNav()
                }
            }
        }
    }
}

@Composable
fun VoiceCoreAppNav() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as VoiceCoreApp

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onOpenChat = { navController.navigate("chat") },
                onOpenHistory = { navController.navigate("history") },
                onOpenSettings = { navController.navigate("settings") }
            )
        }
        composable("chat") {
            ChatScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("history") {
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                app = app,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
