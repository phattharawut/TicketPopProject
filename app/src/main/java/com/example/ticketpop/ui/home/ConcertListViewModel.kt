package com.example.ticketpop.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.Concert
import com.example.ticketpop.data.repository.ConcertRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ConcertListViewModel : ViewModel() {

    private val repository = ConcertRepository()

    private val _allConcerts = MutableStateFlow<List<Concert>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val concerts: StateFlow<List<Concert>> = _searchQuery
        .debounce(300)
        .combine(_allConcerts) { query, all ->
            if (query.isBlank()) all
            else all.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.venueName.contains(query, ignoreCase = true)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadConcerts()
    }

    fun loadConcerts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _allConcerts.value = repository.getConcerts()
            } catch (e: Exception) {
                _errorMessage.value = "ไม่สามารถเชื่อมต่อได้: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchConcerts(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }
}