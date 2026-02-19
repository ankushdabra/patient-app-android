package com.patient.app.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.patient.app.doctors.detail.ui.BookAppointmentScreen
import com.patient.app.login.ui.AuthRoute
import com.patient.app.core.storage.TokenManager
import com.patient.app.dashboard.PatientDashboard
import com.patient.app.doctors.list.ui.DoctorsListScreen

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
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = snap()) },
        exitTransition = { fadeOut(animationSpec = snap()) },
        popEnterTransition = { fadeIn(animationSpec = snap()) },
        popExitTransition = { fadeOut(animationSpec = snap()) }
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

            BookAppointmentScreen(
                doctorId = doctorId,
                tokenManager = tokenManager
            )
        }
    }
}
