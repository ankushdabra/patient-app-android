package com.patient.app.doctors.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.patient.app.core.storage.TokenManager
import com.patient.app.core.ui.UiState
import com.patient.app.doctors.detail.api.AppointmentRequestDto
import com.patient.app.doctors.detail.api.AppointmentResponseDto
import com.patient.app.doctors.detail.api.BookAppointmentRepository
import com.patient.app.doctors.detail.api.DoctorDetailDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class BookAppointmentData(
    val doctor: DoctorDetailDto,
    val bookingState: BookingState = BookingState.Idle
)

sealed interface BookingState {
    data object Idle : BookingState
    data object Loading : BookingState
    data class Success(val message: String) : BookingState
    data class Error(val message: String) : BookingState
}

class BookAppointmentViewModel(
    private val repository: BookAppointmentRepository,
    private val doctorId: String
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<BookAppointmentData>>(UiState.Loading)
    val state: StateFlow<UiState<BookAppointmentData>> = _state.asStateFlow()

    init {
        loadDoctor(doctorId)
    }

    fun loadDoctor(doctorId: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            repository.getDoctorDetail(doctorId)
                .onSuccess { doctor ->
                    _state.value = UiState.Success(BookAppointmentData(doctor = doctor))
                }
                .onFailure { error ->
                    _state.value =
                        UiState.Error(error.message ?: "Failed to load doctor")
                }
        }
    }

    fun bookAppointment(doctorId: String, date: String, time: String) {
        val currentState = _state.value
        if (currentState !is UiState.Success) return

        viewModelScope.launch {
            _state.update {
                UiState.Success(currentState.data.copy(bookingState = BookingState.Loading))
            }

            repository.bookAppointment(
                AppointmentRequestDto(doctorId, date, time)
            ).onSuccess { response ->
                _state.update {
                    UiState.Success(
                        currentState.data.copy(
                            bookingState = BookingState.Success(
                                response.message ?: "Appointment booked successfully"
                            )
                        )
                    )
                }
            }.onFailure { exception ->
                val errorMessage = if (exception is HttpException) {
                    val errorBody = exception.response()?.errorBody()?.string()
                    try {
                        val response =
                            Gson().fromJson(errorBody, AppointmentResponseDto::class.java)
                        response.error ?: response.message ?: "Booking failed"
                    } catch (e: Exception) {
                        "Booking failed"
                    }
                } else {
                    exception.message ?: "Booking failed"
                }
                _state.update {
                    UiState.Success(
                        currentState.data.copy(
                            bookingState = BookingState.Error(
                                errorMessage
                            )
                        )
                    )
                }
            }
        }
    }

    fun clearBookingState() {
        val currentState = _state.value
        if (currentState is UiState.Success) {
            _state.update { UiState.Success(currentState.data.copy(bookingState = BookingState.Idle)) }
        }
    }
}

class BookAppointmentViewModelFactory(
    private val tokenManager: TokenManager,
    private val doctorId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookAppointmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookAppointmentViewModel(BookAppointmentRepository(tokenManager), doctorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
