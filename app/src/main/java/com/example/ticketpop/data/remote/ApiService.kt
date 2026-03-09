package com.example.ticketpop.data.remote

import com.example.ticketpop.data.model.ApiResponse
import com.example.ticketpop.data.model.Seat
import com.example.ticketpop.data.model.Zone
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    // TODO: Add API service methods here
    @GET("api/concerts/{concertId}/zones")
    suspend fun getZones(
        @Path("concertId") concertId: Int
    ): ApiResponse<List<Zone>>

    @GET("api/zones/{zoneId}/seats")
    suspend fun getSeats(
        @Path("zoneId") zoneId: Int
    ): ApiResponse<List<Seat>>
}
