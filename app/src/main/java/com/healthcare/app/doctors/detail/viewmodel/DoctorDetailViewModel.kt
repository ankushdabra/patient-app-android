package com.healthcare.app.doctors.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthcare.app.doctors.detail.api.AppointmentRequest
import com.healthcare.app.doctors.detail.api.AppointmentResponse
import com.healthcare.app.doctors.detail.api.DoctorDetailRepository
import com.healthcare.app.doctors.detail.api.DoctorDetailUiState
import com.healthcare.app.doctors.detail.api.BookingState
import com.google.gson.Gson
import com.healthcare.app.core.storage.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DoctorDetailViewModel(
    private val repository: DoctorDetailRepository,
    doctorId: String
) : ViewModel() {

    private val _state = MutableStateFlow<DoctorDetailUiState>(DoctorDetailUiState.Loading)
    val state: StateFlow<DoctorDetailUiState> = _state.asStateFlow()

    init {
        loadDoctor(doctorId)
    }

    private fun loadDoctor(doctorId: String) {
        viewModelScope.launch {
            _state.value = DoctorDetailUiState.Loading
            repository.getDoctorDetail(doctorId)
                .onSuccess { doctor ->
                    _state.value = DoctorDetailUiState.Success(doctor = doctor)
                }
                .onFailure { error ->
                    _state.value = DoctorDetailUiState.Error(error.message ?: "Failed to load doctor")
                }
        }
    }

    fun bookAppointment(doctorId: String, date: String, time: String) {
        val currentState = _state.value
        if (currentState !is DoctorDetailUiState.Success) return

        viewModelScope.launch {
            _state.update { currentState.copy(bookingState = BookingState.Loading) }
            
            repository.bookAppointment(
                AppointmentRequest(doctorId, date, time)
            ).onSuccess { response ->
                _state.update { 
                    currentState.copy(
                        bookingState = BookingState.Success(response.message ?: "Appointment booked successfully")
                    ) 
                }
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
                _state.update { 
                    currentState.copy(bookingState = BookingState.Error(errorMessage)) 
                }
            }
        }
    }
    
    fun clearBookingState() {
        val currentState = _state.value
        if (currentState is DoctorDetailUiState.Success) {
            _state.update { currentState.copy(bookingState = BookingState.Idle) }
        }
    }
}

class DoctorDetailViewModelFactory(
    private val tokenManager: TokenManager,
    private val doctorId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorDetailViewModel(DoctorDetailRepository(tokenManager), doctorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
