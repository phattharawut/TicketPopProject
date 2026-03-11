package com.example.ticketpop.ui.seat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ticketpop.data.model.Seat
import com.example.ticketpop.utils.Constants

private val MapBg      = Color(0xFFF8F5FF)
private val MapPurple  = Color(0xFF7B2FBE)
private val MapViolet  = Color(0xFF9D4EDD)
private val MapPink    = Color(0xFFE040FB)
private val MapGreen   = Color(0xFF00C897)
private val MapRed     = Color(0xFFFF3B5C)
private val MapText    = Color(0xFF1A1A2E)
private val MapSubText = Color(0xFF6B7280)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatMapScreen(
    viewModel: SeatViewModel,
    zoneId: Int,
    navController: NavController
) {
    LaunchedEffect(zoneId) {
        viewModel.loadSeats(zoneId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text("เลือกที่นั่ง", fontWeight = FontWeight.Bold, color = MapText)
                            viewModel.selectedZone?.let {
                                Text(it.zoneName, fontSize = 12.sp, color = MapViolet)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MapPurple)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Brush.horizontalGradient(listOf(MapPurple, MapPink)))
                )
            }
        },
        bottomBar = {
            if (viewModel.selectedSeats.isNotEmpty()) {
                SeatSummaryBar(viewModel, navController)
            }
        },
        containerColor = MapBg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                viewModel.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MapPurple)
                        Spacer(Modifier.height(12.dp))
                        Text("กำลังโหลดผังที่นั่ง...", color = MapSubText, fontSize = 14.sp)
                    }
                }

                viewModel.errorMessage != null && viewModel.seatList.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MapRed, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("โหลดที่นั่งไม่สำเร็จ", color = MapRed, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(viewModel.errorMessage ?: "", color = MapSubText, fontSize = 13.sp, textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp))
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadSeats(zoneId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MapPurple)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("ลองใหม่")
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(16.dp))

                        // Stage Visual
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(36.dp)
                                .shadow(6.dp, RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(MapPurple, MapPink)),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "★  STAGE  ★",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 3.sp
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // Error message (non-blocking, เช่น "เลือกได้แค่ 4")
                        if (viewModel.errorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MapRed.copy(alpha = 0.1f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = viewModel.errorMessage ?: "",
                                    color = MapRed,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        // Seat Grid — adaptive columns
                        val totalSeats = viewModel.seatList.size
                        val columns = when {
                            totalSeats <= 20 -> 5
                            totalSeats <= 50 -> 8
                            totalSeats <= 100 -> 10
                            else -> 12
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
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

                        // Legend + Count
                        SeatLegend(selectedCount = viewModel.selectedSeats.size)
                    }
                }
            }
        }
    }
}

@Composable
fun SeatBox(seat: Seat, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = when {
        seat.isActive == 0  -> Color.Transparent
        seat.isReserved == 1 -> Color(0xFFD1D5DB)
        isSelected           -> MapPurple
        else                 -> Color.White
    }
    val textColor = when {
        seat.isActive == 0   -> Color.Transparent
        seat.isReserved == 1 -> Color(0xFF9CA3AF)
        isSelected            -> Color.White
        else                  -> MapText
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(if (seat.isActive == 1 && seat.isReserved == 0) 2.dp else 0.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(
                width = 1.dp,
                color = when {
                    seat.isActive == 0    -> Color.Transparent
                    isSelected            -> MapViolet
                    seat.isReserved == 1  -> Color(0xFFD1D5DB)
                    else                  -> Color(0xFFE5E7EB)
                },
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(enabled = seat.isReserved == 0 && seat.isActive == 1) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${seat.rowLabel ?: ""}${seat.numberLabel}",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun SeatSummaryBar(viewModel: SeatViewModel, navController: NavController) {
    Surface(
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Gradient accent line ด้านบน
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Brush.horizontalGradient(listOf(MapPurple, MapPink)))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ที่นั่งที่เลือก (${viewModel.selectedSeats.size})",
                        fontSize = 12.sp,
                        color = MapSubText,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(6.dp))

                    // Seat Chips — wrap layout
                    val seats = viewModel.selectedSeats.toList()
                    val rows = seats.chunked(3)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        rows.forEach { rowSeats ->
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                rowSeats.forEach { seat ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFEDE9FE)
                                    ) {
                                        Text(
                                            text = "${seat.rowLabel}${seat.numberLabel}",
                                            color = MapPurple,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${String.format("%,.0f", viewModel.getTotalPrice())} บาท",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MapPurple
                    )
                }

                Spacer(Modifier.width(12.dp))

                Button(
                    onClick = { navController.navigate(Constants.ROUTE_ORDER_SUMMARY) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MapPurple),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text("ยืนยัน", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SeatLegend(selectedCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem("ว่าง", Color.White, Color(0xFFE5E7EB))
        Spacer(Modifier.width(16.dp))
        LegendItem("เลือกแล้ว ($selectedCount/4)", MapPurple, Color.Transparent)
        Spacer(Modifier.width(16.dp))
        LegendItem("ขายแล้ว", Color(0xFFD1D5DB), Color.Transparent)
    }
}

@Composable
fun LegendItem(label: String, color: Color, stroke: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color, RoundedCornerShape(3.dp))
                .border(1.dp, stroke, RoundedCornerShape(3.dp))
        )
        Spacer(Modifier.width(5.dp))
        Text(label, fontSize = 11.sp, color = MapSubText)
    }
}
