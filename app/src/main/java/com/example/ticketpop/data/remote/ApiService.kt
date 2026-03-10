package com.example.ticketpop.data.remote

import com.example.ticketpop.data.model.ApiResponse
import com.example.ticketpop.data.model.Ticket
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService {
    // GET /api/users/{userId}/tickets
    @GET("api/users/{userId}/tickets")
    suspend fun getMyTickets(
        @Path("userId") userId: Int,
        @Header("Authorization") authorization: String
    ): ApiResponse<List<Ticket>>

    // GET /api/tickets/{ticketId}
    @GET("api/tickets/{ticketId}")
    suspend fun getTicketDetail(
        @Path("ticketId") ticketId: Int,
        @Header("Authorization") authorization: String
    ): ApiResponse<Ticket>
}
