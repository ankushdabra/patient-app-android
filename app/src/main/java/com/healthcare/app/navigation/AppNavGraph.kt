package com.healthcare.app.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.healthcare.app.appointments.ui.DoctorDetailBookingScreen
import com.healthcare.app.auth.api.AuthRepository
import com.healthcare.app.auth.ui.LoginRoute
import com.healthcare.app.auth.ui.RegisterScreen
import com.healthcare.app.auth.viewmodel.RegisterViewModel
import com.healthcare.app.auth.viewmodel.RegisterViewModelFactory
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.dashboard.ui.PatientDashboard
import com.healthcare.app.doctors.ui.DoctorsListScreen

@RequiresApi(Build.VERSION_CODES.O)
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

        composable(Routes.REGISTER) {
            val viewModel: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(AuthRepository(tokenManager))
            )
            val state by viewModel.state.collectAsStateWithLifecycle()

            RegisterScreen(
                state = state,
                onRegisterClick = viewModel::register
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
