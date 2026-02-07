package com.healthcare.app.doctors.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthcare.app.doctors.detail.api.AppointmentRequest
import com.healthcare.app.doctors.detail.api.AppointmentResponse
import com.healthcare.app.doctors.detail.api.BookingState
import com.healthcare.app.doctors.detail.api.DoctorDetailRepository
import com.healthcare.app.doctors.detail.api.DoctorDetailUiState
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
                _bookingState.value = BookingState.Success(it.message ?: "Success")
            }.onFailure { exception ->
                val errorMessage = if (exception is HttpException) {
                    val errorBody = exception.response()?.errorBody()?.string()
                    try {
                        val response = Gson().fromJson(errorBody, AppointmentResponse::class.java)
                        response.error ?: response.message ?: "Booking failed"
                    } catch (e: Exception) {
                        "Booking failed"
                    }
                } else {
                    exception.message ?: "Booking failed"
                }
                _bookingState.value = BookingState.Error(errorMessage)
            }
        }
    }
}
