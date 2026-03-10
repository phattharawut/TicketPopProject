package com.example.ticketpop.ui.payment

import androidx.lifecycle.ViewModel
import com.example.ticketpop.data.model.ApiResponse
import com.example.ticketpop.data.model.Booking

class PaymentViewModel : ViewModel() {
    // ฟังก์ชันสำหรับเรียกใช้ API เมื่อกดยืนยันชำระเงิน
    fun createBooking(userId: Int, zoneId: Int, seatIds: List<Int>, paymentMethod: String) {

        println("Booking: User $userId, Zone $zoneId, Seats $seatIds via $paymentMethod")
    }
}