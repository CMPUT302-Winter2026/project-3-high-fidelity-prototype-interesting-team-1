package com.example.myvocabulary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

internal object AppStartupState {
    @Volatile
    var splashReleased: Boolean = false
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !AppStartupState.splashReleased }
        super.onCreate(savedInstanceState)
        val startupSnapshot = runBlocking(Dispatchers.IO) {
            loadDictionaryStartupSnapshot(applicationContext)
        }
        AppStartupState.splashReleased = true
        enableEdgeToEdge()
        setContent {
            VocabularyApp(startupSnapshot = startupSnapshot)
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun GreetingPreview() {
    VocabularyApp()
}
