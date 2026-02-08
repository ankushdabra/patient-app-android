package com.healthcare.app.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.core.ui.UiState
import com.healthcare.app.login.api.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthenticationRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Boolean>>(UiState.Success(false))
    val state: StateFlow<UiState<Boolean>> = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading

            repository.login(email.trim(), password.trim())
                .onSuccess { token ->
                    tokenManager.saveToken(token)
                    _state.value = UiState.Success(true)
                }
                .onFailure { exception ->
                    _state.value = UiState.Error(exception.message ?: "Login failed")
                }
        }
    }
    
    fun resetState() {
        _state.value = UiState.Success(false)
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
