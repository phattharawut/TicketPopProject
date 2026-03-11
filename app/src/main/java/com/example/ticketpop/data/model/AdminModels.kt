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

data class EditConcertRequest(
    val title: String,
    val description: String,
    val venueName: String,
    val showDate: String,   // "YYYY-MM-DD"
    val showTime: String,   // "HH:MM"
    val posterImageUrl: String,
    val status: String
)

data class ZoneRequest(
    val zoneName: String,
    val type: String,       // "Seated" | "Standing"
    val price: Double,
    val capacity: Int,
    val colorCode: String,
    val seatsPerRow: Int = 5  // สำหรับ Seated: จำนวนที่นั่งต่อแถว
)

data class ZoneWithSeats(
    val zoneId: Int,
    val zoneName: String,
    val type: String,
    val price: Double,
    val capacity: Int,
    val colorCode: String,
    val seats: List<com.example.ticketpop.data.model.Seat>
)

data class SeatToggleResponse(
    val isActive: Int
)

// Response from image upload
data class UploadResponse(val url: String)

// Admin dashboard stats
data class AdminStats(
    val totalTickets: String,
    val totalRevenue: String,
    val totalConcerts: String,
    val attendanceRate: String
)

data class RecentConcert(
    val concertId: Int,
    val title: String,
    val showDateFormatted: String,
    val status: String
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
