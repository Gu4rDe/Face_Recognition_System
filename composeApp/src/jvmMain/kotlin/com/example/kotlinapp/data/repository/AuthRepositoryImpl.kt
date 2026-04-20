package com.example.kotlinapp.data.repository

import com.example.kotlinapp.data.mapper.toDomain
import com.example.kotlinapp.data.mapper.toDto
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.domain.model.Admin
import com.example.kotlinapp.domain.model.AdminRegister
import com.example.kotlinapp.domain.model.AuthResult
import com.example.kotlinapp.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun login(login: com.example.kotlinapp.domain.model.AdminLogin): AuthResult {
        return apiService.login(login.username, login.password).toDomain().also {
            apiService.apiClient.token = it.accessToken
        }
    }

    override suspend fun register(register: AdminRegister): Admin {
        val admin = apiService.register(
            register.username,
            register.email,
            register.password,
            register.inviteCode
        ).toDomain()
        try {
            val tokenResult = apiService.login(register.username, register.password).toDomain()
            apiService.apiClient.token = tokenResult.accessToken
        } catch (_: Exception) {
            // Registration succeeded but auto-login failed — user will need to log in manually
        }
        return admin
    }

    override suspend fun getMe(): Admin {
        return apiService.getMe().toDomain()
    }

    override fun getToken(): String? {
        return apiService.apiClient.token
    }

    override fun setToken(token: String?) {
        apiService.apiClient.token = token
    }
}