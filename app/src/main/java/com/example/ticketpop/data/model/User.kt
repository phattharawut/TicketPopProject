package com.example.ticketpop.data.model

data class User(
    val userId: Int,
    val username: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val role: String,       // "Admin" | "Customer"
    val createdAt: String
)
