package com.example.ticketpop.ui.concert

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.Concert
import com.example.ticketpop.data.repository.ConcertRepository
import kotlinx.coroutines.launch

class ConcertDetailViewModel : ViewModel() {

    private val repository = ConcertRepository()

    var concert = mutableStateOf<Concert?>(null)
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun loadConcertDetail(concertId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                concert.value = repository.getConcertDetail(concertId)
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }
}
