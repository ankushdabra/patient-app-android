package com.healthcare.app.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthcare.app.login.api.AuthenticationRepository
import com.healthcare.app.login.ui.AuthUiState
import com.healthcare.app.core.storage.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthenticationRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            repository.login(email.trim(), password.trim())
                .onSuccess { token ->
                    tokenManager.saveToken(token)
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { exception ->
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "Login failed"
                        ) 
                    }
                }
        }
    }
}

class LoginViewModelFactory(
    private val repository: AuthenticationRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, tokenManager) as T
            }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
