package com.example.ticketpop.data.remote

import com.example.ticketpop.data.model.ApiResponse
import com.example.ticketpop.data.model.Concert
import com.example.ticketpop.data.model.Zone
import com.example.ticketpop.data.model.Seat
import com.example.ticketpop.data.model.BookingRequest
import com.example.ticketpop.data.model.BookingResponse
import com.example.ticketpop.data.model.Ticket
import retrofit2.http.*

interface ApiService {
    @GET("api/concerts")
    suspend fun getConcerts(): ApiResponse<List<Concert>>

    @GET("api/zones/{concertId}")
    suspend fun getZones(@Path("concertId") concertId: Int): ApiResponse<List<Zone>>

    @GET("api/seats/{zoneId}")
    suspend fun getSeats(@Path("zoneId") zoneId: Int): ApiResponse<List<Seat>>

    @GET("api/concerts/{concertId}")
    suspend fun getConcertDetail(@Path("concertId") concertId: Int): ApiResponse<Concert>

    @POST("api/bookings")
    suspend fun createBooking(@Body request: BookingRequest): ApiResponse<BookingResponse>

    @GET("api/tickets/user/{userId}")
    suspend fun getUserTickets(@Path("userId") userId: Int): ApiResponse<List<Ticket>>
}