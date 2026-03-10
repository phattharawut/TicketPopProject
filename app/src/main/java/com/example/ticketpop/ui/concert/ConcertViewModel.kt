package com.example.ticketpop.ui.concert

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.Concert
import com.example.ticketpop.data.remote.ApiClient
import kotlinx.coroutines.launch

class ConcertDetailViewModel : ViewModel() {


    var concert = mutableStateOf<Concert?>(null)
    var isLoading = mutableStateOf(false)

    fun loadConcertDetail(concertId: Int) {

        viewModelScope.launch {

            try {

                isLoading.value = true

                val response =
                    ApiClient.apiService.getConcertDetail(concertId)

                if (response.success) {

                    concert.value = response.data

                }

            } catch (e: Exception) {

                e.printStackTrace()

            } finally {

                isLoading.value = false
            }
        }
    }


}
