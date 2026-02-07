package com.healthcare.app.doctors.detail.api

sealed interface BookingState {
    object Idle : BookingState
    object Loading : BookingState
    data class Success(val message: String) : BookingState
    data class Error(val message: String) : BookingState
}