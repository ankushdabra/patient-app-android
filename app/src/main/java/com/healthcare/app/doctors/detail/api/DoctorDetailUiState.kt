package com.healthcare.app.doctors.detail.api

sealed interface DoctorDetailUiState {
    data object Loading : DoctorDetailUiState
    
    data class Success(
        val doctor: DoctorDetailDto,
        val bookingState: BookingState = BookingState.Idle
    ) : DoctorDetailUiState
    
    data class Error(val message: String) : DoctorDetailUiState
}

sealed interface BookingState {
    data object Idle : BookingState
    data object Loading : BookingState
    data class Success(val message: String) : BookingState
    data class Error(val message: String) : BookingState
}
