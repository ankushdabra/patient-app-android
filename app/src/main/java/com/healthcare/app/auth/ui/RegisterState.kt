package com.healthcare.app.auth.ui

sealed interface RegisterState {
    object Idle : RegisterState
    object Loading : RegisterState
    object Success : RegisterState
    data class Error(val message: String) : RegisterState
}
