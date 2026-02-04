package com.healthcare.app.appointments.api

sealed interface DoctorDetailUiState {
    object Loading : DoctorDetailUiState
    data class Success(val doctor: DoctorDetailDto) : DoctorDetailUiState
    data class Error(val message: String) : DoctorDetailUiState
}
