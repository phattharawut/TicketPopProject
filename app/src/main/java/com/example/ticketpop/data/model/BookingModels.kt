package com.example.ticketpop.data.model

data class BookingRequest(
    val userId: Int,
    val concertId: Int,
    val zoneId: Int,
    val seatIds: List<Int>?, // Nullable for standing zones
    val standingCount: Int?, // Nullable for seated zones
    val totalAmount: Double,
    val paymentMethod: String
)

data class BookingResponse(
    val bookingId: Int,
    val status: String,
    val message: String
)
