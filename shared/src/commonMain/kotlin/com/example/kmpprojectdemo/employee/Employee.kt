package com.example.kmpprojectdemo.employee

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeResponse(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val company: Company? = null
)

@Serializable
data class Company(
    val department: String? = null
)

data class Employee(
    val id: Int,
    val fullName: String,
    val email: String,
    val phone: String,
    val department: String
)
