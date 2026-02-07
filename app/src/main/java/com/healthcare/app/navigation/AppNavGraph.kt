package com.healthcare.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.healthcare.app.doctors.detail.ui.DoctorDetailBookingScreen
import com.healthcare.app.auth.ui.LoginRoute
import com.healthcare.app.auth.ui.RegisterRoute
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.PatientDashboard
import com.healthcare.app.doctors.list.ui.DoctorsListScreen

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
            LoginRoute(
                navController = navController,
                tokenManager = tokenManager
            )
        }

        composable(Routes.REGISTER) {
            RegisterRoute(
                navController = navController,
                tokenManager = tokenManager
            )
        }

        composable(Routes.DASHBOARD) {
            PatientDashboard(tokenManager = tokenManager)
        }

        composable(Routes.DOCTORS) {
            DoctorsListScreen(
                tokenManager = tokenManager,
                onDoctorClick = { id ->
                    navController.navigate(
                        "${Routes.DOCTOR_DETAIL}/$id"
                    )
                }
            )
        }

        composable(
            route = "${Routes.DOCTOR_DETAIL}/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val doctorId =
                backStackEntry.arguments?.getString("id")!!

            DoctorDetailBookingScreen(
                doctorId = doctorId,
                tokenManager = tokenManager
            )
        }
    }
}
