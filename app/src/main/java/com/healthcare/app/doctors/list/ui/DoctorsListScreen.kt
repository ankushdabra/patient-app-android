package com.healthcare.app.doctors.list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.core.ui.UiState
import com.healthcare.app.core.ui.components.ErrorState
import com.healthcare.app.core.ui.components.LoadingState
import com.healthcare.app.core.ui.theme.HealthcareTheme
import com.healthcare.app.doctors.list.api.DoctorDto
import com.healthcare.app.doctors.list.viewmodel.DoctorsViewModel
import com.healthcare.app.doctors.list.viewmodel.DoctorsViewModelFactory

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
        onRetry = viewModel::loadDoctors,
        onDoctorClick = onDoctorClick
    )
}

@Composable
fun DoctorsListScreenContent(
    state: UiState<List<DoctorDto>>,
    onRetry: () -> Unit,
    onDoctorClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF2F4F8)
                    )
                )
            )
    ) {
        when (state) {
            UiState.Loading -> {
                LoadingState()
            }

            is UiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = onRetry
                )
            }

            is UiState.Success -> {
                DoctorsList(
                    doctors = state.data,
                    onDoctorClick = onDoctorClick
                )
            }
        }
    }
}

@Composable
fun DoctorsList(
    doctors: List<DoctorDto>,
    onDoctorClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            DashboardHeader()
        }
        items(doctors, key = { it.id }) { doctor ->
            DoctorListItem(
                doctorDto = doctor,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { onDoctorClick(doctor.id) }
            )
        }
    }
}

@Composable
fun DoctorListItem(
    doctorDto: DoctorDto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFCFCFE)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doctorDto.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = doctorDto.specialization,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${doctorDto.experience} experience",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "View",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DashboardHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Find your doctor",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Book appointments with trusted specialists",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorsListScreenPreview() {
    HealthcareTheme {
        val mockDoctors = listOf(
            DoctorDto("1", "Dr. John Smith", "Cardiologist", "15 years"),
            DoctorDto("2", "Dr. Sarah Wilson", "Neurologist", "10 years"),
            DoctorDto("3", "Dr. Amit Sharma", "General Physician", "12 years")
        )
        DoctorsListScreenContent(
            state = UiState.Success(mockDoctors),
            onRetry = {},
            onDoctorClick = {}
        )
    }
}
