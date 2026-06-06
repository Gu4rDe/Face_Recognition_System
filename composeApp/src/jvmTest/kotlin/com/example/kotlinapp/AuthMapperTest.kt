package com.example.kotlinapp

import com.example.kotlinapp.data.dto.*
import com.example.kotlinapp.data.mapper.toDomain
import com.example.kotlinapp.data.mapper.toDto
import com.example.kotlinapp.domain.model.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldNotBeNull

class AuthMapperTest : FunSpec({

    test("AdminRegister maps to RegisterRequestDto correctly") {
        val domain = AdminRegister(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            inviteCode = "CODE123"
        )
        val dto = domain.toDto()
        dto.username shouldBe "testuser"
        dto.email shouldBe "test@example.com"
        dto.password shouldBe "password123"
        dto.invite_code shouldBe "CODE123"
    }

    test("AdminLogin maps to LoginRequestDto correctly") {
        val domain = AdminLogin(username = "admin", password = "pass123")
        val dto = domain.toDto()
        dto.username shouldBe "admin"
        dto.password shouldBe "pass123"
    }

    test("AdminResetPassword maps to ResetPasswordRequestDto correctly") {
        val domain = AdminResetPassword(
            username = "user1",
            inviteCode = "INV123",
            newPassword = "newpass456"
        )
        val dto = domain.toDto()
        dto.username shouldBe "user1"
        dto.invite_code shouldBe "INV123"
        dto.new_password shouldBe "newpass456"
    }

    test("TokenResponseDto maps to AuthResult correctly") {
        val dto = TokenResponseDto(access_token = "abc123", token_type = "bearer")
        val domain = dto.toDomain()
        domain.accessToken shouldBe "abc123"
    }

    test("AdminResponseDto maps to Admin correctly") {
        val dto = AdminResponseDto(
            id = 1L,
            username = "admin",
            email = "admin@test.com",
            created_at = "2024-01-01"
        )
        val domain = dto.toDomain()
        domain.id shouldBe 1L
        domain.username shouldBe "admin"
        domain.email shouldBe "admin@test.com"
        domain.createdAt shouldBe "2024-01-01"
    }
})