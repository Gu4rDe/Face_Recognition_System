package com.example.kotlinapp

import com.example.kotlinapp.data.remote.ApiClient
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.data.repository.*
import com.example.kotlinapp.domain.model.*
import com.example.kotlinapp.domain.repository.*
import org.junit.Test
import org.junit.Assert.*
import kotlinx.coroutines.runBlocking

class ServerCommunicationTest {

    companion object {
        private const val USERNAME = "test"
        private const val PASSWORD = "qq12345"
        private const val INITIAL_INVITE_CODE = "1234567890"
    }

    private val apiClient = ApiClient()
    private val apiService = ApiService(apiClient)

    private val authRepo: AuthRepository = AuthRepositoryImpl(apiService)
    private val inviteRepo: InviteCodeRepository = InviteCodeRepositoryImpl(apiService)
    private val employeeRepo: EmployeeRepository = EmployeeRepositoryImpl(apiService)
    private val faceRepo: FaceRecognitionRepository = FaceRecognitionRepositoryImpl(apiService)
    private val settingsRepo: SettingsRepository = SettingsRepositoryImpl(apiService)

    @Test
    fun testHealthCheck() = runBlocking {
        val result = apiService.healthCheck()
        println("Health check: $result")
        assertTrue("Server should be reachable at localhost:8000", result)
    }

    @Test
    fun testLoginAndGetMe() = runBlocking {
        val result = authRepo.login(AdminLogin(USERNAME, PASSWORD))
        println("Login result: accessToken=${result.accessToken}")
        assertNotNull(result.accessToken)

        val me = authRepo.getMe()
        println("GetMe result: id=${me.id}, username=${me.username}, email=${me.email}")
        assertNotNull(me.username)
    }

    @Test
    fun testFullWorkflow() = runBlocking {
        val authResult = authRepo.login(AdminLogin(USERNAME, PASSWORD))
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
        assertNotNull(savedToken)

        println("\nAll workflow steps completed successfully!")
    }

    @Test
    fun testRegisterAndLogin() = runBlocking {
        val newUsername = "test_user_${System.currentTimeMillis()}"
        val newAdmin = authRepo.register(
            AdminRegister(
                username = newUsername,
                email = "test_${System.currentTimeMillis()}@example.com",
                password = "test123456",
                inviteCode = INITIAL_INVITE_CODE
            )
        )
        println("Registered admin: id=${newAdmin.id}, username=${newAdmin.username}")

        val loginResult = authRepo.login(
            AdminLogin(newAdmin.username, "test123456")
        )
        println("New admin login token: ${loginResult.accessToken.take(20)}...")
        assertNotNull(loginResult.accessToken)
    }

    @Test
    fun testEmployeeCrud() = runBlocking {
        authRepo.login(AdminLogin(USERNAME, PASSWORD))

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

    @Test
    fun testSettingsAndBackup() = runBlocking {
        authRepo.login(AdminLogin(USERNAME, PASSWORD))

        val settings = settingsRepo.getSettings()
        println("Settings: theme=${settings.theme}, fullscreen=${settings.fullscreen}")

        val updated = settingsRepo.updateSettings(
            AppSettingsUpdate(matchThreshold = settings.matchThreshold)
        )
        println("Updated settings: matchThreshold=${updated.matchThreshold}")

        settingsRepo.createBackup()
        println("Backup created")
    }

    @Test
    fun testInviteCodes() = runBlocking {
        authRepo.login(AdminLogin(USERNAME, PASSWORD))

        val invite = inviteRepo.createInviteCode(InviteCodeCreate(expiresHours = 48))
        println("Created invite: code=${invite.code}, expires=${invite.expiresAt}")

        val list = inviteRepo.listInviteCodes()
        println("All invites: ${list.size}")

        inviteRepo.deleteInviteCode(invite.id)
        println("Deleted invite id=${invite.id}")
    }

    @Test
    fun testResetPassword() = runBlocking {
        val testUsername = "test_reset_${System.currentTimeMillis()}"
        val testEmail = "reset_${System.currentTimeMillis()}@example.com"
        val testPassword = "Test123456!"
        val newPassword = "NewPass789!"

        authRepo.login(AdminLogin(USERNAME, PASSWORD))
        val invite = inviteRepo.createInviteCode(InviteCodeCreate(expiresHours = 24))
        println("Created invite code for reset: ${invite.code}")

        val newAdmin = authRepo.register(
            AdminRegister(
                username = testUsername,
                email = testEmail,
                password = testPassword,
                inviteCode = invite.code
            )
        )
        println("Registered admin for reset test: ${newAdmin.username}")

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
        assertNotNull(resetMessage)

        val loginWithNewPassword = authRepo.login(AdminLogin(testUsername, newPassword))
        println("Login with new password successful: ${loginWithNewPassword.accessToken.take(20)}...")
        assertNotNull(loginWithNewPassword.accessToken)
    }
}