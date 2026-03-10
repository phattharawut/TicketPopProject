package com.example.ticketpop.ui.payment

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.BookingRequest
import com.example.ticketpop.data.model.BookingResponse
import com.example.ticketpop.data.remote.ApiClient
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _bookingResult = mutableStateOf<BookingResponse?>(null)
    val bookingResult: State<BookingResponse?> = _bookingResult

    fun createBooking(
        userId: Int,
        concertId: Int,
        zoneId: Int,
        seatIds: List<Int>?,
        standingCount: Int?,
        totalAmount: Double,
        paymentMethod: String,
        onSuccess: (Int) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val request = BookingRequest(
                    userId = userId,
                    concertId = concertId,
                    zoneId = zoneId,
                    seatIds = seatIds,
                    standingCount = standingCount,
                    totalAmount = totalAmount,
                    paymentMethod = paymentMethod
                )
                val response = ApiClient.apiService.createBooking(request)
                if (response.success) {
                    _bookingResult.value = response.data
                    onSuccess(response.data?.bookingId ?: 0)
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Booking failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}