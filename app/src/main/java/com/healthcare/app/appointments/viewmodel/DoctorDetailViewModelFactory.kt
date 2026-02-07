package com.healthcare.app.appointments.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.healthcare.app.appointments.api.DoctorDetailRepository
import com.healthcare.app.core.storage.TokenManager

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
