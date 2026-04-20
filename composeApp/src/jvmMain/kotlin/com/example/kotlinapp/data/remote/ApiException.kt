package com.example.kotlinapp.data.remote

class ApiException(val code: Int, override val message: String) : Exception("API error $code: $message")

class NetworkException(override val message: String, override val cause: Throwable?) : Exception(message, cause)