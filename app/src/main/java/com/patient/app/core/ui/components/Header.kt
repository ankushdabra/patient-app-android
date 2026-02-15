package com.patient.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patient.app.core.ui.theme.PrimaryLight
import com.patient.app.core.ui.theme.SecondaryLight

/**
 * A flexible header component that adapts its background icon based on the screen context.
 */
@Composable
fun DashboardHeader(
    title: String,
    subtitle: String,
    count: Int,
    countLabel: String,
    icon: ImageVector = Icons.Default.MedicalServices
) {
    val headerBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                PrimaryLight,
                SecondaryLight.copy(alpha = 0.8f)
            ),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(brush = headerBrush)
    ) {
        // Large background icon representing the screen context
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 30.dp, y = 20.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.12f),
                modifier = Modifier.size(160.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "HEALTHCARE",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                if (count > 0) {
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "$count $countLabel",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 20.sp
                    ),
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        lineHeight = 36.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Preview(name = "Doctors Context")
@Composable
fun DoctorsHeaderPreview() {
    DashboardHeader(
        title = "Find Your",
        subtitle = "Specialist Doctor",
        count = 45,
        countLabel = "Specialists",
        icon = Icons.Default.MedicalServices
    )
}

@Preview(name = "Appointments Context")
@Composable
fun AppointmentsHeaderPreview() {
    DashboardHeader(
        title = "Manage Your",
        subtitle = "Appointments",
        count = 3,
        countLabel = "Upcoming",
        icon = Icons.Default.EventAvailable
    )
}

@Preview(name = "Pharmacy Context")
@Composable
fun PharmacyHeaderPreview() {
    DashboardHeader(
        title = "Order Your",
        subtitle = "Medicines",
        count = 12,
        countLabel = "Categories",
        icon = Icons.Default.Medication
    )
}
