package com.example.ticketpop.ui.admin

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketpop.data.model.*
import com.example.ticketpop.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

sealed class AdminState {
    object Idle : AdminState()
    object Loading : AdminState()
    data class Success(val message: String) : AdminState()
    data class Error(val message: String) : AdminState()
}

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    object Success : UploadState()
    data class Error(val message: String) : UploadState()
}

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val adminApi = ApiClient.adminApi

    private val _state = MutableStateFlow<AdminState>(AdminState.Idle)
    val state: StateFlow<AdminState> = _state

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    val uploadedPosterUrl = mutableStateOf("")

    var verifiedTicket = mutableStateOf<TicketVerifyResponse?>(null)

    // ========== DASHBOARD STATS ==========
    var adminStats by mutableStateOf<AdminStats?>(null)
        private set
    var recentConcerts = mutableStateListOf<RecentConcert>()
        private set
    var isStatsLoading by mutableStateOf(false)
        private set

    var dashboardError by mutableStateOf<String?>(null)
        private set

    fun loadDashboardData() {
        viewModelScope.launch {
            isStatsLoading = true
            dashboardError = null
            try {
                val statsResp = adminApi.getAdminStats()
                if (statsResp.success) adminStats = statsResp.data
                else dashboardError = statsResp.message

                val concertsResp = adminApi.getRecentConcerts()
                if (concertsResp.success) {
                    recentConcerts.clear()
                    recentConcerts.addAll(concertsResp.data ?: emptyList())
                }
            } catch (e: Exception) {
                dashboardError = "เชื่อมต่อ server ไม่ได้: ${e.message}"
                android.util.Log.e("AdminViewModel", "loadDashboardData error", e)
            } finally {
                isStatsLoading = false
            }
        }
    }
    // ========== END DASHBOARD STATS ==========

    // ========== CONCERT REORDER ==========
    var concertList = mutableStateListOf<Concert>()
        private set

    var isConcertsLoading by mutableStateOf(false)
        private set
    var concertsError by mutableStateOf<String?>(null)
        private set

    fun loadConcerts() {
        viewModelScope.launch {
            isConcertsLoading = true
            concertsError = null
            try {
                // Use the general concerts endpoint - admin sees all non-cancelled
                val response = ApiClient.apiService.getConcerts()
                android.util.Log.d("AdminViewModel", "loadConcerts success=${response.success} count=${response.data?.size}")
                if (response.success) {
                    concertList.clear()
                    concertList.addAll(response.data ?: emptyList())
                } else {
                    concertsError = response.message
                }
            } catch (e: Exception) {
                concertsError = "เชื่อมต่อ server ไม่ได้: ${e.message}"
                android.util.Log.e("AdminViewModel", "loadConcerts error", e)
            } finally {
                isConcertsLoading = false
            }
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

    // ========== IMAGE UPLOAD ==========
    fun uploadPoster(uri: Uri, context: Context) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            try {
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                val ext = when (mimeType) {
                    "image/png"  -> ".png"
                    "image/webp" -> ".webp"
                    else         -> ".jpg"
                }
                val bytes = contentResolver.openInputStream(uri)?.readBytes()
                    ?: throw Exception("ไม่สามารถอ่านไฟล์ได้")

                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("image", "poster$ext", requestBody)

                val response = adminApi.uploadImage(part)
                if (response.success && response.data != null) {
                    uploadedPosterUrl.value = response.data.url
                    _uploadState.value = UploadState.Success
                } else {
                    _uploadState.value = UploadState.Error(response.message)
                }
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("อัพโหลดไม่สำเร็จ: ${e.message}")
            }
        }
    }

    fun resetUpload() {
        _uploadState.value = UploadState.Idle
        uploadedPosterUrl.value = ""
    }
    // ========== END IMAGE UPLOAD ==========

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

    fun updateConcert(concertId: Int, request: EditConcertRequest) {
        viewModelScope.launch {
            _state.value = AdminState.Loading
            try {
                val response = adminApi.updateConcert(concertId, request)
                if (response.success) {
                    _state.value = AdminState.Success("อัพเดตข้อมูลคอนเสิร์ตสำเร็จ!")
                } else {
                    _state.value = AdminState.Error(response.message)
                }
            } catch (e: Exception) {
                _state.value = AdminState.Error("เกิดข้อผิดพลาด: ${e.localizedMessage}")
            }
        }
    }

    fun deleteConcert(concertId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = adminApi.deleteConcert(concertId)
                if (response.success) {
                    loadDashboardData()
                    onSuccess()
                } else {
                    _state.value = AdminState.Error(response.message)
                }
            } catch (e: Exception) {
                _state.value = AdminState.Error("ลบไม่สำเร็จ: ${e.localizedMessage}")
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
