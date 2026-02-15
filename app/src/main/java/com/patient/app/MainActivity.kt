package com.patient.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.patient.app.core.storage.TokenManager
import com.patient.app.navigation.AppNavGraph
import com.patient.app.core.ui.theme.HealthcareTheme

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
