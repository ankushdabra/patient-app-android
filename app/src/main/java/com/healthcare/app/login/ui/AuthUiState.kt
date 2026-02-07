package com.healthcare.app.login.ui

/**
 * A generic state holder for authentication flows (Login, Signup).
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
