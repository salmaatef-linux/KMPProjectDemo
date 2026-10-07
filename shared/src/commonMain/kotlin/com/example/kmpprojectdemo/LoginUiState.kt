package com.example.kmpprojectdemo

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isOnline: Boolean = true,
    val csrfToken: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val isFormValid: Boolean
        get() = username.isNotBlank() && password.isNotBlank()
}
