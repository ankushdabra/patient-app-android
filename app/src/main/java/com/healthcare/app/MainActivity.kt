package com.healthcare.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.navigation.AppNavGraph
import com.healthcare.app.core.ui.theme.HealthcareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenManager = TokenManager(this)
        setContent {
            HealthcareTheme {
                AppNavGraph(tokenManager = tokenManager)
            }
        }
    }
}
