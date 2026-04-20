package com.example.kotlinapp.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    val username: String,
    val email: String,
    val password: String,
    val invite_code: String
)

@Serializable
data class TokenResponseDto(
    val access_token: String,
    val token_type: String = "bearer"
)

@Serializable
data class AdminResponseDto(
    val id: Long,
    val username: String,
    val email: String,
    val created_at: String
)