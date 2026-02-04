package com.healthcare.app.appointments.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimeSelector(
    selectedTime: String?,
    onTimeSelected: (String) -> Unit
) {
    val times = listOf("10:00", "10:30", "11:00", "11:30")

    Column {
        Text(
            text = "Select Time",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            times.forEach { time ->
                FilterChip(
                    selected = selectedTime == time,
                    onClick = { onTimeSelected(time) },
                    label = { Text(time) }
                )
            }
        }
    }
}

