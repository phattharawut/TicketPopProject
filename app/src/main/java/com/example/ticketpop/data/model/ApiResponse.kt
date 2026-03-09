package com.example.ticketpop.data.model

import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Path

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

interface ApiService {
    // [GET] /api/concerts -> ดึงรายการคอนเสิร์ตทั้งหมด
    @GET("api/concerts")
    suspend fun getConcerts(): Response<ApiResponse<List<Concert>>>

    // [GET] /api/concerts/{concertId} -> ดึงรายละเอียดคอนเสิร์ต
    @GET("api/concerts/{id}")
    suspend fun getConcertDetail(@Path("id") concertId: Int): Response<ApiResponse<Concert>>

    // [GET] /api/concerts/{concertId}/zones -> ดึงข้อมูลโซน
    @GET("api/concerts/{id}/zones")
    suspend fun getZones(@Path("id") concertId: Int): Response<ApiResponse<List<Zone>>>
}