package com.example.ticketpop.ui.payment

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
// Import Model จากโปรเจกต์ของเพื่อน
import com.example.ticketpop.data.model.Concert
import com.example.ticketpop.data.model.Zone
import com.example.ticketpop.data.model.Seat

@Composable
fun OrderSummaryScreen(
    concert: Concert,       // รับข้อมูลจาก Concert.kt
    zone: Zone,             // รับข้อมูลจาก Zone.kt
    selectedSeats: List<Seat>, // รับรายชื่อที่นั่งจาก Seat.kt
    onNext: (String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("PromptPay") }

    // คำนวณราคาสรุป
    val ticketCount = selectedSeats.size
    val subTotal = zone.price * ticketCount
    val fee = 40.0
    val totalAmount = subTotal + fee

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(20.dp)
    ) {
        Text("สรุปรายการจอง", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // ดึงชื่อจาก Concert.kt
                Text(text = concert.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF6200EE))
                Spacer(modifier = Modifier.height(12.dp))

                // รายชื่อที่นั่ง (วนลูปจาก Seat.kt)
                val seatLabels = selectedSeats.joinToString(", ") { "${it.rowLabel}${it.numberLabel}" }
                SummaryRow("ที่นั่ง", "โซน ${zone.zoneName} ($seatLabels)")

                // ราคาและจำนวน
                SummaryRow("ราคาต่อที่นั่ง", "${zone.price} บาท")
                SummaryRow("จำนวน", "$ticketCount ใบ")
                SummaryRow("ค่าธรรมเนียม", "$fee บาท")

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // ยอดรวม (ตรงกับค่า totalAmount ใน Booking.kt)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ยอดรวมสุทธิ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("${totalAmount} บาท", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFFE91E63))
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        Text("เลือกช่องทางการชำระเงิน", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        // Radio เลือกวิธีชำระ (ตรงกับเงื่อนไขใน Booking.kt)
        PaymentOptionItem("QR PromptPay", selectedMethod == "PromptPay") { selectedMethod = "PromptPay" }
        Spacer(modifier = Modifier.height(8.dp))
        PaymentOptionItem("บัตรเครดิต / เดบิต", selectedMethod == "CreditCard") { selectedMethod = "CreditCard" }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onNext(selectedMethod) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("ดำเนินการชำระเงิน", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

// ... คงฟังก์ชัน PaymentOptionItem ไว้เหมือนเดิม ...

@Composable
fun PaymentOptionItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) Color(0xFF6200EE) else Color(0xFFE0E0E0)
    val bgColor = if (isSelected) Color(0xFFF3E5F5) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null // จัดการคลิกผ่าน Row แทนเพื่อให้กดง่ายขึ้น
        )
        Text(
            text = title,
            modifier = Modifier.padding(start = 12.dp),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF6200EE) else Color.Black
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderSummaryPreview() {
    // 1. สร้างข้อมูลคอนเสิร์ตสมมติ
    val mockConcert = Concert(
        concertId = 1,
        title = "Summer Music Fest 2024",
        description = "คอนเสิร์ตสุดมันส์รับหน้าร้อน",
        venueName = "Impact Arena",
        showDate = "2024-04-20",
        showTime = "18:00",
        posterImageUrl = "",
        status = "OnSale"
    )

    // 2. สร้างข้อมูลโซนสมมติ
    val mockZone = Zone(
        zoneId = 1,
        concertId = 1,
        zoneName = "A1",
        price = 4500.0,
        type = "Seated",
        colorCode = "#6200EE",
        capacity = 100
    )

    // 3. สร้างข้อมูลที่นั่งสมมติ (2 ที่นั่ง)
    val mockSeats = listOf(
        Seat(seatId = 1, zoneId = 1, rowLabel = "B", numberLabel = "02", isActive = true, isReserved = false),
        Seat(seatId = 2, zoneId = 1, rowLabel = "B", numberLabel = "03", isActive = true, isReserved = false)
    )

    // 4. เรียกใช้ Screen พร้อมส่งข้อมูล Mock เข้าไป
    OrderSummaryScreen(
        concert = mockConcert,
        zone = mockZone,
        selectedSeats = mockSeats,
        onNext = { method -> println("Selected: $method") }
    )
}