package com.example.kmpprojectdemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmpprojectdemo.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(isOnline = repository.isOnline()))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Reactively observe connectivity changes in real-time
        viewModelScope.launch {
            repository.isOnlineFlow.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopMonitoring()
    }

    fun onUsernameChanged(username: String) {
        _uiState.update {
            it.copy(
                username = username,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun onPasswordVisibilityToggled() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun onErrorDismissed() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun onLoginSubmitted() {
        val currentState = _uiState.value
        if (!currentState.isFormValid || currentState.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            val result = repository.authenticate(
                username = currentState.username,
                password = currentState.password
            )

            result.onSuccess { token ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        csrfToken = token,
                        successMessage = "Authentication successful"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Authentication failed"
                    )
                }
            }
        }
    }

    fun onLogout() {
        repository.clearSession()
        _uiState.update {
            LoginUiState(isOnline = repository.isOnline())
        }
    }
}
