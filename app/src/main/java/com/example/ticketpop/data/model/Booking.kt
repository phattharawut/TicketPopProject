package com.example.ticketpop.data.model

data class Booking(
    val bookingId: Int,
    val userId: Int,
    val totalAmount: Double,
    val bookingDate: String,
    val status: String,     // "Pending"|"Paid"|"Cancelled"
    val paymentMethod: String // "PromptPay"|"CreditCard"
)
