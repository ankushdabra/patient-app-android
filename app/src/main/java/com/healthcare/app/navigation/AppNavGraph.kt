package com.healthcare.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.healthcare.app.auth.LoginRoute
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.dashboard.ui.PatientDashboard

@Composable
fun AppNavGraph(
    tokenManager: TokenManager
) {
    val navController = rememberNavController()
    val token by tokenManager.token.collectAsState(initial = null)

    val startDestination =
        if (token.isNullOrEmpty()) Routes.LOGIN else Routes.DASHBOARD

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginRoute(
                navController = navController,
                tokenManager = tokenManager
            )
        }
        composable(Routes.DASHBOARD) {
            PatientDashboard(tokenManager = tokenManager)
        }
    }
}
