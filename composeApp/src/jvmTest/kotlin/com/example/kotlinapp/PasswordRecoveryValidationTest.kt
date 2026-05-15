package com.example.kotlinapp

import com.example.kotlinapp.util.FormValidator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldContain

class PasswordRecoveryValidationTest : FunSpec({

    test("validateConfirmPassword_matchingPasswords_returnsNull") {
        val result = FormValidator.validateConfirmPassword("Test1234!", "Test1234!")
        result.shouldBeNull()
    }

    test("validateConfirmPassword_blankConfirm_returnsError") {
        val result = FormValidator.validateConfirmPassword("Test1234!", "")
        result.shouldNotBeNull()
        result shouldContain "Подтвердите"
    }

    test("validateConfirmPassword_mismatchedPasswords_returnsError") {
        val result = FormValidator.validateConfirmPassword("Test1234!", "Different123@")
        result.shouldNotBeNull()
        result shouldContain "не совпадают"
    }

    test("validateConfirmPassword_bothBlank_returnsError") {
        val result = FormValidator.validateConfirmPassword("", "")
        result.shouldNotBeNull()
    }

    test("validateConfirmPassword_caseSensitive_returnsError") {
        val result = FormValidator.validateConfirmPassword("Test1234!", "test1234!")
        result.shouldNotBeNull()
        result shouldContain "не совпадают"
    }
})