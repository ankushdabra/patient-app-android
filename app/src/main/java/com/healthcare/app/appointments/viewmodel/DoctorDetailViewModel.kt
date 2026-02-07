package com.healthcare.app.appointments.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthcare.app.appointments.api.AppointmentRequest
import com.healthcare.app.appointments.api.BookingState
import com.healthcare.app.appointments.api.DoctorDetailRepository
import com.healthcare.app.appointments.api.DoctorDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorDetailViewModel(
    private val repository: DoctorDetailRepository,
    doctorId: String
) : ViewModel() {

    private val _state = MutableStateFlow<DoctorDetailUiState>(DoctorDetailUiState.Loading)
    val state: StateFlow<DoctorDetailUiState> = _state

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState

    init {
        loadDoctor(doctorId)
    }

    private fun loadDoctor(doctorId: String) {
        viewModelScope.launch {
            repository.getDoctorDetail(doctorId)
                .onSuccess {
                    _state.value = DoctorDetailUiState.Success(it)
                }
                .onFailure {
                    _state.value = DoctorDetailUiState.Error(
                        it.message ?: "Failed to load doctor"
                    )
                }
        }
    }

    fun bookAppointment(doctorId: String, date: String, time: String) {
        viewModelScope.launch {
            _bookingState.value = BookingState.Loading
            repository.bookAppointment(
                AppointmentRequest(doctorId, date, time)
            ).onSuccess {
                _bookingState.value = BookingState.Success(it.message)
            }.onFailure {
                _bookingState.value = BookingState.Error(it.message ?: "Booking failed")
            }
        }
    }
}