package com.healthcare.app.auth.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.healthcare.app.auth.api.AuthRepository
import com.healthcare.app.auth.viewmodel.RegisterViewModel
import com.healthcare.app.auth.viewmodel.RegisterViewModelFactory
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.navigation.Routes

@Composable
fun RegisterRoute(
    navController: NavHostController,
    tokenManager: TokenManager
) {
    val context = LocalContext.current
    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(AuthRepository(tokenManager), tokenManager)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is RegisterState.Success) {
            Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
            navController.navigate(Routes.DASHBOARD) {
                popUpTo(Routes.REGISTER) { inclusive = true }
            }
        }
    }

    RegisterScreen(
        state = state,
        onRegisterClick = viewModel::register
    )
}
