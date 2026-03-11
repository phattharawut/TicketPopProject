@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ticketpop.ui.ticket

import android.graphics.Bitmap
import android.view.WindowManager
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
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
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun TicketQrScreen(
    navController: NavController,
    ticketId: Int,
    viewModel: TicketViewModel = viewModel()
) {
    LaunchedEffect(ticketId) {
        viewModel.loadTicketDetail(ticketId)
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val ticket by viewModel.ticketDetail.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as? android.app.Activity)?.window
        val originalBrightness = window?.attributes?.screenBrightness ?: -1f
        window?.attributes = window?.attributes?.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }
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
                        Text("โหลดข้อมูลไม่สำเร็จ", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage ?: "", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.loadTicketDetail(ticketId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4EFF))
                        ) { Text("ลองใหม่") }
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
                        Text("ไม่พบข้อมูลตั๋ว", color = Color.White, fontSize = 16.sp)
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
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "My Digital Ticket",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // ==================== PREMIUM TICKET CARD ====================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Physical Ticket Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(24.dp))
                ) {
                    // Top Section (Details)
                    Column(
                        modifier = Modifier
                            .padding(top = 28.dp, start = 28.dp, end = 28.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = ticket.concertTitle.uppercase(),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color(0xFF1A1A2E),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF6B4EFF)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${ticket.showDate} • ${ticket.showTime}",
                                fontSize = 14.sp,
                                color = Color(0xFF555566),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF6B4EFF)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = ticket.venueName,
                                fontSize = 14.sp,
                                color = Color(0xFF555566),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Ticket Cutout Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Left Cutout
                            drawArc(
                                color = Color.Transparent,
                                startAngle = -90f,
                                sweepAngle = 180f,
                                useCenter = true,
                                size = androidx.compose.ui.geometry.Size(48f, 48f),
                                topLeft = androidx.compose.ui.geometry.Offset(-24f, 0f),
                                blendMode = androidx.compose.ui.graphics.BlendMode.Clear
                            )
                            // Right Cutout
                            drawArc(
                                color = Color.Transparent,
                                startAngle = 90f,
                                sweepAngle = 180f,
                                useCenter = true,
                                size = androidx.compose.ui.geometry.Size(48f, 48f),
                                topLeft = androidx.compose.ui.geometry.Offset(size.width - 24f, 0f),
                                blendMode = androidx.compose.ui.graphics.BlendMode.Clear
                            )
                            
                            // Dashed Line
                            val dashWidth = 8f
                            val dashGap = 8f
                            var x = 32f
                            while (x < size.width - 32f) {
                                drawLine(
                                    color = Color(0xFFD0D0DD),
                                    start = androidx.compose.ui.geometry.Offset(x, size.height / 2),
                                    end = androidx.compose.ui.geometry.Offset(x + dashWidth, size.height / 2),
                                    strokeWidth = 2f
                                )
                                x += dashWidth + dashGap
                            }
                        }
                    }

                    // Bottom Section (QR Code)
                    Column(
                        modifier = Modifier
                            .padding(start = 28.dp, end = 28.dp, bottom = 28.dp, top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(16.dp)
                                .background(Color(0xFFFAFAFF), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val qrContent = "TICKETPOP|${ticket.bookingId}|${ticket.ticketId}|${ticket.zoneName}"
                            val qrBitmap = remember(qrContent) { generateQrBitmap(qrContent) }
                            if (qrBitmap != null) {
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "QR Code",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                CircularProgressIndicator(color = Color(0xFF6B4EFF))
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Seat Info Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF4F4F9), RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TicketInfoDetailItem(label = "ZONE", value = ticket.zoneName)
                            if (ticket.seatId != null) {
                                TicketInfoDetailItem(label = "ROW", value = ticket.rowLabel ?: "-")
                                TicketInfoDetailItem(label = "SEAT", value = ticket.numberLabel ?: "-")
                            } else {
                                TicketInfoDetailItem(label = "TYPE", value = "STANDING")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "HOLDER: VALUED GUEST",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Text(
                text = "PLEASE SHOW THIS QR AT THE GATE",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


// ==================== QR CODE GENERATOR ====================
fun generateQrBitmap(content: String, size: Int = 512): Bitmap? {
    return try {
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

// ==================== INFO DETAIL ITEM ====================
@Composable
fun TicketInfoDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFFAAAAAA),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
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
        venueName = "Impact Arena",
        posterUrl = ""
    )
    TicketQrContent(ticket = mockTicket)
}