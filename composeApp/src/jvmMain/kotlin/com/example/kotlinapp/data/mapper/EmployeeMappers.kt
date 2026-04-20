package com.example.kotlinapp.data.mapper

import com.example.kotlinapp.data.dto.EmployeeCreateDto
import com.example.kotlinapp.data.dto.EmployeeResponseDto
import com.example.kotlinapp.data.dto.EmployeeStatsDto
import com.example.kotlinapp.data.dto.EmployeeUpdateDto
import com.example.kotlinapp.domain.model.Employee
import com.example.kotlinapp.domain.model.EmployeeCreate
import com.example.kotlinapp.domain.model.EmployeeStats
import com.example.kotlinapp.domain.model.EmployeeUpdate

fun EmployeeCreate.toDto() = EmployeeCreateDto(
    employee_id = employeeId,
    username = username,
    email = email,
    phone = phone,
    department = department,
    position = position,
    location = location,
    hire_date = hireDate,
    is_active = isActive,
    access_enabled = accessEnabled
)

fun EmployeeUpdate.toDto() = EmployeeUpdateDto(
    employee_id = employeeId,
    username = username,
    email = email,
    phone = phone,
    department = department,
    position = position,
    location = location,
    hire_date = hireDate,
    is_active = isActive,
    access_enabled = accessEnabled
)

fun EmployeeResponseDto.toDomain() = Employee(
    id = id,
    employeeId = employee_id,
    username = username,
    email = email,
    phone = phone,
    department = department,
    position = position,
    location = location,
    hireDate = hire_date,
    isActive = is_active,
    accessEnabled = access_enabled,
    photoPath = photo_path,
    createdAt = created_at
)

fun EmployeeStatsDto.toDomain() = EmployeeStats(
    total = total,
    active = active,
    inactive = inactive
)