package com.healthcare.app.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthcare.app.auth.api.AuthRepository
import com.healthcare.app.auth.ui.LoginState
import com.healthcare.app.core.storage.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading

            repository.login(email, password)
                .onSuccess { token ->
                    tokenManager.saveToken(token)   // 🔐 SAVE JWT
                    _state.value = LoginState.Success
                }
                .onFailure {
                    _state.value = LoginState.Error(
                        it.message ?: "Login failed"
                    )
                }
        }
    }
}
