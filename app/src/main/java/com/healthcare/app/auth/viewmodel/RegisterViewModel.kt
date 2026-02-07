package com.healthcare.app.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthcare.app.auth.api.AuthRepository
import com.healthcare.app.auth.api.RegisterRequest
import com.healthcare.app.auth.ui.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

    fun register(
        name: String,
        email: String,
        password: String,
        age: String,
        gender: String,
        bloodGroup: String
    ) {
        viewModelScope.launch {
            _state.value = RegisterState.Loading
            repository.register(
                RegisterRequest(
                    name = name.trim(),
                    email = email.trim(),
                    password = password.trim(),
                    age = age.trim().toIntOrNull() ?: 0,
                    gender = gender.trim(),
                    bloodGroup = bloodGroup.trim()
                )
            ).onSuccess {
                _state.value = RegisterState.Success
            }.onFailure {
                _state.value = RegisterState.Error(it.message ?: "Registration failed")
            }
        }
    }
}
