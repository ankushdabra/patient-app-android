package com.healthcare.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.navigation.AppNavGraph
import com.healthcare.app.core.ui.theme.HealthcareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenManager = TokenManager(this)
        setContent {
            val themeMode by tokenManager.themeMode.collectAsState(initial = "FOLLOW_SYSTEM")
            
            val isDarkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            HealthcareTheme(darkTheme = isDarkTheme) {
                AppNavGraph(tokenManager = tokenManager)
            }
        }
    }
}
