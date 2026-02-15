package com.patient.app.doctors.list.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.patient.app.core.storage.TokenManager
import com.patient.app.doctors.list.api.DoctorDto
import com.patient.app.doctors.list.api.DoctorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
data class DoctorsScreenState(
    val doctors: List<DoctorDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val endReached: Boolean = false,
    val isLoadingMore: Boolean = false
)

class DoctorsViewModel(
    private val repository: DoctorsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DoctorsScreenState())
    val state: StateFlow<DoctorsScreenState> = _state.asStateFlow()

    init {
        loadDoctors()
    }

    fun loadDoctors() {
        if (_state.value.isLoading || _state.value.isLoadingMore || _state.value.endReached) return

        viewModelScope.launch {
            if (_state.value.page == 0) {
                _state.update { it.copy(isLoading = true, error = null) }
            } else {
                _state.update { it.copy(isLoadingMore = true, error = null) }
            }

            repository.getDoctors(page = _state.value.page, size = 10)
                .onSuccess { response ->
                    _state.update { currentState ->
                        currentState.copy(
                            doctors = currentState.doctors + response.content,
                            page = currentState.page + 1,
                            endReached = response.last,
                            isLoading = false,
                            isLoadingMore = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            error = error.message ?: "Unable to load doctors",
                            isLoading = false,
                            isLoadingMore = false
                        )
                    }
                }
        }
    }
}

class DoctorsViewModelFactory(
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorsViewModel(
                DoctorsRepository(tokenManager)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
