package com.healthcare.app.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DoctorDetailBookingScreen(doctorId: String) {

    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    val isBookingEnabled = selectedDate != null && selectedTime != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        DoctorDetailHeader(
            name = doctorId,
            specialization = "Cardiologist",
            experience = "10 yrs experience",
            fee = "₹500"
        )

        Divider()

        DateSelector(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )

        TimeSelector(
            selectedTime = selectedTime,
            onTimeSelected = { selectedTime = it }
        )

        Spacer(Modifier.height(16.dp))

        BookAppointmentButton(
            enabled = isBookingEnabled,
            onClick = {
                // For now, just log / toast later
                println("Booking for $selectedDate at $selectedTime for doctor $doctorId")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorDetailBookingPreview() {
    MaterialTheme {
        DoctorDetailBookingScreen(doctorId = "Dr. Amit Sharma")
    }
}
