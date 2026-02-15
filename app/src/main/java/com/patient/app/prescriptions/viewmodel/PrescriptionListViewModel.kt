package com.patient.app.prescriptions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.patient.app.core.ui.UiState
import com.patient.app.prescriptions.api.PrescriptionDto
import com.patient.app.prescriptions.api.PrescriptionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrescriptionListViewModel(
    private val repository: PrescriptionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<PrescriptionDto>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<PrescriptionDto>>> = _uiState.asStateFlow()

    init {
        loadPrescriptions()
    }

    fun loadPrescriptions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getPrescriptions()
                .onSuccess { prescriptions ->
                    _uiState.value = UiState.Success(prescriptions)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to load prescriptions")
                }
        }
    }
}

class PrescriptionListViewModelFactory(
    private val repository: PrescriptionsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrescriptionListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrescriptionListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
