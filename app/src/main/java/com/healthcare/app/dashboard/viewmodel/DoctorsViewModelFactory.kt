package com.healthcare.app.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.dashboard.api.DoctorsRepository

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
