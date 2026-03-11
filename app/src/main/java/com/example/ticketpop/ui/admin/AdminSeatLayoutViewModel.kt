package com.example.ticketpop.ui.admin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.ZoneWithSeats
import com.example.ticketpop.data.repository.ConcertRepository
import com.example.ticketpop.data.repository.SeatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AdminSeatState {
    object Idle : AdminSeatState()
    object Loading : AdminSeatState()
    data class Success(val message: String) : AdminSeatState()
    data class Error(val message: String) : AdminSeatState()
}

class AdminSeatLayoutViewModel : ViewModel() {
    private val concertRepository = ConcertRepository()
    private val seatRepository = SeatRepository()

    private val _zonesWithSeats = MutableStateFlow<List<ZoneWithSeats>>(emptyList())
    val zonesWithSeats: StateFlow<List<ZoneWithSeats>> = _zonesWithSeats.asStateFlow()

    private val _uiState = MutableStateFlow<AdminSeatState>(AdminSeatState.Idle)
    val uiState: StateFlow<AdminSeatState> = _uiState.asStateFlow()

    fun loadSeatmap(concertId: Int) {
        viewModelScope.launch {
            _uiState.value = AdminSeatState.Loading
            try {
                _zonesWithSeats.value = concertRepository.getConcertZonesWithSeats(concertId)
                _uiState.value = AdminSeatState.Idle
            } catch (e: Exception) {
                _uiState.value = AdminSeatState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleSeatActive(seatId: Int, concertId: Int) {
        viewModelScope.launch {
            _uiState.value = AdminSeatState.Loading
            try {
                seatRepository.toggleSeatStatus(seatId)
                _uiState.value = AdminSeatState.Success("Seat status updated")
                // Reload seatmap to ensure data is fresh
                loadSeatmap(concertId)
            } catch (e: Exception) {
                _uiState.value = AdminSeatState.Error(e.message ?: "Failed to update seat")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = AdminSeatState.Idle
    }
}
