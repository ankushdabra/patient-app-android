package com.healthcare.app.doctors.list.api

sealed interface DoctorsUiState {
    object Loading : DoctorsUiState
    data class Success(val doctors: List<DoctorDto>) : DoctorsUiState
    data class Error(val message: String) : DoctorsUiState
}