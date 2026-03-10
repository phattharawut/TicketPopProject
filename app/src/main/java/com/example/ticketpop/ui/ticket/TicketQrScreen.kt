@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ticketpop.ui.ticket

import android.graphics.Bitmap
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ticketpop.data.model.Ticket
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun TicketQrScreen(
    navController: NavController,
    ticketId: Int,
    viewModel: TicketViewModel = viewModel()
) {
    // โหลดตั๋วจาก ticketId จริงที่รับมา
    LaunchedEffect(ticketId) {
        viewModel.loadTicketDetail(ticketId)
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val ticket by viewModel.ticketDetail.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // เพิ่ม Brightness เต็มจอเมื่อเปิดหน้านี้
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as? android.app.Activity)?.window
        val originalBrightness = window?.attributes?.screenBrightness ?: -1f
        window?.attributes = window?.attributes?.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }
        // คืนค่า Brightness เดิมเมื่อออกจากหน้านี้
        onDispose {
            window?.attributes = window?.attributes?.apply {
                screenBrightness = originalBrightness
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6B4EFF))
                }
            }
            errorMessage != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "โหลดข้อมูลไม่สำเร็จ",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.loadTicketDetail(ticketId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6B4EFF)
                            )
                        ) {
                            Text("ลองใหม่")
                        }
                    }
                }
            }
            ticket != null -> {
                TicketQrContent(
                    ticket = ticket!!,
                    onBack = { navController.popBackStack() }
                )
            }
            else -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ไม่พบข้อมูลตั๋ว",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("กลับ", color = Color(0xFF6B4EFF))
                        }
                    }
                }
            }
        }
    }
}

// ==================== QR CONTENT ====================
@Composable
fun TicketQrContent(ticket: Ticket, onBack: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ปุ่มย้อนกลับ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.12f),
                            RoundedCornerShape(12.dp)
                        )
                        .size(42.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ย้อนกลับ",
                        tint = Color.White
                    )
                }
            }

            // ==================== QR CARD ใหญ่เต็มหน้า ====================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // ชื่อคอนเสิร์ต
                    Text(
                        text = ticket.concertTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF1A1A2E)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // วันเวลา
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF6B4EFF)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${ticket.showDate}  •  ${ticket.showTime}",
                            fontSize = 13.sp,
                            color = Color(0xFF888899)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // สถานที่
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF6B4EFF)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = ticket.venueName,
                            fontSize = 13.sp,
                            color = Color(0xFF888899)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // ==================== QR CODE ====================
                    // format: TICKETPOP|ticketId|bookingId|zoneName
                    // คนที่ 1 ใช้ format นี้ในการสแกนที่ AdminScanScreen
                    val qrContent = "TICKETPOP|${ticket.ticketId}|${ticket.bookingId}|${ticket.zoneName}"
                    val qrBitmap = generateQrCode(qrContent)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "QR Code ตั๋วเลขที่ ${ticket.ticketId}",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // กรณี generate QR ไม่สำเร็จ
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "ไม่สามารถสร้าง QR ได้",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F5))
                    Spacer(modifier = Modifier.height(24.dp))

                    // ==================== ข้อมูลที่นั่ง ====================
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TicketInfoItem(label = "โซน", value = ticket.zoneName)
                        if (ticket.seatId != null) {
                            TicketInfoItem(label = "แถว", value = ticket.rowLabel ?: "-")
                            TicketInfoItem(label = "เบอร์", value = ticket.numberLabel ?: "-")
                        } else {
                            TicketInfoItem(label = "ประเภท", value = "ยืนชม")
                        }
                    }
                }
            }

            // ข้อความด้านล่าง
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "แสดงหน้านี้ต่อเจ้าหน้าที่เพื่อเข้างาน",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ==================== QR Generator ====================
fun generateQrCode(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x, y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK
                    else android.graphics.Color.WHITE
                )
            }
        }
        bitmap
    } catch (_: WriterException) {
        null
    }
}

// ==================== INFO ITEM ====================
@Composable
fun TicketInfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF888899)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E)
        )
    }
}

// ==================== PREVIEW ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TicketQrScreenPreview() {
    val mockTicket = Ticket(
        ticketId = 1,
        bookingId = 101,
        zoneId = 1,
        zoneName = "A1",
        seatId = 5,
        rowLabel = "B",
        numberLabel = "2",
        concertTitle = "Summer Music Fest 2024",
        showDate = "2026-04-15",
        showTime = "18:00",
        venueName = "Impact Arena"
    )
    TicketQrContent(ticket = mockTicket)
}