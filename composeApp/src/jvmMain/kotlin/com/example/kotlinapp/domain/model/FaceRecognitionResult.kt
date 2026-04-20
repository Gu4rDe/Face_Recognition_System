package com.example.kotlinapp.domain.model

data class FaceRecognitionResult(
    val facesDetected: Int,
    val results: List<FaceResult>
)

data class FaceResult(
    val bbox: BoundingBox,
    val matches: List<FaceMatch>
)

data class BoundingBox(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

data class FaceMatch(
    val id: Long,
    val username: String,
    val similarity: Double
)