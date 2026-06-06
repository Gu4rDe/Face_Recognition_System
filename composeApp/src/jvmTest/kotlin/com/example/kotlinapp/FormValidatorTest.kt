package com.example.kotlinapp

import com.example.kotlinapp.util.FormValidator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldContain

class FormValidatorTest : FunSpec({

    context("validateUsername") {
        test("blank username returns error") {
            FormValidator.validateUsername("").shouldNotBeNull()
        }

        test("short username returns error") {
            val result = FormValidator.validateUsername("ab")
            result.shouldNotBeNull()
            result shouldContain "минимум"
        }

        test("username with spaces returns error") {
            val result = FormValidator.validateUsername("user name")
            result.shouldNotBeNull()
            result shouldContain "пробелы"
        }

        test("username with special chars returns error") {
            val result = FormValidator.validateUsername("user@name")
            result.shouldNotBeNull()
            result shouldContain "латинские"
        }

        test("valid username returns null") {
            FormValidator.validateUsername("valid_user123").shouldBeNull()
        }

        test("username with underscores is valid") {
            FormValidator.validateUsername("my_user_name").shouldBeNull()
        }
    }

    context("validateEmail") {
        test("blank email returns error") {
            val result = FormValidator.validateEmail("")
            result.shouldNotBeNull()
        }

        test("email without @ returns error") {
            val result = FormValidator.validateEmail("userexample.com")
            result.shouldNotBeNull()
        }

        test("email without dot returns error") {
            val result = FormValidator.validateEmail("user@example")
            result.shouldNotBeNull()
        }

        test("valid email returns null") {
            FormValidator.validateEmail("user@example.com").shouldBeNull()
        }
    }

    context("validatePassword") {
        test("blank password returns error") {
            val result = FormValidator.validatePassword("")
            result.shouldNotBeNull()
        }

        test("short password returns error") {
            val result = FormValidator.validatePassword("Ab1!")
            result.shouldNotBeNull()
        }

        test("password without letter returns error") {
            val result = FormValidator.validatePassword("12345678!@")
            result.shouldNotBeNull()
        }

        test("password without digit returns error") {
            val result = FormValidator.validatePassword("abcdefgh!@")
            result.shouldNotBeNull()
        }

        test("password without special char returns error") {
            val result = FormValidator.validatePassword("Abcd12345")
            result.shouldNotBeNull()
        }

        test("valid password returns null") {
            FormValidator.validatePassword("Test1234!").shouldBeNull()
        }
    }

    context("validateInviteCode") {
        test("blank invite code returns error") {
            val result = FormValidator.validateInviteCode("")
            result.shouldNotBeNull()
        }

        test("non-blank invite code returns null") {
            FormValidator.validateInviteCode("ABC123").shouldBeNull()
        }
    }

    context("validateConfirmPassword") {
        test("matching passwords return null") {
            FormValidator.validateConfirmPassword("Test1234!", "Test1234!").shouldBeNull()
        }

        test("empty confirm returns error") {
            val result = FormValidator.validateConfirmPassword("Test1234!", "")
            result.shouldNotBeNull()
        }

        test("mismatched passwords return error") {
            val result = FormValidator.validateConfirmPassword("Test1234!", "Other5678@")
            result.shouldNotBeNull()
        }
    }
})