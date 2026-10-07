package com.example.kmpprojectdemo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kmpprojectdemo.shared.generated.resources.Res
import kmpprojectdemo.shared.generated.resources.linuxp_logo_1
import org.jetbrains.compose.resources.painterResource

private val LinuxPlusBackground = Color(0xFF292260)
private val LinuxPlusAccent = Color(0xFFED235B)
private val LinuxPlusInputBg = Color(0xFFFFFFFF)
private val LinuxPlusInputText = Color(0xFF1F1A42)
private val LinuxPlusLabelUnfocused = Color(0xFF6E6B8E)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel { LoginViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoggedIn) {
        HomeScreen(
            username = uiState.username,
            onLogoutClick = viewModel::onLogout
        )
    } else {
        LoginContent(
            uiState = uiState,
            onUsernameChanged = viewModel::onUsernameChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onPasswordVisibilityToggled = viewModel::onPasswordVisibilityToggled,
            onLoginSubmitted = viewModel::onLoginSubmitted,
            onErrorDismissed = viewModel::onErrorDismissed
        )
    }
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggled: () -> Unit,
    onLoginSubmitted: () -> Unit,
    onErrorDismissed: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    // Error Dialog
    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = onErrorDismissed,
            title = {
                Text(
                    text = "Connection Error",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = LinuxPlusInputText
                )
            },
            text = {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4A4668)
                )
            },
            confirmButton = {
                Button(
                    onClick = onErrorDismissed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LinuxPlusAccent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("OK")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LinuxPlusBackground)
            .safeContentPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Welcome",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = Color.White
                    )

                    // Mode Indicator Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (uiState.isOnline) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFFF9800).copy(alpha = 0.2f),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (uiState.isOnline) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (uiState.isOnline) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                        shape = CircleShape
                                    )
                            )
                            Text(
                                text = if (uiState.isOnline) "Online Mode" else "Offline Mode",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                        }
                    }
                }

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    ),
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(4.dp)
                        .background(LinuxPlusAccent, shape = RoundedCornerShape(2.dp))
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Input Fields Section
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = onUsernameChanged,
                    placeholder = { Text("Username", color = LinuxPlusLabelUnfocused) },
                    label = { Text("Username") },
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LinuxPlusInputBg,
                        unfocusedContainerColor = LinuxPlusInputBg,
                        disabledContainerColor = LinuxPlusInputBg.copy(alpha = 0.8f),
                        focusedBorderColor = LinuxPlusAccent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = LinuxPlusLabelUnfocused,
                        focusedPlaceholderColor = LinuxPlusLabelUnfocused,
                        unfocusedPlaceholderColor = LinuxPlusLabelUnfocused,
                        focusedTextColor = LinuxPlusInputText,
                        unfocusedTextColor = LinuxPlusInputText
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChanged,
                    placeholder = { Text("Password", color = LinuxPlusLabelUnfocused) },
                    label = { Text("Password") },
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(
                            onClick = onPasswordVisibilityToggled,
                            enabled = !uiState.isLoading
                        ) {
                            Text(
                                text = if (uiState.isPasswordVisible) "Hide" else "Show",
                                color = LinuxPlusAccent,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LinuxPlusInputBg,
                        unfocusedContainerColor = LinuxPlusInputBg,
                        disabledContainerColor = LinuxPlusInputBg.copy(alpha = 0.8f),
                        focusedBorderColor = LinuxPlusAccent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = LinuxPlusLabelUnfocused,
                        focusedPlaceholderColor = LinuxPlusLabelUnfocused,
                        unfocusedPlaceholderColor = LinuxPlusLabelUnfocused,
                        focusedTextColor = LinuxPlusInputText,
                        unfocusedTextColor = LinuxPlusInputText
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (uiState.isFormValid && !uiState.isLoading) {
                                focusManager.clearFocus()
                                onLoginSubmitted()
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onLoginSubmitted()
                    },
                    enabled = uiState.isFormValid && !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LinuxPlusAccent,
                        contentColor = Color.White,
                        disabledContainerColor = LinuxPlusAccent.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Prominent Adaptive Transparent Linux Plus Logo (linuxp_logo_1)
            Image(
                painter = painterResource(Res.drawable.linuxp_logo_1),
                contentDescription = "Linux Plus Logo",
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .widthIn(min = 280.dp, max = 460.dp)
                    .heightIn(min = 120.dp, max = 190.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
