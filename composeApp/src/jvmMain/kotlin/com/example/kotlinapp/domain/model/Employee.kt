package com.example.kotlinapp.domain.model

data class Employee(
    val id: Long,
    val employeeId: String,
    val username: String,
    val email: String,
    val phone: String?,
    val department: String?,
    val position: String?,
    val location: String?,
    val hireDate: String?,
    val isActive: Boolean,
    val accessEnabled: Boolean,
    val photoPath: String?,
    val createdAt: String
)

data class EmployeeCreate(
    val employeeId: String,
    val username: String,
    val email: String,
    val phone: String?,
    val department: String?,
    val position: String?,
    val location: String?,
    val hireDate: String?,
    val isActive: Boolean,
    val accessEnabled: Boolean,
    val photoBytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmployeeCreate) return false
        return employeeId == other.employeeId
    }

    override fun hashCode(): Int = employeeId.hashCode()
}

data class EmployeeUpdate(
    val employeeId: String? = null,
    val username: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val department: String? = null,
    val position: String? = null,
    val location: String? = null,
    val hireDate: String? = null,
    val isActive: Boolean? = null,
    val accessEnabled: Boolean? = null
)

data class EmployeeStats(
    val total: Int,
    val active: Int,
    val inactive: Int
)