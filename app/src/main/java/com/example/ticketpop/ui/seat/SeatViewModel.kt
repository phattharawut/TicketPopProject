package com.example.ticketpop.ui.seat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.Seat
import com.example.ticketpop.data.model.Zone
import com.example.ticketpop.data.remote.ApiClient
import kotlinx.coroutines.launch

class SeatViewModel : ViewModel() {
    var zoneList = mutableStateListOf<Zone>()
        private set
    var selectedZone by mutableStateOf<Zone?>(null)
        private set
    var seatList = mutableStateListOf<Seat>()
        private set
    var selectedSeats = mutableStateListOf<Seat>()
        private set
    var standingCount by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val maxTickets = 4

    fun loadZones(concertId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = ApiClient.apiService.getZones(concertId)
                if (response.success) {
                    zoneList.clear()
                    zoneList.addAll(response.data ?: emptyList())
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                errorMessage = "ไม่สามารถโหลดข้อมูลโซนได้: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadSeats(zoneId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            selectedSeats.clear()
            try {
                val response = ApiClient.apiService.getSeats(zoneId)
                if (response.success) {
                    seatList.clear()
                    seatList.addAll(response.data ?: emptyList())
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                errorMessage = "ไม่สามารถโหลดผังที่นั่งได้"
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleSeat(seat: Seat) {
        if (seat.isActive == 0 || seat.isReserved == 1) return

        if (selectedSeats.any { it.seatId == seat.seatId }) {
            selectedSeats.removeAll { it.seatId == seat.seatId }
            errorMessage = null
        } else {
            if (selectedSeats.size < maxTickets) {
                selectedSeats.add(seat)
                errorMessage = null
            } else {
                errorMessage = "เลือกได้สูงสุด $maxTickets ที่นั่งเท่านั้น"
            }
        }
    }

    fun updateStandingCount(delta: Int) {
        val newCount = standingCount + delta
        val capacity = selectedZone?.capacity ?: 0

        when {
            newCount < 0 -> { errorMessage = null }
            newCount > capacity -> {
                errorMessage = "ขออภัย โซนนี้เหลือที่ว่างเพียง $capacity ที่"
            }
            newCount > maxTickets -> {
                errorMessage = "จองได้สูงสุด $maxTickets ใบต่อครั้ง"
            }
            else -> {
                standingCount = newCount
                errorMessage = null
            }
        }
    }

    fun selectZone(zone: Zone) {
        selectedZone = zone
        selectedSeats.clear()
        standingCount = 0
        seatList.clear()
        errorMessage = null
    }

    // Clear Selection
    fun clearAllSelections() {
        selectedZone = null
        selectedSeats.clear()
        standingCount = 0
        seatList.clear()
        errorMessage = null
    }

    fun getTotalPrice(): Double {
        val price = selectedZone?.price ?: 0.0
        val count = if (selectedZone?.type == "Standing") standingCount else selectedSeats.size
        return count * price
    }
}