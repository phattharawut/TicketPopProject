package com.example.ticketpop.data.remote

import com.example.ticketpop.data.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface AdminApi {
    @POST("api/concerts")
    suspend fun createConcert(@Body request: CreateConcertRequest): ApiResponse<Concert>

    @GET("api/tickets/{ticketId}")
    suspend fun verifyTicket(@Path("ticketId") ticketId: String): ApiResponse<TicketVerifyResponse>

    @PUT("api/tickets/{ticketId}/use")
    suspend fun useTicket(@Path("ticketId") ticketId: String): ApiResponse<Any>

    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): ApiResponse<UploadResponse>

    @GET("api/admin/stats")
    suspend fun getAdminStats(): ApiResponse<AdminStats>

    @GET("api/admin/recent-concerts")
    suspend fun getRecentConcerts(): ApiResponse<List<RecentConcert>>
    
    @PUT("api/concerts/{concertId}")
    suspend fun updateConcert(@Path("concertId") concertId: Int, @Body request: EditConcertRequest): ApiResponse<Any>

    @DELETE("api/concerts/{concertId}")
    suspend fun deleteConcert(@Path("concertId") concertId: Int): ApiResponse<Any>
}
