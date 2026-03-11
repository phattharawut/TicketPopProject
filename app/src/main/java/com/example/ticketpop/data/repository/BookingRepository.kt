package com.example.ticketpop.data.repository

import com.example.ticketpop.data.model.BookingRequest
import com.example.ticketpop.data.model.BookingResponse
import com.example.ticketpop.data.remote.ApiClient

class BookingRepository {

    suspend fun createBooking(request: BookingRequest): BookingResponse {
        val response = ApiClient.apiService.createBooking(request)
        if (response.success) return response.data ?: throw Exception("ไม่ได้รับข้อมูลการจอง")
        throw Exception(response.message)
    }
}
