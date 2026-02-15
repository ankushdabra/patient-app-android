package com.patient.app.login.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.patient.app.core.storage.TokenManager
import com.patient.app.core.ui.UiState
import com.patient.app.login.api.AuthenticationRepository
import com.patient.app.login.viewmodel.LoginViewModel
import com.patient.app.login.viewmodel.LoginViewModelFactory
import com.patient.app.login.viewmodel.SignUpViewModel
import com.patient.app.login.viewmodel.SignUpViewModelFactory
import com.patient.app.navigation.Routes

@Composable
fun AuthRoute(
    route: String,
    navController: NavHostController,
    tokenManager: TokenManager
) {
    val context = LocalContext.current
    val repository = AuthenticationRepository(tokenManager)

    when (route) {
        Routes.LOGIN -> {
            val viewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(
                    repository = repository,
                    tokenManager = tokenManager
                )
            )
            val state by viewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(state) {
                val s = state
                if (s is UiState.Success && s.data) {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                state = state,
                onLoginClick = viewModel::login,
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        Routes.REGISTER -> {
            val viewModel: SignUpViewModel = viewModel(
                factory = SignUpViewModelFactory(
                    repository = repository,
                    tokenManager = tokenManager
                )
            )
            val state by viewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(state) {
                val s = state
                if (s is UiState.Success && s.data) {
                    Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            }

            SignUpScreen(
                state = state,
                onRegisterClick = viewModel::register
            )
        }
    }
}
