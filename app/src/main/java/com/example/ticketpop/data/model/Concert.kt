package com.example.ticketpop.data.model

data class Concert(
    val concertId: Int,
    val title: String,
    val description: String,
    val venueName: String,
    val showDate: String,   // "YYYY-MM-DD"
    val showTime: String,   // "HH:mm"
    val posterImageUrl: String,
    val status: String      // "Upcoming"|"OnSale"|"SoldOut"|"Ended"
)
