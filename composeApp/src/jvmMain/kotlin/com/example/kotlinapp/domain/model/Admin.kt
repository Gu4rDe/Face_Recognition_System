package com.example.kotlinapp.domain.model

data class Admin(
    val id: Long,
    val username: String,
    val email: String,
    val createdAt: String
)

data class AdminRegister(
    val username: String,
    val email: String,
    val password: String,
    val inviteCode: String
)

data class AdminLogin(
    val username: String,
    val password: String
)

data class AdminResetPassword(
    val username: String,
    val inviteCode: String,
    val newPassword: String
)