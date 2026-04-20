package com.example.kotlinapp.domain.repository

import com.example.kotlinapp.domain.model.Admin
import com.example.kotlinapp.domain.model.AdminLogin
import com.example.kotlinapp.domain.model.AdminRegister
import com.example.kotlinapp.domain.model.AuthResult

interface AuthRepository {
    suspend fun login(login: AdminLogin): AuthResult
    suspend fun register(register: AdminRegister): Admin
    suspend fun getMe(): Admin
    fun getToken(): String?
    fun setToken(token: String?)
}