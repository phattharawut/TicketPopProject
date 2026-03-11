package com.example.ticketpop.data.model

data class Seat(
    val seatId: Int,
    val zoneId: Int,
    val rowLabel: String?,   // "A", "B", "C"
    val numberLabel: String,// "01", "02"
    val isActive: Int,
    val isReserved: Int // true = ขายแล้ว
)
