package com.example.kotlinapp.data.repository

import com.example.kotlinapp.data.mapper.toDomain
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.domain.model.InviteCode
import com.example.kotlinapp.domain.model.InviteCodeCreate
import com.example.kotlinapp.domain.repository.InviteCodeRepository

class InviteCodeRepositoryImpl(
    private val apiService: ApiService
) : InviteCodeRepository {

    override suspend fun createInviteCode(create: InviteCodeCreate): InviteCode {
        return apiService.createInviteCode(create.expiresHours).toDomain()
    }

    override suspend fun listInviteCodes(): List<InviteCode> {
        return apiService.listInviteCodes().codes.map { it.toDomain() }
    }

    override suspend fun deleteInviteCode(id: Long) {
        apiService.deleteInviteCode(id)
    }
}