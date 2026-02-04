package com.healthcare.app.doctors.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.healthcare.app.doctors.api.DoctorDto

@Composable
fun DoctorsList(
    doctors: List<DoctorDto>,
    onDoctorClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            DashboardHeader()
        }
        items(doctors) { doctor ->
            DoctorListItem(
                doctorDto = doctor,
                modifier = Modifier.clickable { onDoctorClick(doctor.id) }
            )
        }
    }
}
