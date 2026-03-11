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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ticketpop.utils.Constants
import androidx.compose.runtime.mutableIntStateOf

@Composable
fun MyTicketsScreen(
    navController: NavController,
    userId: Int,
    viewModel: TicketViewModel = viewModel(),
    initialTab: Int = 0
) {
    LaunchedEffect(userId) {
        viewModel.loadMyTickets(userId)
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val tabs = listOf("บัตรของฉัน", "ประวัติการเข้าชม")

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
                    text = "บัตรของฉัน",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF1A1A2E)
                )
            }

            // ==================== TAB ====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = index }
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF6B4EFF) else Color(0xFFAAAAAA)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .fillMaxWidth(0.6f)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (isSelected) Color(0xFF6B4EFF)
                                    else Color.Transparent
                                )
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            // ==================== CONTENT ====================
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
                else -> {
                    val tickets = if (selectedTab == 0)
                        viewModel.getActiveTickets()
                    else
                        viewModel.getUsedTickets()

                    if (tickets.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .background(
                                            Color(0xFFF0EDFF),
                                            RoundedCornerShape(45.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(44.dp),
                                        tint = Color(0xFF6B4EFF)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (selectedTab == 0)
                                        "ยังไม่มีบัตรในขณะนี้"
                                    else
                                        "ยังไม่มีประวัติการเข้าชม",
                                    color = Color(0xFF1A1A2E),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (selectedTab == 0)
                                        "บัตรที่จองไว้จะแสดงที่นี่"
                                    else
                                        "คอนเสิร์ตที่ผ่านไปแล้วจะแสดงที่นี่",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = 20.dp,
                                vertical = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(tickets) { ticket ->
                                val isCancelled = ticket.concertStatus == "Cancelled"
                                TicketCard(
                                    ticket = ticket,
                                    isUsed = ticket.showDate < viewModel.getTodayDate(),
                                    isCancelled = isCancelled,
                                    onClick = {
                                        if (!isCancelled && ticket.showDate >= viewModel.getTodayDate()) {
                                            navController.navigate(
                                                Constants.ROUTE_TICKET_QR
                                                    .replace(
                                                        "{ticketId}",
                                                        ticket.ticketId.toString()
                                                    )
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== TICKET CARD ====================
@Composable
fun TicketCard(
    ticket: Ticket,
    isUsed: Boolean,
    isCancelled: Boolean = false,
    onClick: () -> Unit
) {
    val gradientBrush = when {
        isCancelled -> Brush.horizontalGradient(listOf(Color(0xFF8B0000), Color(0xFFB71C1C)))
        isUsed -> Brush.horizontalGradient(listOf(Color(0xFF6B6B8A), Color(0xFF4A4A6A)))
        else -> Brush.horizontalGradient(listOf(Color(0xFF7C5CFF), Color(0xFF4B6EFF)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {

            // ==================== ส่วนบน Gradient ====================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = gradientBrush, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = ticket.concertTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        if (isCancelled) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "ยกเลิก",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${ticket.showDate}  •  ${ticket.venueName}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // ==================== ส่วนล่าง ขาว ====================
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "โซน ${ticket.zoneName}",
                            fontSize = 12.sp,
                            color = Color(0xFF888899)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (ticket.seatId != null)
                                "${ticket.rowLabel}, ${ticket.numberLabel}"
                            else "ยืนชม",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCancelled) Color(0xFFAAAAAA) else Color(0xFF1A1A2E)
                        )
                    }

                    if (!isUsed && !isCancelled) {
                        Text(
                            text = "1 ที่นั่ง",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B4EFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // เส้นประ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(40) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFFE0E0E0))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            isCancelled -> "คอนเสิร์ตถูกยกเลิก"
                            isUsed -> "จบไปแล้ว"
                            else -> "แตะเพื่อดู QR Code"
                        },
                        fontSize = 13.sp,
                        fontWeight = if (!isUsed && !isCancelled) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            isCancelled -> Color(0xFFE53935)
                            isUsed -> Color(0xFF888899)
                            else -> Color(0xFF6B4EFF)
                        }
                    )
                }
            }
        }
    }
}

// ==================== PREVIEW ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyTicketsScreenPreview() {
    MyTicketsScreen(
        navController = rememberNavController(),
        userId = 1
    )
}