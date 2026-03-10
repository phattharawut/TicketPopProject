package com.example.ticketpop.ui.auth

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.*
import com.example.ticketpop.data.remote.AuthApi
import com.example.ticketpop.utils.Constants
import com.example.ticketpop.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val session = SessionManager(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = mutableStateOf<UserProfile?>(null)
    val currentUser: State<UserProfile?> = _currentUser

    private val _userStats = mutableStateOf<UserStats?>(null)
    val userStats: State<UserStats?> = _userStats

    // สร้าง Retrofit instance
    private val authApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApi::class.java)

    init {
        // โหลด User จาก Session ที่บันทึกไว้ (ถ้ามี)
        val savedUser = session.getUser()
        if (savedUser != null) {
            _currentUser.value = savedUser
            _authState.value = AuthState.Success(savedUser)
            fetchUserStats(savedUser.id)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authApi.login(LoginRequest(email, password))
                if (response.success && response.data != null) {
                    val user = response.data.user
                    _currentUser.value = user
                    session.saveUser(user, response.data.token)
                    fetchUserStats(user.id)
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error(response.message)
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ApiResponse::class.java).message
                } catch (ex: Exception) {
                    "รหัสผ่านไม่ถูกต้อง หรือ ไม่พบผู้ใช้งาน"
                }
                _authState.value = AuthState.Error(errorMessage)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("เชื่อมต่อไม่ได้: ${e.localizedMessage}")
            }
        }
    }

    fun register(fullName: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authApi.register(RegisterRequest(fullName, email, phone, password))
                if (response.success && response.data != null) {
                    val user = response.data.user
                    _currentUser.value = user
                    session.saveUser(user, response.data.token)
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error(response.message)
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ApiResponse::class.java).message
                } catch (ex: Exception) {
                    "สมัครสมาชิกไม่ได้ (400)"
                }
                _authState.value = AuthState.Error(errorMessage)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("สมัครสมาชิกไม่ได้: ${e.localizedMessage}")
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
                val response = authApi.updateProfile(UpdateProfileRequest(userId, fullName, phone))
                if (response.success && response.data != null) {
                    _currentUser.value = response.data
                    session.saveUser(response.data, session.getToken())
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("อัปเดตไม่สำเร็จ: ${e.localizedMessage}")
            }
        }
    }

    fun changePassword(userId: String, oldPass: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authApi.changePassword(ChangePasswordRequest(userId, oldPass, newPass))
                onResult(response.success, response.message)
            } catch (e: Exception) {
                onResult(false, "เปลี่ยนรหัสผ่านไม่ได้: ${e.localizedMessage}")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _userStats.value = null
        _authState.value = AuthState.Idle
        session.clearSession()
    }
}