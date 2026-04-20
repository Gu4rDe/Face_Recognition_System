package com.example.kotlinapp.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeCreateDto(
    val employee_id: String,
    val username: String,
    val email: String,
    val phone: String? = null,
    val department: String? = null,
    val position: String? = null,
    val location: String? = null,
    val hire_date: String? = null,
    val is_active: Boolean = true,
    val access_enabled: Boolean = true
)

@Serializable
data class EmployeeUpdateDto(
    val employee_id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val department: String? = null,
    val position: String? = null,
    val location: String? = null,
    val hire_date: String? = null,
    val is_active: Boolean? = null,
    val access_enabled: Boolean? = null
)

@Serializable
data class EmployeeResponseDto(
    val id: Long,
    val employee_id: String = "",
    val username: String,
    val email: String = "",
    val phone: String? = null,
    val department: String? = null,
    val position: String? = null,
    val location: String? = null,
    val hire_date: String? = null,
    val is_active: Boolean = true,
    val access_enabled: Boolean = true,
    val photo_path: String? = null,
    val created_at: String
)

@Serializable
data class EmployeeStatsDto(
    val total: Int,
    val active: Int,
    val inactive: Int
)