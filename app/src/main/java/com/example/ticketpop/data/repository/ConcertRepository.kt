package com.example.ticketpop.data.repository

import com.example.ticketpop.data.model.Concert
import com.example.ticketpop.data.remote.ApiClient

class ConcertRepository {

    suspend fun getConcerts(): List<Concert> {
        val response = ApiClient.apiService.getConcerts()
        if (!response.success) throw Exception(response.message)
        return response.data ?: emptyList()
    }

    suspend fun getConcertDetail(concertId: Int): Concert {
        val response = ApiClient.apiService.getConcertDetail(concertId)
        return response.data ?: throw Exception(response.message)
    }

    suspend fun getConcertZonesWithSeats(concertId: Int): List<com.example.ticketpop.data.model.ZoneWithSeats> {
        val response = ApiClient.apiService.getConcertZonesWithSeats(concertId)
        if (!response.success) throw Exception(response.message)
        return response.data ?: emptyList()
    }

    suspend fun updateConcert(concertId: Int, request: com.example.ticketpop.data.model.EditConcertRequest): Boolean {
        val response = ApiClient.adminApi.updateConcert(concertId, request)
        if (!response.success) throw Exception(response.message)
        return true
    }
}
