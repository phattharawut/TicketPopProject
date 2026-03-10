package com.example.ticketpop.data.model

// Request to create concert
data class CreateConcertRequest(
    val title: String,
    val description: String,
    val venueName: String,
    val showDate: String,   // "YYYY-MM-DD"
    val showTime: String,   // "HH:MM"
    val posterImageUrl: String,
    val zones: List<ZoneRequest>
)

data class ZoneRequest(
    val zoneName: String,
    val type: String,       // "Seated" | "Standing"
    val price: Double,
    val capacity: Int,
    val colorCode: String
)

// Response from verifyTicket
data class TicketVerifyResponse(
    val ticketId: Int,
    val holderName: String,
    val concertTitle: String,
    val zoneName: String,
    val seatLabel: String?,    // null for standing
    val showDate: String,
    val showTime: String,
    val isUsed: Boolean
)
