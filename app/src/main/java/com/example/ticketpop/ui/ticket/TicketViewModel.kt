package com.example.ticketpop.ui.ticket

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.Ticket
import com.example.ticketpop.data.remote.ApiClient
import kotlinx.coroutines.launch

class TicketViewModel : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _tickets = mutableStateOf<List<Ticket>>(emptyList())
    val tickets: State<List<Ticket>> = _tickets

    fun loadUserTickets(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = ApiClient.apiService.getUserTickets(userId)
                if (response.success) {
                    _tickets.value = response.data ?: emptyList()
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load tickets"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
