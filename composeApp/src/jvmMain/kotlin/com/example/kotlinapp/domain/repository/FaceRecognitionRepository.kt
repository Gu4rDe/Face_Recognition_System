package com.example.kotlinapp.domain.repository

import com.example.kotlinapp.domain.model.FaceRecognitionResult

interface FaceRecognitionRepository {
    suspend fun recognizeFace(imageBytes: ByteArray): FaceRecognitionResult
}