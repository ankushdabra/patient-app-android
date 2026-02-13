package com.healthcare.app.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.core.ui.UiState
import com.healthcare.app.login.api.AuthenticationRepository
import com.healthcare.app.login.api.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: AuthenticationRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<UserDto>>(UiState.Loading)
    val uiState: StateFlow<UiState<UserDto>> = _uiState.asStateFlow()

    val themeMode: StateFlow<String> = tokenManager.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "FOLLOW_SYSTEM")

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getProfile()
                .onSuccess { user ->
                    _uiState.value = UiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to load profile")
                }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            tokenManager.saveThemeMode(mode)
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
        }
    }
}

class ProfileViewModelFactory(
    private val repository: AuthenticationRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
