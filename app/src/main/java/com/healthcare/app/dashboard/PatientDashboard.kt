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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.healthcare.app.appointments.ui.AppointmentDetailScreen
import com.healthcare.app.appointments.ui.AppointmentListScreen
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.core.ui.UiState
import com.healthcare.app.core.ui.theme.HealthcareTheme
import com.healthcare.app.doctors.detail.ui.BookAppointmentScreen
import com.healthcare.app.doctors.list.api.DoctorDto
import com.healthcare.app.doctors.list.ui.DoctorsListScreen
import com.healthcare.app.doctors.list.ui.DoctorsListScreenContent
import com.healthcare.app.login.ui.ProfileScreen
import com.healthcare.app.navigation.PatientBottomNavItem
import com.healthcare.app.navigation.Routes
import com.healthcare.app.prescriptions.ui.PrescriptionDetailScreen
import com.healthcare.app.prescriptions.ui.PrescriptionListScreen

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
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = NavigationBarDefaults.Elevation
            ) {
                val navBackStackEntry = navController.currentBackStackEntryAsState().value
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    val isSelected =
                        currentDestination?.hierarchy?.any { it.route == item.route } == true ||
                                (item == PatientBottomNavItem.Doctors && currentDestination?.route?.startsWith(
                                    Routes.DOCTOR_DETAIL
                                ) == true) ||
                                (item == PatientBottomNavItem.Appointments && currentDestination?.route?.startsWith(
                                    Routes.APPOINTMENT_DETAIL
                                ) == true) ||
                                (item == PatientBottomNavItem.Prescriptions && currentDestination?.route?.startsWith(
                                    Routes.PRESCRIPTION_DETAIL
                                ) == true)

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentDestination?.route != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState =
                                        item != PatientBottomNavItem.Doctors &&
                                                item != PatientBottomNavItem.Appointments &&
                                                item != PatientBottomNavItem.Prescriptions
                                }
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.label)
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                BookAppointmentScreen(
                    doctorId = doctorId,
                    tokenManager = tokenManager,
                    onBackClick = { navController.popBackStack() },
                    onBookingSuccess = {
                        navController.navigate(PatientBottomNavItem.Appointments.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(PatientBottomNavItem.Appointments.route) {
                AppointmentListScreen(
                    tokenManager = tokenManager,
                    onAppointmentClick = { id ->
                        navController.navigate("${Routes.APPOINTMENT_DETAIL}/$id")
                    }
                )
            }
            composable(
                route = "${Routes.APPOINTMENT_DETAIL}/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val appointmentId = backStackEntry.arguments?.getString("id")!!
                AppointmentDetailScreen(
                    appointmentId = appointmentId,
                    tokenManager = tokenManager,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(PatientBottomNavItem.Prescriptions.route) {
                PrescriptionListScreen(
                    tokenManager = tokenManager,
                    onPrescriptionClick = { id ->
                        navController.navigate("${Routes.PRESCRIPTION_DETAIL}/$id")
                    }
                )
            }
            composable(
                route = "${Routes.PRESCRIPTION_DETAIL}/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val prescriptionId = backStackEntry.arguments?.getString("id")!!
                PrescriptionDetailScreen(
                    prescriptionId = prescriptionId,
                    tokenManager = tokenManager,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(PatientBottomNavItem.Profile.route) {
                ProfileScreen(tokenManager = tokenManager)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PatientDashboardPreview() {
    HealthcareTheme {
        val mockDoctors = listOf(
            DoctorDto("1", "Dr. John Smith", "Cardiologist", 15, 100.0, 4.5, null),
            DoctorDto("2", "Dr. Sarah Wilson", "Neurologist", 10, 120.0, 4.8, null)
        )

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
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
                            label = { 
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                DoctorsListScreenContent(
                    state = UiState.Success(mockDoctors),
                    onRetry = { },
                    onDoctorClick = { }
                )
            }
        }
    }
}
