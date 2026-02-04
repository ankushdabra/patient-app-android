package com.healthcare.app.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DateSelector(
    selectedDate: String?,
    onDateSelected: (String) -> Unit
) {
    val dates = listOf("Mon 12", "Tue 13", "Wed 14")

    Column {
        Text(
            text = "Select Date",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            dates.forEach { date ->
                AssistChip(
                    onClick = { onDateSelected(date) },
                    label = { Text(date) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor =
                            if (selectedDate == date)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}

