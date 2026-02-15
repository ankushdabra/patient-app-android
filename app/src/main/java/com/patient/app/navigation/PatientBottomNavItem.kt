package com.patient.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class PatientBottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Doctors : PatientBottomNavItem(
        route = "doctors",
        label = "Doctors",
        icon = Icons.Outlined.MedicalServices
    )

    object Appointments : PatientBottomNavItem(
        route = "appointments",
        label = "Appointments",
        icon = Icons.Outlined.Event
    )

    object Prescriptions : PatientBottomNavItem(
        route = "prescriptions",
        label = "Prescriptions",
        icon = Icons.Outlined.Description
    )

    object Profile : PatientBottomNavItem(
        route = "profile",
        label = "Profile",
        icon = Icons.Outlined.Person
    )
}
