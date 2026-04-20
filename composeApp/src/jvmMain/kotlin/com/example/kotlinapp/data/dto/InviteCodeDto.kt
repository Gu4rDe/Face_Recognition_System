package com.example.kotlinapp.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class InviteCodeCreateDto(
    val expires_hours: Int
)

@Serializable
data class InviteCodeResponseDto(
    val id: Long,
    val code: String,
    val created_by: Long? = null,
    val expires_at: String? = null,
    val is_used: Boolean,
    val created_at: String
)

@Serializable
data class InviteCodeListResponseDto(
    val codes: List<InviteCodeResponseDto>,
    val total: Int
)