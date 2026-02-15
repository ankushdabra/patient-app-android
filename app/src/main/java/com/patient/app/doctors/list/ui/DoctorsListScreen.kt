package com.patient.app.doctors.list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patient.app.core.storage.TokenManager
import com.patient.app.core.ui.components.DashboardHeader
import com.patient.app.core.ui.components.LoadingState
import com.patient.app.core.ui.theme.HealthcareTheme
import com.patient.app.core.ui.theme.SuccessGreen
import com.patient.app.doctors.list.api.DoctorDto
import com.patient.app.doctors.list.viewmodel.DoctorsScreenState
import com.patient.app.doctors.list.viewmodel.DoctorsViewModel
import com.patient.app.doctors.list.viewmodel.DoctorsViewModelFactory

@Composable
fun DoctorsListScreen(
    tokenManager: TokenManager,
    onDoctorClick: (String) -> Unit,
    viewModel: DoctorsViewModel = viewModel(
        factory = DoctorsViewModelFactory(tokenManager)
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DoctorsListScreenContent(
        state = state,
        onLoadMore = viewModel::loadDoctors,
        onDoctorClick = onDoctorClick
    )
}

@Composable
fun DoctorsListScreenContent(
    state: DoctorsScreenState,
    onLoadMore: () -> Unit,
    onDoctorClick: (String) -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val backgroundBrush = remember(backgroundColor, surfaceVariantColor) {
        Brush.verticalGradient(
            colors = listOf(
                backgroundColor,
                surfaceVariantColor.copy(alpha = 0.3f)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
    ) {
        if (state.isLoading && state.doctors.isEmpty()) {
            LoadingState()
        } else if (state.error != null && state.doctors.isEmpty()) {
            EnhancedErrorState(
                onRetry = onLoadMore
            )
        } else {
            DoctorsList(
                state = state,
                onLoadMore = onLoadMore,
                onDoctorClick = onDoctorClick
            )
        }
    }
}

@Composable
fun EnhancedErrorState(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
        
        Spacer(Modifier.height(32.dp))
        
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(Modifier.height(12.dp))
        
        Text(
            text = "Unable to load doctors. Please check your internet connection and try again.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        
        Spacer(Modifier.height(48.dp))
        
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Try Again",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DoctorsList(
    state: DoctorsScreenState,
    onLoadMore: () -> Unit,
    onDoctorClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(contentType = "header") {
            DashboardHeader(
                title = "Find Your",
                subtitle = "Specialist Doctor",
                count = state.doctors.size,
                countLabel = "Specialists",
                icon = Icons.Default.MedicalServices
            )
        }
        itemsIndexed(
            items = state.doctors, 
            key = { _, doctor -> doctor.id },
            contentType = { _, _ -> "doctor" }
        ) { index, doctor ->
            DoctorListItem(
                doctorDto = doctor,
                modifier = Modifier.padding(horizontal = 12.dp),
                onBookNowClick = { onDoctorClick(doctor.id) }
            )

            if (index >= state.doctors.size - 1 && !state.endReached && !state.isLoadingMore) {
                LaunchedEffect(Unit) {
                    onLoadMore()
                }
            }
        }

        if (state.isLoadingMore) {
            item(contentType = "loader") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun DoctorListItem(
    doctorDto: DoctorDto,
    onBookNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile Image Placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = doctorDto.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(Modifier.width(8.dp))
                        
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = SuccessGreen
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = doctorDto.rating.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }
                        }
                    }

                    Text(
                        text = doctorDto.specialization,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoChip(
                            icon = Icons.Outlined.WorkOutline,
                            text = "${doctorDto.experience} Years Exp"
                        )
                        InfoChip(
                            icon = Icons.Outlined.CurrencyRupee,
                            text = "₹${doctorDto.consultationFee.toInt()}"
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Next Available",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    val nextAvailableText = remember(doctorDto.nextAvailable) {
                        doctorDto.nextAvailable?.let { text ->
                            val parts = text.split(", ")
                            if (parts.size >= 2) {
                                val day = parts[0]
                                val formattedDay = if (day.equals("Today", ignoreCase = true) || day.equals("Tomorrow", ignoreCase = true)) {
                                    day
                                } else {
                                    day.take(3)
                                }
                                "$formattedDay, ${parts[1]}"
                            } else {
                                if (text.equals("Today", ignoreCase = true) || text.equals("Tomorrow", ignoreCase = true)) text else text.take(3)
                            }
                        } ?: "Today, 04:30 PM"
                    }
                    Text(
                        text = nextAvailableText,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Button(
                    onClick = onBookNowClick,
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Book Now",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorsListScreenPreview() {
    HealthcareTheme {
        val mockDoctors = listOf(
            DoctorDto(
                id = "1",
                name = "Dr. Amit Sharma",
                specialization = "Cardiologist",
                experience = 12,
                consultationFee = 800.0,
                rating = 4.8,
                profileImage = null,
                nextAvailable = "Today, 05:00 PM"
            ),
            DoctorDto(
                id = "2",
                name = "Dr. Sneha Patil",
                specialization = "Dermatologist",
                experience = 8,
                consultationFee = 600.0,
                rating = 4.6,
                profileImage = null,
                nextAvailable = "Tomorrow, 10:00 AM"
            ),
            DoctorDto(
                id = "3",
                name = "Dr. Vikram Singh",
                specialization = "Orthopedic",
                experience = 15,
                consultationFee = 1000.0,
                rating = 4.9,
                profileImage = null,
                nextAvailable = "Wednesday, 06:30 PM"
            )
        )
        
        DoctorsListScreenContent(
            state = DoctorsScreenState(doctors = mockDoctors),
            onLoadMore = {},
            onDoctorClick = {}
        )
    }
}
