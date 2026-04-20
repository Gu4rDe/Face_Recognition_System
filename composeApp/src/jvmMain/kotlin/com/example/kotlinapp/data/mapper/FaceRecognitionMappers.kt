package com.example.kotlinapp.data.mapper

import com.example.kotlinapp.data.dto.FaceMatchDto
import com.example.kotlinapp.data.dto.FaceRecognitionResponseDto
import com.example.kotlinapp.data.dto.FaceResultDto
import com.example.kotlinapp.domain.model.BoundingBox
import com.example.kotlinapp.domain.model.FaceMatch
import com.example.kotlinapp.domain.model.FaceRecognitionResult
import com.example.kotlinapp.domain.model.FaceResult

fun FaceRecognitionResponseDto.toDomain() = FaceRecognitionResult(
    facesDetected = faces_detected,
    results = results.map { it.toDomain() }
)

fun FaceResultDto.toDomain() = FaceResult(
    bbox = BoundingBox(
        x = bbox.getOrElse(0) { 0 },
        y = bbox.getOrElse(1) { 0 },
        width = bbox.getOrElse(2) { 0 },
        height = bbox.getOrElse(3) { 0 }
    ),
    matches = matches.map { it.toDomain() }
)

fun FaceMatchDto.toDomain() = FaceMatch(
    id = id,
    username = username,
    similarity = similarity
)