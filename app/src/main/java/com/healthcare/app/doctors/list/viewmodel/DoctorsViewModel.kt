package com.healthcare.app.doctors.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.doctors.list.api.DoctorsRepository
import com.healthcare.app.doctors.list.api.DoctorsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorsViewModel(
    private val repository: DoctorsRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow<DoctorsUiState>(DoctorsUiState.Loading)
    val state: StateFlow<DoctorsUiState> = _state

    init {
        loadDoctors()
    }

    fun loadDoctors() {
        viewModelScope.launch {
            _state.value = DoctorsUiState.Loading

            repository.getDoctors()
                .onSuccess {
                    _state.value = DoctorsUiState.Success(it)
                }
                .onFailure {
                    _state.value = DoctorsUiState.Error(
                        it.message ?: "Unable to load doctors"
                    )
                }
        }
    }
}

class DoctorsViewModelFactory(
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorsViewModel(
                DoctorsRepository(tokenManager)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}