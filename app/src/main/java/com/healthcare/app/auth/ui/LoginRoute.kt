package com.healthcare.app.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.healthcare.app.auth.viewmodel.LoginViewModel
import com.healthcare.app.auth.viewmodel.LoginViewModelFactory
import com.healthcare.app.auth.api.AuthRepository
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.navigation.Routes

@Composable
fun LoginRoute(
    navController: NavHostController,
    tokenManager: TokenManager
) {
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            repository = AuthRepository(tokenManager),
            tokenManager = tokenManager
        )
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    // 🔑 Navigate on success
    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            navController.navigate(Routes.DASHBOARD) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    LoginScreen(
        state = state,
        onLoginClick =  viewModel::login
    )
}
