package com.patient.app.appointments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.patient.app.appointments.api.AppointmentDto
import com.patient.app.appointments.api.AppointmentsRepository
import com.patient.app.core.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppointmentListViewModel(private val repository: AppointmentsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<AppointmentDto>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<AppointmentDto>>> = _uiState.asStateFlow()

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }
            repository.getAppointments()
                .onSuccess { appointments ->
                    _uiState.update { UiState.Success(appointments) }
                }
                .onFailure { exception ->
                    _uiState.update { 
                        UiState.Error(exception.message ?: "Unknown error")
                    }
                }
        }
    }
}

class AppointmentListViewModelFactory(private val repository: AppointmentsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
