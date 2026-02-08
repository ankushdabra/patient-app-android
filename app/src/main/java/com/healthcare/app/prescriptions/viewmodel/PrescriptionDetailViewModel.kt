package com.healthcare.app.prescriptions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthcare.app.core.ui.UiState
import com.healthcare.app.prescriptions.api.PrescriptionDto
import com.healthcare.app.prescriptions.api.PrescriptionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrescriptionDetailViewModel(
    private val repository: PrescriptionsRepository,
    private val prescriptionId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<PrescriptionDto>>(UiState.Loading)
    val uiState: StateFlow<UiState<PrescriptionDto>> = _uiState.asStateFlow()

    init {
        loadPrescriptionDetail()
    }

    fun loadPrescriptionDetail() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getPrescriptions()
                .onSuccess { prescriptions ->
                    val prescription = prescriptions.find { it.id == prescriptionId }
                    if (prescription != null) {
                        _uiState.value = UiState.Success(prescription)
                    } else {
                        _uiState.value = UiState.Error("Prescription not found")
                    }
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to load prescription")
                }
        }
    }
}

class PrescriptionDetailViewModelFactory(
    private val repository: PrescriptionsRepository,
    private val prescriptionId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrescriptionDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrescriptionDetailViewModel(repository, prescriptionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
