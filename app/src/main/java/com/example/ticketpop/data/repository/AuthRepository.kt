package com.example.ticketpop.data.repository

import com.example.ticketpop.data.model.*
import com.example.ticketpop.data.remote.ApiClient
import com.example.ticketpop.utils.SessionManager

class AuthRepository(private val session: SessionManager) {

    private val authApi = ApiClient.authApi

    suspend fun login(email: String, password: String): AuthResponse {
        val response = authApi.login(LoginRequest(email, password))
        if (response.success && response.data != null) {
            session.saveUser(response.data.user, response.data.token)
            return response.data
        }
        throw Exception(response.message)
    }

    suspend fun register(
        fullName: String,
        email: String,
        phone: String,
        password: String
    ): AuthResponse {
        val response = authApi.register(RegisterRequest(fullName, email, phone, password))
        if (response.success && response.data != null) {
            session.saveUser(response.data.user, response.data.token)
            return response.data
        }
        throw Exception(response.message)
    }

    suspend fun getUserStats(userId: String): UserStats {
        return authApi.getUserStats(userId)
    }

    suspend fun updateProfile(userId: String, fullName: String, phone: String): UserProfile {
        val response = authApi.updateProfile(UpdateProfileRequest(userId, fullName, phone))
        if (response.success && response.data != null) {
            session.saveUser(response.data, session.getToken())
            return response.data
        }
        throw Exception(response.message)
    }

    suspend fun changePassword(
        userId: String,
        oldPass: String,
        newPass: String
    ): Pair<Boolean, String> {
        return try {
            val response = authApi.changePassword(ChangePasswordRequest(userId, oldPass, newPass))
            Pair(response.success, response.message)
        } catch (e: Exception) {
            Pair(false, "เปลี่ยนรหัสผ่านไม่ได้: ${e.localizedMessage}")
        }
    }

    fun getSavedUser(): UserProfile? = session.getUser()

    fun logout() = session.clearSession()
}
