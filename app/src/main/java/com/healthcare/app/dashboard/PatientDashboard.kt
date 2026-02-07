package com.healthcare.app.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.healthcare.app.appointments.ui.AppointmentsScreen
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.core.ui.theme.HealthcareTheme
import com.healthcare.app.doctors.detail.ui.DoctorDetailBookingScreen
import com.healthcare.app.doctors.list.api.DoctorDto
import com.healthcare.app.doctors.list.api.DoctorsUiState
import com.healthcare.app.doctors.list.ui.DoctorsListScreen
import com.healthcare.app.doctors.list.ui.DoctorsListScreenContent
import com.healthcare.app.navigation.PatientBottomNavItem
import com.healthcare.app.navigation.Routes

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
            NavigationBar(
                containerColor = Color(0xFFF3EDF7),
                tonalElevation = NavigationBarDefaults.Elevation
            ) {
                val navBackStackEntry = navController.currentBackStackEntryAsState().value
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    // Check if the current destination or any of its parents match the route
                    // Also explicitly check if we are in doctor_detail to keep "Doctors" tab active
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true ||
                            (item == PatientBottomNavItem.Doctors && currentDestination?.route?.startsWith(Routes.DOCTOR_DETAIL) == true)

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                
                                // Restore state for other tabs, but always go to list for Doctors
                                restoreState = item != PatientBottomNavItem.Doctors
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.label)
                        },
                        label = {
                            Text(item.label)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            indicatorColor = Color(0xFFE8DEF8),
                            unselectedIconColor = Color(0xFF49454F),
                            unselectedTextColor = Color(0xFF49454F)
                        )
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
                DoctorsListScreen(
                    tokenManager = tokenManager,
                    onDoctorClick = { id ->
                        navController.navigate("${Routes.DOCTOR_DETAIL}/$id")
                    }
                )
            }
            composable(
                route = "${Routes.DOCTOR_DETAIL}/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("id")!!
                DoctorDetailBookingScreen(
                    doctorId = doctorId,
                    tokenManager = tokenManager,
                    onBookingSuccess = {
                        navController.navigate(PatientBottomNavItem.Appointments.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(PatientBottomNavItem.Appointments.route) {
                AppointmentsScreen(tokenManager = tokenManager)
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
    HealthcareTheme {
        val mockState = DoctorsUiState.Success(
            doctors = listOf(
                DoctorDto("1", "Dr. John Smith", "Cardiologist", "15 years"),
                DoctorDto("2", "Dr. Sarah Wilson", "Neurologist", "10 years")
            )
        )

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFFF3EDF7)
                ) {
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
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFFE8DEF8)
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                DoctorsListScreenContent(
                    state = mockState,
                    onRetry = { },
                    onDoctorClick = { }
                )
            }
        }
    }
}
