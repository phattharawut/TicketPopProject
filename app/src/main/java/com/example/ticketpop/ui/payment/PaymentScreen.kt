package com.example.ticketpop.ui.payment

import androidx.compose.foundation.background
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

@Composable
fun PaymentScreen(paymentMethod: String, onSuccess: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // หัวข้อเปลี่ยนตามวิธีที่เลือก
        Text(
            text = if (paymentMethod == "PromptPay") "สแกน QR Code" else "ข้อมูลบัตรเครดิต",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (paymentMethod == "PromptPay") {
            // --- ส่วนแสดง PromptPay ---
            Card(
                modifier = Modifier.size(280.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("QR CODE MOCKUP", color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("กรุณาชำระเงินภายใน 10:00 นาที", color = Color(0xFFE91E63))
        } else {
            // --- ส่วนฟอร์มบัตรเครดิต ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("กรอกรายละเอียดบัตร", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("หมายเลขบัตร 16 หลัก") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("EXP (MM/YY)") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("CVV") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSuccess,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("ยืนยันการชำระเงิน", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// --- ส่วน Preview เพื่อเช็คความสวยงาม ---

@Preview(showBackground = true, name = "Preview: PromptPay Mode")
@Composable
fun PaymentPromptPayPreview() {
    PaymentScreen(paymentMethod = "PromptPay", onSuccess = {})
}

@Preview(showBackground = true, name = "Preview: Credit Card Mode")
@Composable
fun PaymentCreditCardPreview() {
    PaymentScreen(paymentMethod = "CreditCard", onSuccess = {})
}