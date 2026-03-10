package com.example.ticketpop.ui.ticket

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.Ticket
import com.example.ticketpop.data.remote.ApiClient
import com.example.ticketpop.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(
        Constants.PREF_NAME, Context.MODE_PRIVATE
    )

    private val _myTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val myTickets: StateFlow<List<Ticket>> = _myTickets

    private val _ticketDetail = MutableStateFlow<Ticket?>(null)
    val ticketDetail: StateFlow<Ticket?> = _ticketDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadMyTickets(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = prefs.getString(Constants.KEY_JWT_TOKEN, "") ?: ""
                val response = ApiClient.apiService.getMyTickets(
                    userId = userId,
                    authorization = "Bearer $token"
                )
                if (response.success) {
                    _myTickets.value = response.data ?: emptyList()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "โหลดข้อมูลไม่สำเร็จ: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTicketDetail(ticketId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = prefs.getString(Constants.KEY_JWT_TOKEN, "") ?: ""
                val response = ApiClient.apiService.getTicketDetail(
                    ticketId = ticketId,
                    authorization = "Bearer $token"
                )
                if (response.success) {
                    _ticketDetail.value = response.data
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "โหลดข้อมูลไม่สำเร็จ: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getActiveTickets(): List<Ticket> =
        _myTickets.value.filter { it.showDate >= getTodayDate() }

    fun getUsedTickets(): List<Ticket> =
        _myTickets.value.filter { it.showDate < getTodayDate() }

    fun getTodayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}