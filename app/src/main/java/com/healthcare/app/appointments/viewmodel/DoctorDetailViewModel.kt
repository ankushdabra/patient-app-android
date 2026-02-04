package com.healthcare.app.appointments.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthcare.app.appointments.api.DoctorDetailRepository
import com.healthcare.app.appointments.api.DoctorDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorDetailViewModel(
    private val repository: DoctorDetailRepository,
    doctorId: String
) : ViewModel() {

    private val _state =
        MutableStateFlow<DoctorDetailUiState>(DoctorDetailUiState.Loading)
    val state: StateFlow<DoctorDetailUiState> = _state

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
}
