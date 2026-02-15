package com.patient.app.appointments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.patient.app.appointments.api.AppointmentDto
import com.patient.app.appointments.api.AppointmentsRepository
import com.patient.app.core.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentDetailViewModel(
    private val repository: AppointmentsRepository,
    private val appointmentId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AppointmentDto>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadAppointmentDetail()
    }

    fun loadAppointmentDetail() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getAppointmentDetail(appointmentId)
                .onSuccess { appointment ->
                    _uiState.value = UiState.Success(appointment)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(
                        error.message ?: "Failed to load appointment details"
                    )
                }
        }
    }
}

class AppointmentDetailViewModelFactory(
    private val repository: AppointmentsRepository,
    private val appointmentId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentDetailViewModel(repository, appointmentId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
