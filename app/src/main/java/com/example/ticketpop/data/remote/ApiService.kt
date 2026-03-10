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

    @GET("api/concerts/{concertId}")
    suspend fun getConcertDetail(@Path("concertId") concertId: Int): ApiResponse<Concert>

    @GET("api/zones/{concertId}")
    suspend fun getZones(@Path("concertId") concertId: Int): ApiResponse<List<Zone>>
    @GET("api/zones/{zoneId}/seats")
    suspend fun getSeats(@Path("zoneId") zoneId: Int): ApiResponse<List<Seat>>

    @POST("api/bookings")
    suspend fun createBooking(@Body request: BookingRequest): ApiResponse<BookingResponse>

    // คนที่ 7
    @GET("api/users/{userId}/tickets")
    suspend fun getMyTickets(
        @Path("userId") userId: Int,
        @Header("Authorization") authorization: String
    ): ApiResponse<List<Ticket>>

    @GET("api/tickets/{ticketId}")
    suspend fun getTicketDetail(
        @Path("ticketId") ticketId: Int,
        @Header("Authorization") authorization: String
    ): ApiResponse<Ticket>

    @PUT("api/concerts/reorder")
    suspend fun reorderConcerts(@Body body: Map<String, @JvmSuppressWildcards Any>): ApiResponse<Any>
}