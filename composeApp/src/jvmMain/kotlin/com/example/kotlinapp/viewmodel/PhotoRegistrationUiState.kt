package com.example.kotlinapp.viewmodel

data class PhotoRegistrationUiState(
    val capturedPhotos: Map<Int, ByteArray?> = emptyMap(),
    val currentStep: Int = 0,
    val totalSteps: Int = 5,
    val instruction: String = "",
    val isUploading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {
    val canUpload: Boolean
        get() = capturedPhotos.values.filterNotNull().size >= 3 && !isUploading

    val isOptionalStep: Boolean
        get() = currentStep >= 3

    val capturedCount: Int
        get() = capturedPhotos.values.filterNotNull().size
}
