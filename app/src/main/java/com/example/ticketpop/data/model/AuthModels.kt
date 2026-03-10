package com.example.ticketpop.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: UserProfile
)

data class UserProfile(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String, // เพิ่ม role เพื่อแยก User/Admin
    val level: String = "Bronze",
    val profileImageUrl: String? = null
)

data class UpdateProfileRequest(
    val userId: String,
    val fullName: String,
    val phone: String
)

data class UserStats(
    val ticketCount: String,
    val points: String,
    val historyCount: String
)

data class ChangePasswordRequest(
    val userId: String,
    val oldPassword: String,
    val newPassword: String
)

data class SimpleResponse(
    val success: Boolean,
    val message: String
)