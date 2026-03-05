package com.example.ticketpop.data.remote

import com.example.ticketpop.data.model.*
import retrofit2.http.*

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("api/auth/profile/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): UserProfile

    @POST("api/auth/update-profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UserProfile

    @GET("api/auth/user-stats/{userId}")
    suspend fun getUserStats(@Path("userId") userId: String): UserStats

    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): SimpleResponse
}