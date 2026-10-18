package com.example.kmpprojectdemo.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeViewModel(
    private val repository: EmployeeRepository = EmployeeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()

    private var savedEmployee: Employee? = null

    init {
        loadEmployee()
    }

    fun loadEmployee(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = repository.getEmployee()
            result.onSuccess { emp ->
                savedEmployee = emp
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        employee = emp,
                        fullNameInput = emp.fullName,
                        emailInput = emp.email,
                        phoneInput = emp.phone,
                        departmentInput = emp.department,
                        isApiOnline = true,
                        hasUnsavedChanges = false,
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isApiOnline = false,
                        errorMessage = err.message ?: "Failed to load employee data."
                    )
                }
            }
        }
    }

    fun onFullNameChanged(value: String) {
        _uiState.update { state ->
            val nameErr = if (value.isBlank()) "Full Name is required" else null
            val updated = state.copy(fullNameInput = value, nameError = nameErr, successMessage = null)
            updated.copy(hasUnsavedChanges = checkUnsavedChanges(updated))
        }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { state ->
            val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
            val emailErr = when {
                value.isBlank() -> "Email is required"
                !value.matches(emailRegex) -> "Invalid email format"
                else -> null
            }
            val updated = state.copy(emailInput = value, emailError = emailErr, successMessage = null)
            updated.copy(hasUnsavedChanges = checkUnsavedChanges(updated))
        }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { state ->
            val updated = state.copy(phoneInput = value, successMessage = null)
            updated.copy(hasUnsavedChanges = checkUnsavedChanges(updated))
        }
    }

    fun onDepartmentChanged(value: String) {
        _uiState.update { state ->
            val updated = state.copy(departmentInput = value, successMessage = null)
            updated.copy(hasUnsavedChanges = checkUnsavedChanges(updated))
        }
    }

    fun saveChanges() {
        val state = _uiState.value
        if (state.nameError != null || state.emailError != null || state.fullNameInput.isBlank() || state.emailInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fix validation errors before saving.") }
            return
        }

        val updatedEmp = Employee(
            id = state.employee?.id ?: 1,
            fullName = state.fullNameInput,
            email = state.emailInput,
            phone = state.phoneInput,
            department = state.departmentInput
        )
        savedEmployee = updatedEmp
        _uiState.update {
            it.copy(
                employee = updatedEmp,
                hasUnsavedChanges = false,
                successMessage = "Changes saved successfully (in-memory).",
                errorMessage = null
            )
        }
    }

    fun cancelChanges() {
        val emp = savedEmployee ?: return
        _uiState.update {
            it.copy(
                fullNameInput = emp.fullName,
                emailInput = emp.email,
                phoneInput = emp.phone,
                departmentInput = emp.department,
                nameError = null,
                emailError = null,
                hasUnsavedChanges = false,
                errorMessage = null,
                successMessage = "Changes discarded. Restored last saved values."
            )
        }
    }

    fun refreshEmployee() {
        loadEmployee(forceRefresh = true)
    }

    private fun checkUnsavedChanges(state: EmployeeUiState): Boolean {
        val emp = savedEmployee ?: return false
        return state.fullNameInput != emp.fullName ||
                state.emailInput != emp.email ||
                state.phoneInput != emp.phone ||
                state.departmentInput != emp.department
    }
}
