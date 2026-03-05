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
import retrofit2.HttpException
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

    // ข้อมูลสถิติ (ตั๋ว, คะแนน, ประวัติ)
    private val _userStats = mutableStateOf<UserStats?>(null)
    val userStats: State<UserStats?> = _userStats

    private val authApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL + "/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApi::class.java)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authApi.login(LoginRequest(email, password))
                _currentUser.value = response.user
                fetchUserStats(response.user.id)
                _authState.value = AuthState.Success(response.user)
            } catch (e: HttpException) {
                _authState.value = AuthState.Error(if (e.code() == 401) "อีเมลหรือรหัสผ่านไม่ถูกต้อง" else "เข้าสู่ระบบไม่สำเร็จ")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("เชื่อมต่อเซิร์ฟเวอร์ไม่ได้")
            }
        }
    }

    fun register(fullName: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authApi.register(RegisterRequest(fullName, email, phone, password))
                _currentUser.value = response.user
                fetchUserStats(response.user.id)
                _authState.value = AuthState.Success(response.user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("ลงทะเบียนไม่สำเร็จ")
            }
        }
    }

    fun fetchUserStats(userId: String) {
        viewModelScope.launch {
            try {
                val stats = authApi.getUserStats(userId)
                _userStats.value = stats
            } catch (e: Exception) {
                _userStats.value = UserStats("0", "0", "0")
            }
        }
    }

    fun updateProfile(userId: String, fullName: String, phone: String) {
        viewModelScope.launch {
            try {
                val updatedUser = authApi.updateProfile(UpdateProfileRequest(userId, fullName, phone))
                _currentUser.value = updatedUser
            } catch (e: Exception) {
                _authState.value = AuthState.Error("อัปเดตโปรไฟล์ไม่สำเร็จ")
            }
        }
    }

    // ฟังก์ชันใหม่: เปลี่ยนรหัสผ่าน
    fun changePassword(userId: String, oldPass: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authApi.changePassword(ChangePasswordRequest(userId, oldPass, newPass))
                if (response.success) {
                    onResult(true, "เปลี่ยนรหัสผ่านสำเร็จ")
                } else {
                    onResult(false, response.message)
                }
            } catch (e: HttpException) {
                onResult(false, if (e.code() == 400) "รหัสผ่านเดิมไม่ถูกต้อง" else "เกิดข้อผิดพลาดที่เซิร์ฟเวอร์")
            } catch (e: Exception) {
                onResult(false, "ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _userStats.value = null
        _authState.value = AuthState.Idle
    }
}