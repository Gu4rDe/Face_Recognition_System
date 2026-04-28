package com.example.kotlinapp.data.remote

import com.example.kotlinapp.data.dto.*
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException

class ApiService(val apiClient: ApiClient) {

    private val client get() = apiClient.client

    private suspend inline fun <reified T> safeCall(block: () -> T): T {
        return try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: ClientRequestException) {
            throw ApiException(e.response.status.value, e.response.bodyAsText())
        } catch (e: ServerResponseException) {
            throw ApiException(e.response.status.value, e.response.bodyAsText())
        } catch (e: ApiException) {
            throw e
        } catch (e: NetworkException) {
            throw e
        } catch (e: Exception) {
            throw NetworkException(e.message ?: "Ошибка сети", e)
        }
    }

    // Auth
    suspend fun login(username: String, password: String): TokenResponseDto {
        return safeCall {
            client.post("/api/v1/admins/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(username, password))
            }.body()
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        inviteCode: String
    ): AdminResponseDto {
        return safeCall {
            client.post("/api/v1/admins/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequestDto(username, email, password, inviteCode))
            }.body()
        }
    }

    suspend fun getMe(): AdminResponseDto {
        return safeCall {
            client.get("/api/v1/admins/me") {
                addAuthToken(apiClient.token)
            }.body()
        }
    }

    suspend fun resetPassword(username: String, inviteCode: String, newPassword: String): ResetPasswordResponseDto {
        return safeCall {
            client.post("/api/v1/admins/reset-password") {
                contentType(ContentType.Application.Json)
                setBody(ResetPasswordRequestDto(username, inviteCode, newPassword))
            }.body()
        }
    }

    // Invite Codes
    suspend fun createInviteCode(expiresHours: Int): InviteCodeResponseDto {
        return safeCall {
            client.post("/api/v1/admin/invites") {
                addAuthToken(apiClient.token)
                contentType(ContentType.Application.Json)
                setBody(InviteCodeCreateDto(expires_hours = expiresHours))
            }.body()
        }
    }

    suspend fun listInviteCodes(): InviteCodeListResponseDto {
        return safeCall {
            client.get("/api/v1/admin/invites") {
                addAuthToken(apiClient.token)
            }.body()
        }
    }

    suspend fun deleteInviteCode(id: Long) {
        safeCall {
            client.delete("/api/v1/admin/invites/$id") {
                addAuthToken(apiClient.token)
            }
        }
    }

    // Employees
    suspend fun createEmployee(
        employeeDto: EmployeeCreateDto,
        imageBytes: ByteArray
    ): EmployeeResponseDto {
        return safeCall {
            client.post("/api/v1/employees/register") {
                addAuthToken(apiClient.token)
                setBody(MultiPartFormDataContent(formData {
                    append("employee_id", employeeDto.employee_id)
                    append("username", employeeDto.username)
                    append("email", employeeDto.email)
                    employeeDto.phone?.let { append("phone", it) }
                    employeeDto.department?.let { append("department", it) }
                    employeeDto.position?.let { append("position", it) }
                    employeeDto.location?.let { append("location", it) }
                    employeeDto.hire_date?.let { append("hire_date", it) }
                    append("is_active", employeeDto.is_active.toString())
                    append("access_enabled", employeeDto.access_enabled.toString())
                    append("file", imageBytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=photo.jpg")
                        append(HttpHeaders.ContentType, "image/jpeg")
                    })
                }))
            }.body()
        }
    }

    suspend fun listEmployees(skip: Int = 0, limit: Int = 100): List<EmployeeResponseDto> {
        return safeCall {
            client.get("/api/v1/employees") {
                addAuthToken(apiClient.token)
                parameter("skip", skip)
                parameter("limit", limit)
            }.body()
        }
    }

    suspend fun searchEmployees(query: String): List<EmployeeResponseDto> {
        return safeCall {
            client.get("/api/v1/employees/search") {
                addAuthToken(apiClient.token)
                parameter("q", query)
            }.body()
        }
    }

    suspend fun getEmployeeStats(): EmployeeStatsDto {
        return safeCall {
            client.get("/api/v1/employees/stats") {
                addAuthToken(apiClient.token)
            }.body()
        }
    }

    suspend fun updateEmployee(id: Long, update: EmployeeUpdateDto): EmployeeResponseDto {
        return safeCall {
            client.put("/api/v1/employees/$id") {
                addAuthToken(apiClient.token)
                contentType(ContentType.Application.Json)
                setBody(update)
            }.body()
        }
    }

    suspend fun deleteEmployee(id: Long) {
        safeCall {
            client.delete("/api/v1/employees/$id") {
                addAuthToken(apiClient.token)
            }
        }
    }

    // Face Recognition
    suspend fun recognizeFace(imageBytes: ByteArray): FaceRecognitionResponseDto {
        return safeCall {
            client.post("/api/v1/faces/recognize") {
                addAuthToken(apiClient.token)
                setBody(MultiPartFormDataContent(formData {
                    append("file", imageBytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=face.jpg")
                        append(HttpHeaders.ContentType, "image/jpeg")
                    })
                }))
            }.body()
        }
    }

    // Settings
    suspend fun getSettings(): SettingsResponseDto {
        return safeCall {
            client.get("/api/v1/settings") {
                addAuthToken(apiClient.token)
            }.body()
        }
    }

    suspend fun updateSettings(update: SettingsUpdateDto): SettingsResponseDto {
        return safeCall {
            client.put("/api/v1/settings") {
                addAuthToken(apiClient.token)
                contentType(ContentType.Application.Json)
                setBody(update)
            }.body()
        }
    }

    suspend fun createBackup() {
        safeCall {
            client.post("/api/v1/settings/backup") {
                addAuthToken(apiClient.token)
            }
        }
    }

    // Health
    suspend fun healthCheck(): Boolean {
        return try {
            val response = client.get("/health")
            val status = response.status.value in 200..299
            response.bodyAsText()
            status
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        }
    }
}