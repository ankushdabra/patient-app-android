package com.healthcare.app.appointments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthcare.app.appointments.api.AppointmentDto
import com.healthcare.app.appointments.api.AppointmentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class AppointmentsUiState {
    object Loading : AppointmentsUiState()
    data class Success(val appointments: List<AppointmentDto>) : AppointmentsUiState()
    data class Error(val message: String) : AppointmentsUiState()
}

class AppointmentsViewModel(private val repository: AppointmentsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<AppointmentsUiState>(AppointmentsUiState.Loading)
    val uiState: StateFlow<AppointmentsUiState> = _uiState.asStateFlow()

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _uiState.update { AppointmentsUiState.Loading }
            repository.getAppointments()
                .onSuccess { appointments ->
                    _uiState.update { AppointmentsUiState.Success(appointments) }
                }
                .onFailure { exception ->
                    _uiState.update { 
                        AppointmentsUiState.Error(exception.message ?: "Unknown error") 
                    }
                }
        }
    }
}

class AppointmentsViewModelFactory(private val repository: AppointmentsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
