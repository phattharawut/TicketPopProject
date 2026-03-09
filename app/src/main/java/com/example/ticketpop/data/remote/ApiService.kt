package com.example.ticketpop.data.remote

import com.example.ticketpop.data.model.ApiResponse
import com.example.ticketpop.data.model.Concert
import com.example.ticketpop.data.model.Zone
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/concerts")
    suspend fun getConcerts(): Response<ApiResponse<List<Concert>>>

    @GET("api/concerts/{id}")
    suspend fun getConcertDetail(@Path("id") id: Int): Response<ApiResponse<Concert>>


    @GET("api/concerts/{id}/zones")
    suspend fun getZones(@Path("id") concertId: Int): Response<ApiResponse<List<Zone>>>
}