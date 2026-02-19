package com.patient.app.login.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patient.app.core.storage.TokenManager
import com.patient.app.core.ui.UiState
import com.patient.app.core.ui.components.LoadingState
import com.patient.app.core.ui.theme.HeaderPrimaryDarkBlue
import com.patient.app.core.ui.theme.HeaderSecondaryDarkBlue
import com.patient.app.core.ui.theme.HealthcareTheme
import com.patient.app.login.api.AuthenticationRepository
import com.patient.app.login.api.ProfileUpdateRequestDto
import com.patient.app.login.api.UserDto
import com.patient.app.login.viewmodel.ProfileViewModel
import com.patient.app.login.viewmodel.ProfileViewModelFactory

@Composable
fun ProfileScreen(
    tokenManager: TokenManager
) {
    val repository = AuthenticationRepository(tokenManager)
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(repository, tokenManager)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    val isDark = when (themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme()
    }

    HealthcareTheme(darkTheme = isDark) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is UiState.Loading -> LoadingState()
                    is UiState.Error -> ProfileErrorState(
                        onRetry = viewModel::loadProfile
                    )

                    is UiState.Success -> {
                        ProfileContent(
                            user = state.data,
                            themeMode = themeMode,
                            onThemeChange = viewModel::setThemeMode,
                            onLogoutClick = viewModel::logout,
                            onUpdateProfile = viewModel::updateProfile
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileErrorState(
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
            text = "Unable to load profile. Please check your internet connection and try again.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    user: UserDto,
    themeMode: String,
    onThemeChange: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onUpdateProfile: (ProfileUpdateRequestDto) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }

    // Editable states
    var editedName by remember(user) { mutableStateOf(user.name) }
    var editedAge by remember(user) { mutableStateOf(user.age?.toString() ?: "") }
    var editedGender by remember(user) { mutableStateOf(user.gender ?: "") }
    var editedBloodGroup by remember(user) { mutableStateOf(user.bloodGroup ?: "") }
    var editedWeight by remember(user) { mutableStateOf(user.weight?.toString() ?: "") }
    var editedHeight by remember(user) { mutableStateOf(user.height?.toString() ?: "") }

    fun cancelEdit() {
        editedName = user.name
        editedAge = user.age?.toString() ?: ""
        editedGender = user.gender ?: ""
        editedBloodGroup = user.bloodGroup ?: ""
        editedWeight = user.weight?.toString() ?: ""
        editedHeight = user.height?.toString() ?: ""
        isEditMode = false
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (isEditMode) {
                        val updateRequest = ProfileUpdateRequestDto(
                            name = editedName,
                            email = user.email,
                            age = editedAge.toIntOrNull(),
                            gender = editedGender,
                            bloodGroup = editedBloodGroup,
                            weight = editedWeight.toDoubleOrNull(),
                            height = editedHeight.toDoubleOrNull()
                        )
                        onUpdateProfile(updateRequest)
                        isEditMode = false
                    } else {
                        isEditMode = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = null
                    )
                },
                text = {
                    Text(text = if (isEditMode) "Save" else "Edit")
                },
                containerColor = if (isEditMode) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                contentColor = if (isEditMode) Color.White else MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            // --- Hero Header Section ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                HeaderPrimaryDarkBlue,
                                HeaderSecondaryDarkBlue
                            )
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = 280.dp, y = (-40).dp)
                        .size(200.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.06f),
                            shape = CircleShape
                        )
                )

                Box(
                    modifier = Modifier
                        .offset(x = (-30).dp, y = 110.dp)
                        .size(120.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.04f),
                            shape = CircleShape
                        )
                )
                // Top Bar with Menu
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 8.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditMode) {
                        IconButton(onClick = { cancelEdit() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel Edit",
                                tint = Color.White
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            val themeOptions = listOf(
                                Triple("LIGHT", "Light", Icons.Outlined.LightMode),
                                Triple("DARK", "Dark", Icons.Outlined.DarkMode),
                                Triple("FOLLOW_SYSTEM", "System", Icons.Outlined.SettingsSuggest)
                            )

                            themeOptions.forEach { (option, label, icon) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                    onClick = {
                                        onThemeChange(option)
                                        showMenu = false
                                    },
                                    trailingIcon = {
                                        if (themeMode == option) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Selected",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                )
                            }

                            HorizontalDivider()

                            DropdownMenuItem(
                                text = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    onLogoutClick()
                                    showMenu = false
                                }
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    if (isEditMode) {
                        TextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            modifier = Modifier.padding(horizontal = 24.dp),
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = Color.White
                            ),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = Color.White
                            )
                        )
                    } else {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // --- Content Section ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SectionHeader("Personal Information")
                InfoCard {
                    ProfileEditableDetailRow(
                        isEditMode = isEditMode,
                        icon = Icons.Rounded.CalendarToday,
                        label = "Age",
                        value = editedAge,
                        onValueChange = { editedAge = it },
                        iconColor = Color(0xFF1976D2),
                        keyboardType = KeyboardType.Number
                    )
                    ProfileEditableDetailRow(
                        isEditMode = isEditMode,
                        icon = Icons.Rounded.Wc,
                        label = "Gender",
                        value = editedGender,
                        onValueChange = { editedGender = it },
                        iconColor = Color(0xFFE91E63)
                    )
                    ProfileEditableDetailRow(
                        isEditMode = isEditMode,
                        icon = Icons.Rounded.Bloodtype,
                        label = "Blood Group",
                        value = editedBloodGroup,
                        onValueChange = { editedBloodGroup = it },
                        iconColor = Color(0xFFD32F2F)
                    )
                    ProfileEditableDetailRow(
                        isEditMode = isEditMode,
                        icon = Icons.Rounded.MonitorWeight,
                        label = "Weight (kg)",
                        value = editedWeight,
                        onValueChange = { editedWeight = it },
                        iconColor = Color(0xFFF57C00),
                        keyboardType = KeyboardType.Decimal
                    )
                    ProfileEditableDetailRow(
                        isEditMode = isEditMode,
                        icon = Icons.Rounded.Straighten,
                        label = "Height (cm)",
                        value = editedHeight,
                        onValueChange = { editedHeight = it },
                        iconColor = Color(0xFF388E3C),
                        keyboardType = KeyboardType.Decimal
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Composable
fun ProfileEditableDetailRow(
    isEditMode: Boolean,
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    iconColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconColor
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            if (isEditMode) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = if (singleLine) ImeAction.Done else ImeAction.Default
                    ),
                    singleLine = singleLine,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                )
            } else {
                Text(
                    text = value.ifEmpty { "Not specified" },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    HealthcareTheme {
        ProfileScreenPreviewContent()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenDarkPreview() {
    HealthcareTheme(darkTheme = true) {
        ProfileScreenPreviewContent()
    }
}

@Composable
private fun ProfileScreenPreviewContent() {
    ProfileContent(
        user = UserDto(
            id = "1",
            name = "John Doe",
            email = "john.doe@example.com",
            role = "PATIENT",
            age = 30,
            gender = "Male",
            bloodGroup = "O+",
            weight = 70.0,
            height = 175.0,
            about = "Loves running and healthy food.",
            clinicAddress = "123 Health St, Wellness City"
        ),
        themeMode = "LIGHT",
        onThemeChange = {},
        onLogoutClick = {},
        onUpdateProfile = {}
    )
}
