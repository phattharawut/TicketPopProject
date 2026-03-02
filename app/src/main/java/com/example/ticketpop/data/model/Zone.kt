package com.example.ticketpop.data.model

data class Zone(
    val zoneId: Int,
    val concertId: Int,
    val zoneName: String,
    val price: Double,
    val type: String,       // "Seated" | "Standing"
    val colorCode: String,  // hex เช่น "#FF5733"
    val capacity: Int
)
