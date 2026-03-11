package com.example.ticketpop.data.repository

import com.example.ticketpop.data.model.Ticket
import com.example.ticketpop.data.remote.ApiClient
import com.example.ticketpop.utils.SessionManager

class TicketRepository(private val session: SessionManager) {

    private fun bearerToken() = "Bearer ${session.getToken() ?: ""}"

    suspend fun getMyTickets(userId: Int): List<Ticket> {
        val response = ApiClient.apiService.getMyTickets(userId, bearerToken())
        if (!response.success) throw Exception(response.message)
        return response.data ?: emptyList()
    }

    suspend fun getTicketDetail(ticketId: Int): Ticket {
        val response = ApiClient.apiService.getTicketDetail(ticketId, bearerToken())
        return response.data ?: throw Exception(response.message)
    }
}
