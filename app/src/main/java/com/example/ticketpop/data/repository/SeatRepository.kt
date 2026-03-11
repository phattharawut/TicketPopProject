package com.example.ticketpop.data.repository

import com.example.ticketpop.data.model.Seat
import com.example.ticketpop.data.model.Zone
import com.example.ticketpop.data.remote.ApiClient

class SeatRepository {

    suspend fun getZones(concertId: Int): List<Zone> {
        val response = ApiClient.apiService.getZones(concertId)
        if (!response.success) throw Exception(response.message)
        return response.data ?: emptyList()
    }

    suspend fun getSeats(zoneId: Int): List<Seat> {
        val response = ApiClient.apiService.getSeats(zoneId)
        if (!response.success) throw Exception(response.message)
        return response.data ?: emptyList()
    }

    suspend fun toggleSeatStatus(seatId: Int): com.example.ticketpop.data.model.SeatToggleResponse {
        val response = ApiClient.apiService.toggleSeatStatus(seatId)
        if (!response.success) throw Exception(response.message)
        return response.data ?: throw Exception("Invalid response")
    }
}
