package com.example.kotlinapp

import com.example.kotlinapp.util.FormValidator
import org.junit.Test
import org.junit.Assert.*

class PasswordRecoveryValidationTest {

    @Test
    fun validateConfirmPassword_matchingPasswords_returnsNull() {
        val result = FormValidator.validateConfirmPassword("Test1234!", "Test1234!")
        assertNull(result)
    }

    @Test
    fun validateConfirmPassword_blankConfirm_returnsError() {
        val result = FormValidator.validateConfirmPassword("Test1234!", "")
        assertNotNull(result)
        assertTrue(result!!.contains("Подтвердите"))
    }

    @Test
    fun validateConfirmPassword_mismatchedPasswords_returnsError() {
        val result = FormValidator.validateConfirmPassword("Test1234!", "Different123@")
        assertNotNull(result)
        assertTrue(result!!.contains("не совпадают"))
    }

    @Test
    fun validateConfirmPassword_bothBlank_returnsError() {
        val result = FormValidator.validateConfirmPassword("", "")
        assertNotNull(result)
    }

    @Test
    fun validateConfirmPassword_caseSensitive_returnsError() {
        val result = FormValidator.validateConfirmPassword("Test1234!", "test1234!")
        assertNotNull(result)
        assertTrue(result!!.contains("не совпадают"))
    }
}
