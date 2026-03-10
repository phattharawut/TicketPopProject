package com.example.ticketpop.ui.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ticketpop.data.model.Ticket

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketScreen(
    viewModel: TicketViewModel,
    userId: Int
) {
    val tickets by viewModel.tickets
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    LaunchedEffect(userId) {
        viewModel.loadUserTickets(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ตั๋วของฉัน", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            if (isLoading && tickets.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null && tickets.isEmpty()) {
                Text(
                    text = error ?: "เกิดข้อผิดพลาด",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )
            } else if (tickets.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("คุณยังไม่มีตั๋ว", color = Color.Gray, fontSize = 18.sp)
                    Text("เริ่มจองคอนเสิร์ตที่คุณชอบได้เลย!", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(tickets) { ticket ->
                        TicketItem(ticket)
                    }
                }
            }
        }
    }
}

@Composable
fun TicketItem(ticket: Ticket) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(IntrinsicSize.Min)
        ) {
            AsyncImage(
                model = ticket.posterUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp, 120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = ticket.concertTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "${ticket.showDate} | ${ticket.showTime}",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                    Text(
                        text = ticket.venueName,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "โซน: ${ticket.zoneName}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF6200EE)
                        )
                        if (ticket.seatId != null) {
                            Text(
                                text = "ที่นั่ง: ${ticket.rowLabel ?: ""}${ticket.numberLabel ?: ""}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        } else {
                            Text(
                                text = "ประเภท: Standing",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    Surface(
                        color = Color(0xFFE8DEF8),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "#${ticket.ticketId}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6200EE)
                        )
                    }
                }
            }
        }
    }
}
