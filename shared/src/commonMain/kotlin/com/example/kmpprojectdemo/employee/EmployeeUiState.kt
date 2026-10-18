package com.example.kmpprojectdemo.employee

data class EmployeeUiState(
    val isLoading: Boolean = false,
    val employee: Employee? = null,
    val fullNameInput: String = "",
    val emailInput: String = "",
    val phoneInput: String = "",
    val departmentInput: String = "",
    val emailError: String? = null,
    val nameError: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isApiOnline: Boolean = true,
    val hasUnsavedChanges: Boolean = false
)
