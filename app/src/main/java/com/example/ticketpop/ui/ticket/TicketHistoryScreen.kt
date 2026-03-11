@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ticketpop.ui.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ticketpop.data.model.Ticket


@Composable
fun TicketHistoryScreen(
    navController: NavController,
    userId: Int,
    viewModel: TicketViewModel = viewModel()
) {
    LaunchedEffect(userId) {
        viewModel.loadMyTickets(userId)
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val allTickets by viewModel.myTickets.collectAsState()

    // แสดงเฉพาะตั๋วที่ใช้แล้ว (showDate ผ่านไปแล้ว)
    val usedTickets by remember {
        derivedStateOf { viewModel.getUsedTickets() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FA))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ==================== HEADER ====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ย้อนกลับ",
                        tint = Color(0xFF1A1A2E)
                    )
                }
                Text(
                    text = "ประวัติการเข้าชม",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1A1A2E)
                )
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF6B4EFF))
                    }
                }
                errorMessage != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = errorMessage ?: "เกิดข้อผิดพลาด", color = Color.Red)
                    }
                }
                usedTickets.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .background(Color(0xFFF0EDFF), RoundedCornerShape(45.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(44.dp),
                                    tint = Color(0xFF6B4EFF)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "ยังไม่มีประวัติการเข้าชม",
                                color = Color(0xFF1A1A2E),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "คอนเสิร์ตที่ผ่านไปแล้วจะแสดงที่นี่",
                                color = Color(0xFFAAAAAA),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 20.dp,
                            vertical = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
// แล้วใช้
                        item { SummaryCard(tickets = allTickets) }
                        items(usedTickets) { ticket ->
                            HistoryCard(ticket = ticket)
                        }
                    }
                }
            }
        }
    }
}

// ==================== SUMMARY CARD ====================
@Composable
fun SummaryCard(tickets: List<Ticket>) {
    val todayDate = remember {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF7C5CFF), Color(0xFF4B6EFF))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(value = "${tickets.size}", label = "ทั้งหมด")
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(Color.White.copy(alpha = 0.25f))
            )
            SummaryItem(
                value = "${tickets.filter { it.showDate < todayDate }.size}",
                label = "ไปแล้ว"
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(Color.White.copy(alpha = 0.25f))
            )
            SummaryItem(
                value = "${tickets.filter { it.showDate >= todayDate }.size}",
                label = "กำลังจะมา"
            )
        }
    }
}

@Composable
fun SummaryItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

// ==================== HISTORY CARD ====================
@Composable
fun HistoryCard(ticket: Ticket, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticket.concertTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A1A2E),
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF0EDFF), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "เข้าชมแล้ว",
                        color = Color(0xFF6B4EFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color(0xFF6B4EFF)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${ticket.showDate}  •  ${ticket.showTime}",
                    fontSize = 13.sp,
                    color = Color(0xFF666680)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color(0xFF6B4EFF)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = ticket.venueName,
                    fontSize = 13.sp,
                    color = Color(0xFF666680)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color(0xFF6B4EFF)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (ticket.seatId != null)
                        "${ticket.zoneName}  •  แถว ${ticket.rowLabel}  เบอร์ ${ticket.numberLabel}"
                    else "${ticket.zoneName}  •  ยืนชม",
                    fontSize = 13.sp,
                    color = Color(0xFF666680)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color(0xFFBBBBCC)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Booking #${ticket.bookingId}",
                    fontSize = 12.sp,
                    color = Color(0xFFBBBBCC)
                )
            }
        }
    }
}

// ==================== PREVIEW ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TicketHistoryScreenPreview() {
    TicketHistoryScreen(
        navController = rememberNavController(),
        userId = 1
    )
}