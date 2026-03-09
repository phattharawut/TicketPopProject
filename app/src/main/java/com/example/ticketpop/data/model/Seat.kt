package com.example.ticketpop.data.model

data class Seat(
    val seatId: Int,
    val zoneId: Int,
    val rowLabel: String,   // "A", "B", "C"
    val numberLabel: String,// "01", "02"
    val isActive: Boolean,
    val isReserved: Boolean // true = ขายแล้ว
)
