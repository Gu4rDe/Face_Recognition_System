package com.example.kotlinapp

import com.example.kotlinapp.data.dto.*
import com.example.kotlinapp.data.mapper.toDomain
import com.example.kotlinapp.domain.model.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FaceRecognitionMapperTest : FunSpec({

    test("FaceRecognitionResponseDto maps to FaceRecognitionResult correctly") {
        val dto = FaceRecognitionResponseDto(
            faces_detected = 2,
            results = listOf(
                FaceResultDto(
                    bbox = listOf(10, 20, 100, 200),
                    matches = listOf(
                        FaceMatchDto(id = 1L, username = "john", similarity = 0.95)
                    )
                )
            )
        )
        val domain = dto.toDomain()
        domain.facesDetected shouldBe 2
        domain.results.size shouldBe 1
        domain.results[0].bbox.x shouldBe 10
        domain.results[0].bbox.y shouldBe 20
        domain.results[0].bbox.width shouldBe 100
        domain.results[0].bbox.height shouldBe 200
        domain.results[0].matches[0].username shouldBe "john"
        domain.results[0].matches[0].similarity shouldBe 0.95
    }

    test("FaceResultDto with empty bbox uses defaults") {
        val dto = FaceResultDto(
            bbox = emptyList(),
            matches = emptyList()
        )
        val domain = dto.toDomain()
        domain.bbox.x shouldBe 0
        domain.bbox.y shouldBe 0
        domain.bbox.width shouldBe 0
        domain.bbox.height shouldBe 0
        domain.matches.size shouldBe 0
    }

    test("FaceResultDto with partial bbox uses defaults for missing indices") {
        val dto = FaceResultDto(
            bbox = listOf(50, 60),
            matches = emptyList()
        )
        val domain = dto.toDomain()
        domain.bbox.x shouldBe 50
        domain.bbox.y shouldBe 60
        domain.bbox.width shouldBe 0
        domain.bbox.height shouldBe 0
    }
})