package com.healthcare.app.doctors.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.doctors.api.DoctorsUiState
import com.healthcare.app.doctors.viewmodel.DoctorsViewModel
import com.healthcare.app.doctors.viewmodel.DoctorsViewModelFactory

@Composable
fun DoctorsListScreen(
    tokenManager: TokenManager,
    onDoctorClick: (String) -> Unit,
    viewModel: DoctorsViewModel = viewModel(
        factory = DoctorsViewModelFactory(tokenManager)
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DoctorsListScreenContent(
        state = state,
        onRetry = viewModel::loadDoctors,
        onDoctorClick = onDoctorClick
    )
}

@Composable
fun DoctorsListScreenContent(
    state: DoctorsUiState,
    onRetry: () -> Unit,
    onDoctorClick: (String) -> Unit
) {
    when (state) {
        DoctorsUiState.Loading -> {
            LoadingState()
        }

        is DoctorsUiState.Error -> {
            ErrorState(
                message = state.message,
                onRetry = onRetry
            )
        }

        is DoctorsUiState.Success -> {
            DoctorsList(
                doctors = state.doctors,
                onDoctorClick = onDoctorClick
            )
        }
    }
}
