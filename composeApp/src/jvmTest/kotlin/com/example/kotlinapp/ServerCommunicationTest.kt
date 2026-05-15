package com.example.kotlinapp

import com.example.kotlinapp.data.remote.ApiClient
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.data.repository.AuthRepositoryImpl
import com.example.kotlinapp.data.repository.EmployeeRepositoryImpl
import com.example.kotlinapp.data.repository.FaceRecognitionRepositoryImpl
import com.example.kotlinapp.data.repository.InviteCodeRepositoryImpl
import com.example.kotlinapp.data.repository.SettingsRepositoryImpl
import com.example.kotlinapp.domain.model.AdminLogin
import com.example.kotlinapp.domain.model.AdminRegister
import com.example.kotlinapp.domain.model.AdminResetPassword
import com.example.kotlinapp.domain.model.AppSettingsUpdate
import com.example.kotlinapp.domain.model.EmployeeCreate
import com.example.kotlinapp.domain.model.EmployeeUpdate
import com.example.kotlinapp.domain.model.InviteCodeCreate
import com.example.kotlinapp.domain.repository.AuthRepository
import com.example.kotlinapp.domain.repository.EmployeeRepository
import com.example.kotlinapp.domain.repository.FaceRecognitionRepository
import com.example.kotlinapp.domain.repository.InviteCodeRepository
import com.example.kotlinapp.domain.repository.SettingsRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.runBlocking

class ServerCommunicationTest : FunSpec({

    val apiClient = ApiClient()
    val apiService = ApiService(apiClient)

    val authRepo: AuthRepository = AuthRepositoryImpl(apiService)
    val inviteRepo: InviteCodeRepository = InviteCodeRepositoryImpl(apiService)
    val employeeRepo: EmployeeRepository = EmployeeRepositoryImpl(apiService)
    val faceRepo: FaceRecognitionRepository = FaceRecognitionRepositoryImpl(apiService)
    val settingsRepo: SettingsRepository = SettingsRepositoryImpl(apiService)

    test("healthCheck should reach server") {
        runBlocking {
            val result = apiService.healthCheck()
            println("Health check: $result")
            result shouldBe true
        }
    }

    test("loginAndGetMe should authenticate and return admin info") {
        runBlocking {
            val result = authRepo.login(AdminLogin("test", "qq12345"))
            println("Login result: accessToken=${result.accessToken}")
            result.accessToken shouldNotBe ""

            val me = authRepo.getMe()
            println("GetMe result: id=${me.id}, username=${me.username}, email=${me.email}")
            me.username shouldNotBe ""
        }
    }

    test("fullWorkflow should complete all steps") {
        runBlocking {
            val authResult = authRepo.login(AdminLogin("test", "qq12345"))
            println("[1] Login: ${authResult.accessToken.take(20)}...")

            val me = authRepo.getMe()
            println("[2] Me: username=${me.username}, email=${me.email}")

            val invite = inviteRepo.createInviteCode(InviteCodeCreate(expiresHours = 24))
            println("[3] Invite code: ${invite.code}, expires=${invite.expiresAt}")

            val invites = inviteRepo.listInviteCodes()
            println("[4] Invite codes count: ${invites.size}")

            val stats = employeeRepo.getEmployeeStats()
            println("[5] Employee stats: total=${stats.total}, active=${stats.active}, inactive=${stats.inactive}")

            val employees = employeeRepo.listEmployees()
            println("[6] Employees count: ${employees.size}")

            val search = employeeRepo.searchEmployees("test")
            println("[7] Search results: ${search.size}")

            val settings = settingsRepo.getSettings()
            println("[8] Settings: theme=${settings.theme}, matchThreshold=${settings.matchThreshold}")

            val savedToken = authRepo.getToken()
            println("[9] Saved token: ${savedToken?.take(20)}...")
            savedToken.shouldNotBeNull()

            println("\nAll workflow steps completed successfully!")
        }
    }

    test("registerAndLogin should create new admin and login") {
        runBlocking {
            val newUsername = "test_user_${System.currentTimeMillis()}"
            val newAdmin = authRepo.register(
                AdminRegister(
                    username = newUsername,
                    email = "test_${System.currentTimeMillis()}@example.com",
                    password = "test123456",
                    inviteCode = "1234567890"
                )
            )
            println("Registered admin: id=${newAdmin.id}, username=${newAdmin.username}")

            val loginResult = authRepo.login(AdminLogin(newAdmin.username, "test123456"))
            println("New admin login token: ${loginResult.accessToken.take(20)}...")
            loginResult.accessToken shouldNotBe ""
        }
    }

    test("employeeCrud should create, read, update, and delete employee") {
        runBlocking {
            authRepo.login(AdminLogin("test", "qq12345"))

            val employee = employeeRepo.createEmployee(
                EmployeeCreate(
                    employeeId = "EMP-${System.currentTimeMillis()}",
                    username = "Test Employee",
                    email = "emp_${System.currentTimeMillis()}@test.com",
                    phone = "+1234567890",
                    department = "IT",
                    position = "Developer",
                    location = "Office",
                    hireDate = "2024-01-15",
                    isActive = true,
                    accessEnabled = true,
                    photoBytes = byteArrayOf()
                )
            )
            println("Created employee: id=${employee.id}, name=${employee.username}")

            val list = employeeRepo.listEmployees()
            println("Employees: ${list.size}")

            val stats = employeeRepo.getEmployeeStats()
            println("Stats: total=${stats.total}")

            val updated = employeeRepo.updateEmployee(
                employee.id,
                EmployeeUpdate(department = "HR")
            )
            println("Updated employee: department=${updated.department}")

            employeeRepo.deleteEmployee(employee.id)
            println("Deleted employee id=${employee.id}")
        }
    }

    test("settingsAndBackup should get and update settings") {
        runBlocking {
            authRepo.login(AdminLogin("test", "qq12345"))

            val settings = settingsRepo.getSettings()
            println("Settings: theme=${settings.theme}, fullscreen=${settings.fullscreen}")

            val updated = settingsRepo.updateSettings(
                AppSettingsUpdate(matchThreshold = settings.matchThreshold)
            )
            println("Updated settings: matchThreshold=${updated.matchThreshold}")

            settingsRepo.createBackup()
            println("Backup created")
        }
    }

    test("inviteCodes should create, list, and delete") {
        runBlocking {
            authRepo.login(AdminLogin("test", "qq12345"))

            val invite = inviteRepo.createInviteCode(InviteCodeCreate(expiresHours = 48))
            println("Created invite: code=${invite.code}, expires=${invite.expiresAt}")

            val list = inviteRepo.listInviteCodes()
            println("All invites: ${list.size}")

            inviteRepo.deleteInviteCode(invite.id)
            println("Deleted invite id=${invite.id}")
        }
    }

    test("resetPassword should reset and allow new password") {
        runBlocking {
            val testUsername = "test_reset_${System.currentTimeMillis()}"
            val testEmail = "reset_${System.currentTimeMillis()}@example.com"
            val testPassword = "Test123456!"
            val newPassword = "NewPass789!"

            authRepo.login(AdminLogin("test", "qq12345"))
            val invite = inviteRepo.createInviteCode(InviteCodeCreate(expiresHours = 24))
            println("Created invite code for reset: ${invite.code}")

            authRepo.register(
                AdminRegister(
                    username = testUsername,
                    email = testEmail,
                    password = testPassword,
                    inviteCode = invite.code
                )
            )
            println("Registered admin for reset test: $testUsername")

            val secondInvite = inviteRepo.createInviteCode(InviteCodeCreate(expiresHours = 24))
            println("Created second invite code for reset: ${secondInvite.code}")

            val resetMessage = authRepo.resetPassword(
                AdminResetPassword(
                    username = testUsername,
                    inviteCode = secondInvite.code,
                    newPassword = newPassword
                )
            )
            println("Reset password message: $resetMessage")
            resetMessage shouldNotBe ""

            val loginWithNewPassword = authRepo.login(AdminLogin(testUsername, newPassword))
            println("Login with new password successful: ${loginWithNewPassword.accessToken.take(20)}...")
            loginWithNewPassword.accessToken shouldNotBe ""
        }
    }
})