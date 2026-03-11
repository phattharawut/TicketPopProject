package com.example.ticketpop.ui.auth

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.UserProfile
import com.example.ticketpop.data.model.UserStats
import com.example.ticketpop.data.repository.AuthRepository
import com.example.ticketpop.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.google.gson.Gson
import com.example.ticketpop.data.model.ApiResponse

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(SessionManager(application))

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = mutableStateOf<UserProfile?>(null)
    val currentUser: State<UserProfile?> = _currentUser

    private val _userStats = mutableStateOf<UserStats?>(null)
    val userStats: State<UserStats?> = _userStats

    init {
        val savedUser = repository.getSavedUser()
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
                val result = repository.login(email, password)
                _currentUser.value = result.user
                fetchUserStats(result.user.id)
                _authState.value = AuthState.Success(result.user)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ApiResponse::class.java).message
                } catch (ex: Exception) {
                    "รหัสผ่านไม่ถูกต้อง หรือ ไม่พบผู้ใช้งาน"
                }
                _authState.value = AuthState.Error(errorMessage)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "เชื่อมต่อไม่ได้")
            }
        }
    }

    fun register(fullName: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = repository.register(fullName, email, phone, password)
                _currentUser.value = result.user
                _authState.value = AuthState.Success(result.user)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ApiResponse::class.java).message
                } catch (ex: Exception) {
                    "สมัครสมาชิกไม่ได้ (400)"
                }
                _authState.value = AuthState.Error(errorMessage)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "สมัครสมาชิกไม่ได้")
            }
        }
    }

    fun fetchUserStats(userId: String) {
        viewModelScope.launch {
            try {
                _userStats.value = repository.getUserStats(userId)
            } catch (e: Exception) {
                _userStats.value = UserStats("0", "0", "0")
            }
        }
    }

    fun updateProfile(userId: String, fullName: String, phone: String) {
        viewModelScope.launch {
            try {
                val updated = repository.updateProfile(userId, fullName, phone)
                _currentUser.value = updated
            } catch (e: Exception) {
                _authState.value = AuthState.Error("อัปเดตไม่สำเร็จ: ${e.localizedMessage}")
            }
        }
    }

    fun changePassword(userId: String, oldPass: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (success, message) = repository.changePassword(userId, oldPass, newPass)
            onResult(success, message)
        }
    }

    fun logout() {
        _currentUser.value = null
        _userStats.value = null
        _authState.value = AuthState.Idle
        repository.logout()
    }
}
