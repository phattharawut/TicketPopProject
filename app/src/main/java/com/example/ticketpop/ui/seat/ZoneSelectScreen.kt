package com.example.ticketpop.ui.seat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ticketpop.data.model.Zone
import com.example.ticketpop.utils.Constants

private val SeatBg      = Color(0xFFF8F5FF)
private val SeatCard    = Color.White
private val SeatPurple  = Color(0xFF7B2FBE)
private val SeatViolet  = Color(0xFF9D4EDD)
private val SeatPink    = Color(0xFFE040FB)
private val SeatGreen   = Color(0xFF00C897)
private val SeatRed     = Color(0xFFFF3B5C)
private val SeatText    = Color(0xFF1A1A2E)
private val SeatSubText = Color(0xFF6B7280)
private val SeatDivider = Color(0xFFE8E0F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneSelectScreen(
    viewModel: SeatViewModel,
    concertId: Int,
    navController: NavController
) {
    LaunchedEffect(concertId) {
        viewModel.loadZones(concertId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text("เลือกโซน", fontWeight = FontWeight.Bold, color = SeatText)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SeatPurple)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Brush.horizontalGradient(listOf(SeatPurple, SeatPink)))
                )
            }
        },
        containerColor = SeatBg
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
                        CircularProgressIndicator(color = SeatPurple)
                        Spacer(Modifier.height(12.dp))
                        Text("กำลังโหลดโซน...", color = SeatSubText, fontSize = 14.sp)
                    }
                }

                viewModel.errorMessage != null && viewModel.zoneList.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = SeatRed, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("โหลดข้อมูลไม่สำเร็จ", color = SeatRed, fontWeight = FontWeight.Bold)
                        Text(viewModel.errorMessage ?: "", color = SeatSubText, fontSize = 13.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadZones(concertId) },
                            colors = ButtonDefaults.buttonColors(containerColor = SeatPurple)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("ลองใหม่")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "เลือกโซนที่ต้องการ",
                                color = SeatSubText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        items(viewModel.zoneList) { zone ->
                            ZoneCard(zone = zone) {
                                if (zone.capacity > 0) {
                                    viewModel.selectZone(zone)
                                    val route = if (zone.type == "Standing") {
                                        Constants.ROUTE_STANDING.replace("{zoneId}", zone.zoneId.toString())
                                    } else {
                                        Constants.ROUTE_SEAT_MAP.replace("{zoneId}", zone.zoneId.toString())
                                    }
                                    navController.navigate(route) { launchSingleTop = true }
                                }
                            }
                        }

                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun ZoneCard(zone: Zone, onClick: () -> Unit) {
    val isFull = zone.capacity <= 0
    val isStanding = zone.type == "Standing"

    val accentColor = when {
        isFull -> SeatSubText
        isStanding -> SeatPink
        else -> SeatPurple
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isFull) { onClick() }
            .border(
                width = if (isFull) 0.dp else 1.dp,
                color = accentColor.copy(alpha = 0.25f),
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(if (isFull) 0.dp else 4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isFull) Color(0xFFF5F5F8) else SeatCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Zone icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isStanding) Icons.Default.Groups else Icons.Default.ChairAlt,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = zone.zoneName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFull) SeatSubText else SeatText
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = accentColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = if (isStanding) "โซนยืน" else "โซนนั่ง",
                                color = accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        if (!isFull) {
                            Text(
                                text = "เหลือ ${zone.capacity} ที่",
                                color = SeatGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "เต็มแล้ว",
                                color = SeatRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Price column
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%,.0f", zone.price)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isFull) SeatSubText else accentColor
                )
                Text(
                    text = "บาท/ใบ",
                    fontSize = 11.sp,
                    color = SeatSubText
                )
            }
        }
    }
}
