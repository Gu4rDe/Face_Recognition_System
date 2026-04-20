package com.example.kotlinapp.data.mapper

import com.example.kotlinapp.data.dto.AdminResponseDto
import com.example.kotlinapp.data.dto.LoginRequestDto
import com.example.kotlinapp.data.dto.RegisterRequestDto
import com.example.kotlinapp.data.dto.TokenResponseDto
import com.example.kotlinapp.domain.model.Admin
import com.example.kotlinapp.domain.model.AdminLogin
import com.example.kotlinapp.domain.model.AdminRegister
import com.example.kotlinapp.domain.model.AuthResult

fun AdminRegister.toDto() = RegisterRequestDto(
    username = username,
    email = email,
    password = password,
    invite_code = inviteCode
)

fun AdminLogin.toDto() = LoginRequestDto(
    username = username,
    password = password
)

fun TokenResponseDto.toDomain() = AuthResult(
    accessToken = access_token
)

fun AdminResponseDto.toDomain() = Admin(
    id = id,
    username = username,
    email = email,
    createdAt = created_at
)