package com.patient.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.patient.app.core.storage.TokenManager
import com.patient.app.dashboard.PatientDashboard
import com.patient.app.login.ui.AuthRoute

@Composable
fun AppNavGraph(
    tokenManager: TokenManager
) {
    val navController = rememberNavController()
    
    // Use produceState to distinguish between "loading" and "no token"
    val tokenState by produceState<String?>(initialValue = "LOADING_INITIAL_TOKEN") {
        tokenManager.token.collect { value = it }
    }

    if (tokenState == "LOADING_INITIAL_TOKEN") {
        // Return empty or a splash screen to avoid flickering
        return
    }

    val startDestination =
        if (tokenState.isNullOrEmpty()) Routes.LOGIN else Routes.DASHBOARD

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            AuthRoute(
                route = Routes.LOGIN,
                navController = navController,
                tokenManager = tokenManager
            )
        }

        composable(Routes.REGISTER) {
            AuthRoute(
                route = Routes.REGISTER,
                navController = navController,
                tokenManager = tokenManager
            )
        }

        composable(Routes.DASHBOARD) {
            PatientDashboard(tokenManager = tokenManager)
        }
    }
}
