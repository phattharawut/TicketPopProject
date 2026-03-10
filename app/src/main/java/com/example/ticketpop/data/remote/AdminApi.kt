package com.example.ticketpop.data.remote

import com.example.ticketpop.data.model.*
import retrofit2.http.*

interface AdminApi {
    @POST("api/concerts")
    suspend fun createConcert(@Body request: CreateConcertRequest): ApiResponse<Concert>

    @GET("api/tickets/{ticketId}")
    suspend fun verifyTicket(@Path("ticketId") ticketId: String): ApiResponse<TicketVerifyResponse>

    @PUT("api/tickets/{ticketId}/use")
    suspend fun useTicket(@Path("ticketId") ticketId: String): ApiResponse<Any>
}
