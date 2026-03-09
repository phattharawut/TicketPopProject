package com.example.ticketpop.ui.seat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketpop.data.model.Seat
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ButtonDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatMapScreen(
    viewModel: SeatViewModel,
    zoneId: Int,
    navController: NavController
) {

    //val seats = viewModel.seatList

    LaunchedEffect(zoneId) {
        viewModel.loadSeats(zoneId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("เลือกที่นั่ง - ${viewModel.selectedZone?.zoneName ?: ""}") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },

        bottomBar = {
            if (viewModel.selectedSeats.isNotEmpty()) {
                SeatSummaryBar(viewModel)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stage/Screen Visual
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(36.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .background(Color.LightGray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "STAGE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 4.sp
                    //modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Grid (Adaptive)
            LazyVerticalGrid(
                //columns = GridCells.Adaptive(minSize = 48.dp),
                columns = GridCells.Fixed(5),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(viewModel.seatList) { seat ->
                    val isSelected = viewModel.selectedSeats.any { it.seatId == seat.seatId }

                    SeatBox(
                        seat = seat,
                        isSelected = isSelected,
                        onClick = { viewModel.toggleSeat(seat) }
                    )
                }
            }

            // Color Status
            SeatLegend()
        }
    }
}

@Composable
fun SeatBox(seat: Seat, isSelected: Boolean, onClick: () -> Unit) {
    // reserved = Gray, Selected = Purple, White
    val bgColor = when {
        seat.isActive == 0 -> Color.Transparent
        seat.isReserved == 1 -> Color.LightGray
        isSelected -> Color(0xFF8B5CF6) // Purple
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .shadow(if (seat.isActive == 1) 3.dp else 0.dp, RoundedCornerShape(6.dp))
            .background(bgColor, RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = if (seat.isActive == 0) Color.Transparent
                        else if (isSelected) Color.Transparent
                        else Color.Gray,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(enabled = seat.isReserved == 0 && seat.isActive == 1) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            // Show rowLabel + numberLabel e.g., A1, A2
            text = "${seat.rowLabel ?: ""}${seat.numberLabel}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected || seat.isReserved == 1) Color.White else Color.Black
        )
    }
}

@Composable
fun SeatSummaryBar(viewModel: SeatViewModel) {
    Surface(shadowElevation = 15.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ที่นั่งที่เลือก",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Seat Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    viewModel.selectedSeats.forEach { seat ->
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFFEDE9FE),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${seat.rowLabel}${seat.numberLabel}",
                                color = Color(0xFF6D28D9),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${viewModel.selectedSeats.size} ที่นั่ง",
                    fontSize = 14.sp
                )
                Text(
                    text = "${String.format("%,.0f", viewModel.getTotalPrice())} บาท",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFF4FA3)
                )
            }
            Button(
                onClick = { /* navController.navigate(Constants.ROUTE_ORDER_SUMMARY) */ },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6)
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("ยืนยันการจอง", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SeatLegend() {
    Row(
        modifier = Modifier.padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        LegendItem("ว่าง", Color.White, Color.Gray)
        LegendItem("เลือก", Color(0xFF8B5CF6), Color.Transparent)
        LegendItem("ขายแล้ว", Color.LightGray, Color.Transparent)
    }
}

@Composable
fun LegendItem(label: String, color: Color, stroke: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(3.dp))
                .border(1.dp, stroke, RoundedCornerShape(3.dp))
        )
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 13.sp, color = Color.DarkGray)
    }
}