package com.example.ticketpop.data.model

data class Concert(
    val concertId: Int,
    val title: String,
    val description: String?,
    val venueName: String,
    val showDate: String,
    val showTime: String,
    val posterImageUrl: String?,
    val status: String,
    val minPrice: Double? = null,  // ✅
    val maxPrice: Double? = null   // ✅
)
