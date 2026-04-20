package com.example.kotlinapp.domain.model

data class InviteCode(
    val id: Long,
    val code: String,
    val createdBy: Long?,
    val expiresAt: String?,
    val isUsed: Boolean,
    val createdAt: String
)

data class InviteCodeCreate(
    val expiresHours: Int = 24
)