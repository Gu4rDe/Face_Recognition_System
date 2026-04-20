package com.example.kotlinapp.data.mapper

import com.example.kotlinapp.data.dto.InviteCodeResponseDto
import com.example.kotlinapp.domain.model.InviteCode

fun InviteCodeResponseDto.toDomain() = InviteCode(
    id = id,
    code = code,
    createdBy = created_by,
    expiresAt = expires_at,
    isUsed = is_used,
    createdAt = created_at
)