package com.example.kotlinapp

import com.example.kotlinapp.data.dto.*
import com.example.kotlinapp.data.mapper.toDomain
import com.example.kotlinapp.data.mapper.toDto
import com.example.kotlinapp.domain.model.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EmployeeMapperTest : FunSpec({

    test("EmployeeCreate maps to EmployeeCreateDto correctly") {
        val domain = EmployeeCreate(
            employeeId = "EMP-001",
            username = "John Doe",
            email = "john@test.com",
            phone = "+1234567890",
            department = "IT",
            position = "Developer",
            location = "Office",
            hireDate = "2024-01-15",
            isActive = true,
            accessEnabled = true,
            photoBytes = byteArrayOf()
        )
        val dto = domain.toDto()
        dto.employee_id shouldBe "EMP-001"
        dto.username shouldBe "John Doe"
        dto.email shouldBe "john@test.com"
        dto.phone shouldBe "+1234567890"
        dto.department shouldBe "IT"
        dto.position shouldBe "Developer"
        dto.location shouldBe "Office"
        dto.hire_date shouldBe "2024-01-15"
        dto.is_active shouldBe true
        dto.access_enabled shouldBe true
    }

    test("EmployeeCreate with null optional fields maps correctly") {
        val domain = EmployeeCreate(
            employeeId = "EMP-002",
            username = "Jane",
            email = "jane@test.com",
            phone = null,
            department = null,
            position = null,
            location = null,
            hireDate = null,
            isActive = true,
            accessEnabled = true,
            photoBytes = byteArrayOf()
        )
        val dto = domain.toDto()
        dto.phone shouldBe null
        dto.department shouldBe null
        dto.position shouldBe null
    }

    test("EmployeeUpdate maps to EmployeeUpdateDto correctly") {
        val domain = EmployeeUpdate(department = "HR")
        val dto = domain.toDto()
        dto.department shouldBe "HR"
        dto.username shouldBe null
        dto.email shouldBe null
    }

    test("EmployeeResponseDto maps to Employee correctly") {
        val dto = EmployeeResponseDto(
            id = 1L,
            employee_id = "EMP-001",
            username = "John",
            email = "john@test.com",
            phone = "+1234567890",
            department = "IT",
            position = "Dev",
            location = "Office",
            hire_date = "2024-01-15",
            is_active = true,
            access_enabled = true,
            photo_path = "/photos/john.jpg",
            created_at = "2024-01-01"
        )
        val domain = dto.toDomain()
        domain.id shouldBe 1L
        domain.employeeId shouldBe "EMP-001"
        domain.username shouldBe "John"
        domain.phone shouldBe "+1234567890"
        domain.department shouldBe "IT"
    }

    test("EmployeeStatsDto maps to EmployeeStats correctly") {
        val dto = EmployeeStatsDto(total = 100, active = 80, inactive = 20)
        val domain = dto.toDomain()
        domain.total shouldBe 100
        domain.active shouldBe 80
        domain.inactive shouldBe 20
    }
})