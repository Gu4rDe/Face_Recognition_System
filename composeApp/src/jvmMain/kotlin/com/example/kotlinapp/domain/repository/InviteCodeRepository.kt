package com.example.kotlinapp.domain.repository

import com.example.kotlinapp.domain.model.InviteCode
import com.example.kotlinapp.domain.model.InviteCodeCreate

interface InviteCodeRepository {
    suspend fun createInviteCode(create: InviteCodeCreate): InviteCode
    suspend fun listInviteCodes(): List<InviteCode>
    suspend fun deleteInviteCode(id: Long)
}