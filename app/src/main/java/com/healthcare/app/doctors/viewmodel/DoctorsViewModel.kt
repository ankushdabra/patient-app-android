package com.healthcare.app.doctors.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthcare.app.doctors.api.DoctorsRepository
import com.healthcare.app.doctors.api.DoctorsUiState
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