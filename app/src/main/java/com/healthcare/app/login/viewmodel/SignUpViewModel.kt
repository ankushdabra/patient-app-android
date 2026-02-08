package com.healthcare.app.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.core.ui.UiState
import com.healthcare.app.login.api.AuthenticationRepository
import com.healthcare.app.login.api.SignUpRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repository: AuthenticationRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Boolean>>(UiState.Success(false))
    val state: StateFlow<UiState<Boolean>> = _state.asStateFlow()

    fun register(
        name: String,
        email: String,
        password: String,
        age: String,
        gender: String,
        bloodGroup: String
    ) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            repository.register(
                SignUpRequestDto(
                    name = name.trim(),
                    email = email.trim(),
                    password = password.trim(),
                    age = age.trim().toIntOrNull() ?: 0,
                    gender = gender.trim(),
                    bloodGroup = bloodGroup.trim()
                )
            ).onSuccess { response ->
                tokenManager.saveToken(response.token)
                _state.value = UiState.Success(true)
            }.onFailure { exception ->
                _state.value = UiState.Error(exception.message ?: "Registration failed")
            }
        }
    }

    fun resetState() {
        _state.value = UiState.Success(false)
    }
}

class SignUpViewModelFactory(
    private val repository: AuthenticationRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
