package com.example.ticketpop.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketpop.data.model.Seat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSeatLayoutScreen(
    concertId: Int,
    viewModel: AdminSeatLayoutViewModel,
    onBack: () -> Unit
) {
    val zones by viewModel.zonesWithSeats.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var selectedZoneIndex by remember { mutableStateOf(0) }
    
    LaunchedEffect(concertId) {
        viewModel.loadSeatmap(concertId)
    }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is AdminSeatState.Success -> {
                snackbarHostState.showSnackbar((uiState as AdminSeatState.Success).message)
                viewModel.resetState()
            }
            is AdminSeatState.Error -> {
                snackbarHostState.showSnackbar((uiState as AdminSeatState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("จัดการที่นั่งคอนเสิร์ต", color = Color.White, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF16213E))
            )
        },
        containerColor = Color(0xFF0F0F1A),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState is AdminSeatState.Loading && zones.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF9D4EDD))
                }
            } else if (zones.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = selectedZoneIndex,
                    containerColor = Color(0xFF16213E),
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedZoneIndex]),
                            color = Color(0xFF9D4EDD)
                        )
                    }
                ) {
                    zones.forEachIndexed { index, zone ->
                        Tab(
                            selected = selectedZoneIndex == index,
                            onClick = { selectedZoneIndex = index },
                            text = { Text(zone.zoneName, color = if (selectedZoneIndex == index) Color(0xFF9D4EDD) else Color.Gray) }
                        )
                    }
                }
                
                val currentZone = zones[selectedZoneIndex]
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        "โซน: ${currentZone.zoneName} (${currentZone.type})",
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (currentZone.type == "Seated") {
                        val groupedSeats = currentZone.seats.groupBy { it.rowLabel ?: "Z" }
                        
                        groupedSeats.toSortedMap().forEach { (row, seats) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(row, color = Color.Gray, modifier = Modifier.width(24.dp), fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                seats.sortedBy { it.numberLabel.toIntOrNull() ?: 0 }.forEach { seat ->
                                    SeatItem(seat = seat) {
                                        if (seat.isReserved == 0) {
                                            viewModel.toggleSeatActive(seat.seatId, concertId)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color(0xFF16213E), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Text("โซนยืน (Standing)\nรับได้ ${currentZone.capacity} คน", color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(30.dp))
                    
                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem("เปิดขาย", Color(0xFF9D4EDD))
                        LegendItem("ปิด", Color.DarkGray)
                        LegendItem("ขายแล้ว", Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun SeatItem(seat: Seat, onClick: () -> Unit) {
    val bgColor = when {
        seat.isReserved == 1 -> Color.Red.copy(alpha = 0.7f)
        seat.isActive == 0 -> Color.DarkGray
        else -> Color(0xFF9D4EDD)
    }
    
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(1.dp, if (seat.isActive == 1 && seat.isReserved == 0) Color.White.copy(alpha = 0.3f) else Color.Transparent, RoundedCornerShape(4.dp))
            .clickable(enabled = seat.isReserved == 0) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(seat.numberLabel, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Spacer(Modifier.width(6.dp))
        Text(text, color = Color.Gray, fontSize = 12.sp)
    }
}
