package com.patient.app.login.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patient.app.R
import com.patient.app.core.ui.UiState
import com.patient.app.core.ui.theme.HealthcareTheme

@Composable
fun LoginScreen(
    state: UiState<Boolean>,
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    fun validate(): Boolean {
        var isValid = true
        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Invalid email format"
            isValid = false
        } else {
            emailError = null
        }

        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordError = null
        }
        return isValid
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 🔷 TOP BLUE GRADIENT BACKGROUND WITH CURVE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.42f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 80.dp, bottomEnd = 80.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // 🔷 LOGO (Circular White Container)
                Surface(
                    modifier = Modifier.size(90.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_healthcare_logo),
                            contentDescription = "Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "VitalSync Doctor",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 28.sp
                    )
                )

                Text(
                    text = "Welcome back, Doctor",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 🔷 LOGIN CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Login to your account",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // EMAIL FIELD
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = null
                            },
                            placeholder = { Text("Email Address") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            isError = emailError != null,
                            supportingText = emailError?.let { { Text(it) } },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(Modifier.height(16.dp))

                        // PASSWORD FIELD
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = null
                            },
                            placeholder = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                val image = if (passwordVisible)
                                    Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff

                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = image,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            },
                            isError = passwordError != null,
                            supportingText = passwordError?.let { { Text(it) } },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (validate()) {
                                        onLoginClick(email, password)
                                    }
                                }
                            )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "Forgot Password?",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.clickable { /* Handle Forgot Password */ }
                            )
                        }

                        Spacer(Modifier.height(32.dp))

                        // SIGN IN BUTTON
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (validate()) {
                                    onLoginClick(email, password)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            enabled = state !is UiState.Loading
                        ) {
                            if (state is UiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Sign In",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }

                        if (state is UiState.Error) {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 🔷 FOOTER (Sign Up)
                Text(
                    text = buildAnnotatedString {
                        append("Don't have an account? ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Sign Up")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .clickable { onRegisterClick() }
                        .padding(bottom = 32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6)
@Composable
fun LoginScreenPreview() {
    HealthcareTheme {
        LoginScreen(
            state = UiState.Success(false),
            onLoginClick = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_6, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenDarkPreview() {
    HealthcareTheme {
        LoginScreen(
            state = UiState.Success(false),
            onLoginClick = { _, _ -> }
        )
    }
}
