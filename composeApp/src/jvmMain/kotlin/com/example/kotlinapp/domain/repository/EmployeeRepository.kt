package com.example.kotlinapp.domain.repository

import com.example.kotlinapp.domain.model.Employee
import com.example.kotlinapp.domain.model.EmployeeCreate
import com.example.kotlinapp.domain.model.EmployeeStats
import com.example.kotlinapp.domain.model.EmployeeUpdate

interface EmployeeRepository {
    suspend fun createEmployee(create: EmployeeCreate): Employee
    suspend fun listEmployees(skip: Int = 0, limit: Int = 100): List<Employee>
    suspend fun searchEmployees(query: String): List<Employee>
    suspend fun getEmployeeStats(): EmployeeStats
    suspend fun updateEmployee(id: Long, update: EmployeeUpdate): Employee
    suspend fun deleteEmployee(id: Long)
}