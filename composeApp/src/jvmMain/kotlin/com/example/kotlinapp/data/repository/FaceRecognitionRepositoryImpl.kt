package com.example.kotlinapp.data.repository

import com.example.kotlinapp.data.mapper.toDomain
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.domain.model.FaceRecognitionResult
import com.example.kotlinapp.domain.repository.FaceRecognitionRepository

class FaceRecognitionRepositoryImpl(
    private val apiService: ApiService
) : FaceRecognitionRepository {

    override suspend fun recognizeFace(imageBytes: ByteArray): FaceRecognitionResult {
        return apiService.recognizeFace(imageBytes).toDomain()
    }
}