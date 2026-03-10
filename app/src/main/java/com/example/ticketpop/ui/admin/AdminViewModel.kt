package com.example.ticketpop.ui.admin

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.*
import com.example.ticketpop.data.remote.ApiClient
import com.example.ticketpop.data.remote.AdminApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AdminState {
    object Idle : AdminState()
    object Loading : AdminState()
    data class Success(val message: String) : AdminState()
    data class Error(val message: String) : AdminState()
}

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val adminApi = ApiClient.getClient().create(AdminApi::class.java)

    private val _state = MutableStateFlow<AdminState>(AdminState.Idle)
    val state: StateFlow<AdminState> = _state

    var verifiedTicket = mutableStateOf<TicketVerifyResponse?>(null)

    // ========== CONCERT REORDER ==========
    var concertList = mutableStateListOf<Concert>()
        private set

    fun loadConcerts() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getConcerts()
                if (response.success) {
                    concertList.clear()
                    concertList.addAll(response.data ?: emptyList())
                }
            } catch (e: Exception) { }
        }
    }

    fun moveConcertUp(index: Int) {
        if (index <= 0) return
        val temp = concertList[index]
        concertList[index] = concertList[index - 1]
        concertList[index - 1] = temp
        saveOrder()
    }

    fun moveConcertDown(index: Int) {
        if (index >= concertList.size - 1) return
        val temp = concertList[index]
        concertList[index] = concertList[index + 1]
        concertList[index + 1] = temp
        saveOrder()
    }

    private fun saveOrder() {
        viewModelScope.launch {
            try {
                val orders = concertList.mapIndexed { index, concert ->
                    mapOf("concertId" to concert.concertId, "sortOrder" to index)
                }
                ApiClient.apiService.reorderConcerts(mapOf("orders" to orders))
            } catch (e: Exception) { }
        }
    }
    // ========== END CONCERT REORDER ==========

    fun createConcert(request: CreateConcertRequest) {
        viewModelScope.launch {
            _state.value = AdminState.Loading
            try {
                val response = adminApi.createConcert(request)
                if (response.success) {
                    _state.value = AdminState.Success("สร้างคอนเสิร์ตสำเร็จ!")
                } else {
                    _state.value = AdminState.Error(response.message)
                }
            } catch (e: Exception) {
                _state.value = AdminState.Error("เกิดข้อผิดพลาด: ${e.localizedMessage}")
            }
        }
    }

    fun verifyTicket(ticketId: String) {
        viewModelScope.launch {
            _state.value = AdminState.Loading
            try {
                val response = adminApi.verifyTicket(ticketId)
                if (response.success && response.data != null) {
                    verifiedTicket.value = response.data
                    _state.value = AdminState.Success("ตั๋วถูกต้อง")
                } else {
                    verifiedTicket.value = null
                    _state.value = AdminState.Error(response.message)
                }
            } catch (e: Exception) {
                verifiedTicket.value = null
                _state.value = AdminState.Error("ตั๋วไม่ถูกต้องหรือไม่พบในระบบ")
            }
        }
    }

    fun useTicket(ticketId: String) {
        viewModelScope.launch {
            _state.value = AdminState.Loading
            try {
                val response = adminApi.useTicket(ticketId)
                if (response.success) {
                    _state.value = AdminState.Success("เช็คอินสำเร็จ!")
                    verifiedTicket.value = verifiedTicket.value?.copy(isUsed = true)
                } else {
                    _state.value = AdminState.Error(response.message)
                }
            } catch (e: Exception) {
                _state.value = AdminState.Error("เกิดข้อผิดพลาด: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _state.value = AdminState.Idle
        verifiedTicket.value = null
    }
}