package com.healthcare.app.doctors.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.core.ui.UiState
import com.healthcare.app.doctors.list.api.DoctorDto
import com.healthcare.app.doctors.list.api.DoctorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorsViewModel(
    private val repository: DoctorsRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<DoctorDto>>>(UiState.Loading)
    val state: StateFlow<UiState<List<DoctorDto>>> = _state

    init {
        loadDoctors()
    }

    fun loadDoctors() {
        // If we already have data, don't show loading state to avoid flickering
        if (_state.value is UiState.Success) {
            refreshDoctors()
            return
        }

        viewModelScope.launch {
            _state.value = UiState.Loading
            fetchDoctors()
        }
    }

    private fun refreshDoctors() {
        viewModelScope.launch {
            fetchDoctors()
        }
    }

    private suspend fun fetchDoctors() {
        repository.getDoctors()
            .onSuccess {
                _state.value = UiState.Success(it)
            }
            .onFailure {
                _state.value = UiState.Error(
                    it.message ?: "Unable to load doctors"
                )
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
