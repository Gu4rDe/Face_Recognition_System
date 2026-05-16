package com.example.kotlinapp.di

import com.example.kotlinapp.data.local.LocalSettingsStorage
import com.example.kotlinapp.data.remote.ApiClient
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.data.repository.AuthRepositoryImpl
import com.example.kotlinapp.data.repository.EmployeeRepositoryImpl
import com.example.kotlinapp.data.repository.FaceRecognitionRepositoryImpl
import com.example.kotlinapp.data.repository.InviteCodeRepositoryImpl
import com.example.kotlinapp.data.repository.SettingsRepositoryImpl
import com.example.kotlinapp.domain.repository.AuthRepository
import com.example.kotlinapp.domain.repository.EmployeeRepository
import com.example.kotlinapp.domain.repository.FaceRecognitionRepository
import com.example.kotlinapp.domain.repository.InviteCodeRepository
import com.example.kotlinapp.domain.repository.SettingsRepository
import com.example.kotlinapp.service.WebcamService
import com.example.kotlinapp.viewmodel.DashboardViewModel
import com.example.kotlinapp.viewmodel.EmployeeViewModel
import com.example.kotlinapp.viewmodel.FaceRecognitionViewModel
import com.example.kotlinapp.viewmodel.LoginViewModel
import com.example.kotlinapp.viewmodel.PasswordRecoveryViewModel
import com.example.kotlinapp.viewmodel.RegisterViewModel
import com.example.kotlinapp.viewmodel.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val networkModule: Module = module {
    single { ApiClient(LocalSettingsStorage.getApiUrl()) }
}

val infrastructureModule: Module = module {
    single { ApiService(get()) }
}

val repositoryModule: Module = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<EmployeeRepository> { EmployeeRepositoryImpl(get()) }
    single<InviteCodeRepository> { InviteCodeRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<FaceRecognitionRepository> { FaceRecognitionRepositoryImpl(get()) }
    single { WebcamService() }
}

val viewModelModule: Module = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { PasswordRecoveryViewModel(get()) }
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { EmployeeViewModel(get()) }
    viewModel { FaceRecognitionViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
}
