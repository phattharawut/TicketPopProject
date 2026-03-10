package com.example.ticketpop.data.remote


import com.example.ticketpop.data.model.ApiResponse
import com.example.ticketpop.data.model.Concert
import retrofit2.http.GET

interface ApiService {
    @GET("api/concerts")
    suspend fun getConcerts(): ApiResponse<List<Concert>>
}