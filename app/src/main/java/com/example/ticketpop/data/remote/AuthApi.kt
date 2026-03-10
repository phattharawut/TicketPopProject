package com.example.ticketpop.data.remote

import com.example.ticketpop.data.model.*
import retrofit2.http.*

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @GET("api/auth/profile/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): ApiResponse<UserProfile>

    @POST("api/auth/update-profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<UserProfile>

    @GET("api/auth/user-stats/{userId}")
    suspend fun getUserStats(@Path("userId") userId: String): UserStats

    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ApiResponse<Unit>
}