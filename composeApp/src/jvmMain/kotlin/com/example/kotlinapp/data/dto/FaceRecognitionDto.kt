package com.example.kotlinapp.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FaceRecognitionResponseDto(
    val faces_detected: Int,
    val results: List<FaceResultDto>
)

@Serializable
data class FaceResultDto(
    val bbox: List<Int>,
    val matches: List<FaceMatchDto>
)

@Serializable
data class FaceMatchDto(
    val id: Long,
    val username: String,
    val similarity: Double
)