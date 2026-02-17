package com.patient.app.dashboard

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
import androidx.compose.runtime.getValue
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
import com.patient.app.appointments.ui.AppointmentDetailScreen
import com.patient.app.appointments.ui.AppointmentListScreen
import com.patient.app.core.storage.TokenManager
import com.patient.app.core.ui.theme.HealthcareTheme
import com.patient.app.doctors.detail.ui.BookAppointmentScreen
import com.patient.app.doctors.list.api.DoctorDto
import com.patient.app.doctors.list.ui.DoctorsListScreen
import com.patient.app.doctors.list.ui.DoctorsListScreenContent
import com.patient.app.doctors.list.viewmodel.DoctorsScreenState
import com.patient.app.login.ui.ProfileScreen
import com.patient.app.navigation.PatientBottomNavItem
import com.patient.app.navigation.Routes
import com.patient.app.prescriptions.ui.PrescriptionDetailScreen
import com.patient.app.prescriptions.ui.PrescriptionListScreen

@Composable
fun PatientDashboard(tokenManager: TokenManager) {
    val navController = rememberNavController()

    val items = listOf(
        PatientBottomNavItem.Doctors,
        PatientBottomNavItem.Appointments,
        PatientBottomNavItem.Prescriptions,
        PatientBottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    // Check if the current route is a main bottom nav route
    val showBottomBar = items.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = NavigationBarDefaults.Elevation
                ) {
                    items.forEach { item ->
                        val isSelected =
                            currentDestination?.hierarchy?.any { it.route == item.route } == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentDestination?.route != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
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
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
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
        DashboardPreviewContent()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PatientDashboardDarkPreview() {
    HealthcareTheme(darkTheme = true) {
        DashboardPreviewContent()
    }
}

@Composable
private fun DashboardPreviewContent() {
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
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            DoctorsListScreenContent(
                state = DoctorsScreenState(doctors = mockDoctors),
                onLoadMore = { },
                onDoctorClick = { }
            )
        }
    }
}
