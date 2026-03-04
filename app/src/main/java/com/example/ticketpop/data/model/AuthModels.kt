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
    val level: String = "Bronze",
    val profileImageUrl: String? = null
)