package com.example.kmpprojectdemo.employee

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private val LinuxPlusBackground = Color(0xFF292260)
private val LinuxPlusAccent = Color(0xFFED235B)
private val InputBg = Color(0xFFFFFFFF)
private val InputText = Color(0xFF1F1A42)

@Composable
fun EmployeeScreen(
    viewModel: EmployeeViewModel = viewModel { EmployeeViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LinuxPlusBackground)
            .safeContentPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 540.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LinuxPlusAccent.copy(alpha = 0.2f)
                    ) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Text("👤", fontSize = 20.sp)
                        }
                    }
                    Text(
                        text = "Employee Management",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = Color.White
                    )
                }

                // API Status Indicator
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (uiState.isApiOnline) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFFF9800).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, if (uiState.isApiOnline) Color(0xFF4CAF50) else Color(0xFFFF9800))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (uiState.isApiOnline) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = if (uiState.isApiOnline) "API Online" else "API Offline",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Card Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.06f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = LinuxPlusAccent)
                        }
                    } else {
                        // Employee ID (Read-only)
                        OutlinedTextField(
                            value = uiState.employee?.id?.toString() ?: "",
                            onValueChange = {},
                            label = { Text("Employee ID (Read-only)") },
                            readOnly = true,
                            enabled = false,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledContainerColor = InputBg.copy(alpha = 0.8f),
                                disabledTextColor = InputText,
                                disabledLabelColor = Color(0xFF6E6B8E),
                                disabledBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Full Name
                        OutlinedTextField(
                            value = uiState.fullNameInput,
                            onValueChange = viewModel::onFullNameChanged,
                            label = { Text("Full Name *") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = InputBg,
                                unfocusedContainerColor = InputBg,
                                focusedBorderColor = LinuxPlusAccent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color(0xFF6E6B8E),
                                focusedTextColor = InputText,
                                unfocusedTextColor = InputText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (uiState.nameError != null) {
                            Text(
                                text = uiState.nameError!!,
                                color = Color(0xFFFF6B6B),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email
                        OutlinedTextField(
                            value = uiState.emailInput,
                            onValueChange = viewModel::onEmailChanged,
                            label = { Text("Email *") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = InputBg,
                                unfocusedContainerColor = InputBg,
                                focusedBorderColor = LinuxPlusAccent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color(0xFF6E6B8E),
                                focusedTextColor = InputText,
                                unfocusedTextColor = InputText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (uiState.emailError != null) {
                            Text(
                                text = uiState.emailError!!,
                                color = Color(0xFFFF6B6B),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Phone
                        OutlinedTextField(
                            value = uiState.phoneInput,
                            onValueChange = viewModel::onPhoneChanged,
                            label = { Text("Phone") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = InputBg,
                                unfocusedContainerColor = InputBg,
                                focusedBorderColor = LinuxPlusAccent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color(0xFF6E6B8E),
                                focusedTextColor = InputText,
                                unfocusedTextColor = InputText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Department
                        OutlinedTextField(
                            value = uiState.departmentInput,
                            onValueChange = viewModel::onDepartmentChanged,
                            label = { Text("Department") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = InputBg,
                                unfocusedContainerColor = InputBg,
                                focusedBorderColor = LinuxPlusAccent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color(0xFF6E6B8E),
                                focusedTextColor = InputText,
                                unfocusedTextColor = InputText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Error message banner
                        if (uiState.errorMessage != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFF6B6B).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFFFF6B6B))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = uiState.errorMessage!!,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = { viewModel.loadEmployee(true) }) {
                                        Text("Retry", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Success message banner
                        if (uiState.successMessage != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFF4CAF50))
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                    Text(
                                        text = uiState.successMessage!!,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = viewModel::saveChanges,
                                colors = ButtonDefaults.buttonColors(containerColor = LinuxPlusAccent),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Save Changes", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = viewModel::cancelChanges,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Cancel")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = viewModel::refreshEmployee,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, LinuxPlusAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Refresh from API")
                        }
                    }
                }
            }
        }
    }
}
