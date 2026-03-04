package com.example.ticketpop.ui.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.*
import com.example.ticketpop.data.remote.AuthApi
import com.example.ticketpop.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = mutableStateOf<UserProfile?>(null)
    val currentUser: State<UserProfile?> = _currentUser

    // สร้าง Retrofit instance แบบง่าย (ในโปรเจกต์จริงควรใช้ Dependency Injection เช่น Hilt)
    private val authApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL + "/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApi::class.java)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // เรียก API จริงจาก Server
                val response = authApi.login(LoginRequest(email, password))
                _currentUser.value = response.user
                _authState.value = AuthState.Success(response.user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "เข้าสู่ระบบไม่สำเร็จ")
            }
        }
    }

    fun register(fullName: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // เรียก API จริงจาก Server
                val response = authApi.register(RegisterRequest(fullName, email, phone, password))
                _currentUser.value = response.user
                _authState.value = AuthState.Success(response.user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "ลงทะเบียนไม่สำเร็จ")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }
}