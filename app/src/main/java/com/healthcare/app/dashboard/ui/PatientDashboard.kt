package com.healthcare.app.dashboard.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.dashboard.api.DoctorDto
import com.healthcare.app.dashboard.api.DoctorsUiState
import com.healthcare.app.navigation.PatientBottomNavItem

@Composable
fun PatientDashboard(tokenManager: TokenManager) {
    val navController = rememberNavController()

    val items = listOf(
        PatientBottomNavItem.Doctors,
        PatientBottomNavItem.Appointments,
        PatientBottomNavItem.Prescriptions,
        PatientBottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute =
                    navController.currentBackStackEntryAsState()
                        .value?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.label)
                        },
                        label = {
                            Text(item.label)
                        }
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = PatientBottomNavItem.Doctors.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(PatientBottomNavItem.Doctors.route) {
                DoctorsListScreen(tokenManager = tokenManager)
            }
            composable(PatientBottomNavItem.Appointments.route) {
                //PlaceholderScreen("Appointments")
            }
            composable(PatientBottomNavItem.Prescriptions.route) {
                //PlaceholderScreen("Prescriptions")
            }
            composable(PatientBottomNavItem.Profile.route) {
               // PlaceholderScreen("Profile")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PatientDashboardPreview() {
    MaterialTheme {
        // In Previews, we avoid real network calls by using the Content composable directly
        // and bypassing the ViewModel instantiation which triggers OkHttpClient/Retrofit.
        val mockState = DoctorsUiState.Success(
            doctors = listOf(
                DoctorDto("Dr. John Smith", "Cardiologist", "15 years"),
                DoctorDto("Dr. Sarah Wilson", "Neurologist", "10 years")
            )
        )
        
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val items = listOf(
                        PatientBottomNavItem.Doctors,
                        PatientBottomNavItem.Appointments,
                        PatientBottomNavItem.Prescriptions,
                        PatientBottomNavItem.Profile
                    )
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = item == PatientBottomNavItem.Doctors,
                            onClick = { },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        ) { padding ->
            androidx.compose.foundation.layout.Box(modifier = Modifier.padding(padding)) {
                DoctorsListScreenContent(
                    state = mockState,
                    onRetry = { }
                )
            }
        }
    }
}
