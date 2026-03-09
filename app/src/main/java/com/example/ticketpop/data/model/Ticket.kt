package com.example.ticketpop.data.model

data class Ticket(
    val ticketId: Int,
    val bookingId: Int,
    val zoneId: Int,
    val zoneName: String,
    val seatId: Int?,       // null ถ้าเป็น Standing
    val rowLabel: String?,
    val numberLabel: String?,
    val concertTitle: String,
    val showDate: String,
    val showTime: String,
    val venueName: String
)
