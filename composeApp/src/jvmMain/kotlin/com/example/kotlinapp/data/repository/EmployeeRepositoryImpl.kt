package com.example.kotlinapp.data.repository

import com.example.kotlinapp.data.mapper.toDomain
import com.example.kotlinapp.data.mapper.toDto
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.domain.model.Employee
import com.example.kotlinapp.domain.model.EmployeeCreate
import com.example.kotlinapp.domain.model.EmployeeStats
import com.example.kotlinapp.domain.model.EmployeeUpdate
import com.example.kotlinapp.domain.repository.EmployeeRepository

class EmployeeRepositoryImpl(
    private val apiService: ApiService
) : EmployeeRepository {

    override suspend fun createEmployee(create: EmployeeCreate): Employee {
        val dto = create.toDto()
        return apiService.createEmployee(dto, create.photoBytes).toDomain()
    }

    override suspend fun listEmployees(skip: Int, limit: Int): List<Employee> {
        return apiService.listEmployees(skip, limit).map { it.toDomain() }
    }

    override suspend fun searchEmployees(query: String): List<Employee> {
        return apiService.searchEmployees(query).map { it.toDomain() }
    }

    override suspend fun getEmployeeStats(): EmployeeStats {
        return apiService.getEmployeeStats().toDomain()
    }

    override suspend fun updateEmployee(id: Long, update: EmployeeUpdate): Employee {
        return apiService.updateEmployee(id, update.toDto()).toDomain()
    }

    override suspend fun deleteEmployee(id: Long) {
        apiService.deleteEmployee(id)
    }
}