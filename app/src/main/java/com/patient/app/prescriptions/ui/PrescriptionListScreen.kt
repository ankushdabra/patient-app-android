package com.patient.app.prescriptions.ui

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patient.app.core.storage.TokenManager
import com.patient.app.core.ui.UiState
import com.patient.app.core.ui.components.DashboardHeader
import com.patient.app.core.ui.components.LoadingState
import com.patient.app.core.ui.theme.HealthcareTheme
import com.patient.app.prescriptions.api.PrescriptionDto
import com.patient.app.prescriptions.api.PrescriptionsRepository
import com.patient.app.prescriptions.viewmodel.PrescriptionListViewModel
import com.patient.app.prescriptions.viewmodel.PrescriptionListViewModelFactory

@Composable
fun PrescriptionListScreen(
    tokenManager: TokenManager,
    onPrescriptionClick: (String) -> Unit
) {
    val repository = PrescriptionsRepository(tokenManager)
    val viewModel: PrescriptionListViewModel = viewModel(
        factory = PrescriptionListViewModelFactory(repository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PrescriptionListScreenContent(
        state = uiState,
        onRetry = viewModel::loadPrescriptions,
        onPrescriptionClick = onPrescriptionClick
    )
}

@Composable
fun PrescriptionListScreenContent(
    state: UiState<List<PrescriptionDto>>,
    onRetry: () -> Unit,
    onPrescriptionClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        when (state) {
            UiState.Loading -> {
                LoadingState()
            }

            is UiState.Error -> {
                PrescriptionListErrorState(
                    onRetry = onRetry
                )
            }

            is UiState.Success -> {
                PrescriptionsList(
                    prescriptions = state.data,
                    onPrescriptionClick = onPrescriptionClick
                )
            }
        }
    }
}

@Composable
fun PrescriptionListErrorState(
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
            text = "Unable to load prescriptions. Please check your internet connection and try again.",
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
fun PrescriptionsList(
    prescriptions: List<PrescriptionDto>,
    onPrescriptionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            DashboardHeader(
                title = "View Your",
                subtitle = "Prescriptions",
                count = prescriptions.size,
                countLabel = "Total",
                icon = Icons.Default.Medication
            )
        }
        
        if (prescriptions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No prescriptions found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(prescriptions, key = { it.id }) { prescription ->
                PrescriptionListItem(
                    prescription = prescription,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable { onPrescriptionClick(prescription.id) }
                )
            }
        }
    }
}

@Composable
fun PrescriptionListItem(
    prescription: PrescriptionDto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = prescription.prescriptionDate,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = prescription.doctorName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Prescribed Medications",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = prescription.medications,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (!prescription.instructions.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Tap to view instructions",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrescriptionListScreenSuccessPreview() {
    HealthcareTheme {
        PrescriptionListScreenContent(
            state = UiState.Success(
                listOf(
                    PrescriptionDto(
                        id = "1",
                        medications = "Amoxicillin 500mg, Paracetamol 500mg",
                        instructions = "Take 1 tablet every 8 hours",
                        notes = "Drink plenty of fluids",
                        prescriptionDate = "24 Oct 2023",
                        appointmentId = "app1",
                        appointmentDate = "24 Oct 2023",
                        doctorName = "Dr. Sarah Wilson",
                        patientName = "John Doe"
                    ),
                    PrescriptionDto(
                        id = "2",
                        medications = "Cetirizine 10mg",
                        instructions = "Take 1 tablet daily at night",
                        notes = null,
                        prescriptionDate = "15 Oct 2023",
                        appointmentId = "app2",
                        appointmentDate = "15 Oct 2023",
                        doctorName = "Dr. Michael Chen",
                        patientName = "John Doe"
                    )
                )
            ),
            onRetry = {},
            onPrescriptionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrescriptionListScreenEmptyPreview() {
    HealthcareTheme {
        PrescriptionListScreenContent(
            state = UiState.Success(emptyList()),
            onRetry = {},
            onPrescriptionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrescriptionListScreenLoadingPreview() {
    HealthcareTheme {
        PrescriptionListScreenContent(
            state = UiState.Loading,
            onRetry = {},
            onPrescriptionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrescriptionListScreenErrorPreview() {
    HealthcareTheme {
        PrescriptionListScreenContent(
            state = UiState.Error("Something went wrong"),
            onRetry = {},
            onPrescriptionClick = {}
        )
    }
}
