package com.example.ticketpop.ui.seat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ticketpop.utils.Constants
import java.util.Locale

private val StandBg      = Color(0xFFF8F5FF)
private val StandPurple  = Color(0xFF7B2FBE)
private val StandViolet  = Color(0xFF9D4EDD)
private val StandPink    = Color(0xFFE040FB)
private val StandGreen   = Color(0xFF00C897)
private val StandRed     = Color(0xFFFF3B5C)
private val StandText    = Color(0xFF1A1A2E)
private val StandSubText = Color(0xFF6B7280)
private val StandCard    = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandingZoneScreen(
    viewModel: SeatViewModel,
    zoneId: Int,
    navController: NavController
) {
    val zone = viewModel.selectedZone
    val standingCount = viewModel.standingCount
    val pricePerSeat = zone?.price ?: 0.0
    val capacity = zone?.capacity ?: 0
    val totalPrice = viewModel.getTotalPrice()
    val maxPerBooking = 4

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text("โซนยืน", fontWeight = FontWeight.Bold, color = StandText)
                            zone?.let { Text(it.zoneName, fontSize = 12.sp, color = StandViolet) }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StandPurple)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Brush.horizontalGradient(listOf(StandPurple, StandPink)))
                )
            }
        },
        bottomBar = {
            Surface(shadowElevation = 16.dp, color = Color.White) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Brush.horizontalGradient(listOf(StandPurple, StandPink)))
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ราคารวม", fontSize = 12.sp, color = StandSubText)
                            Text(
                                text = "${String.format(Locale.getDefault(), "%,d", totalPrice.toInt())} บาท",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = StandPurple
                            )
                        }
                        Button(
                            onClick = { navController.navigate(Constants.ROUTE_ORDER_SUMMARY) },
                            enabled = standingCount > 0,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StandPurple,
                                disabledContainerColor = Color(0xFFD1D5DB)
                            ),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                        ) {
                            Text("ยืนยันการจอง", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        },
        containerColor = StandBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Capacity info bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = StandViolet.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = StandViolet, modifier = Modifier.size(20.dp))
                    Text(
                        "ที่ว่างในโซนนี้: $capacity ที่  •  จองได้สูงสุด $maxPerBooking ใบ/ครั้ง",
                        fontSize = 13.sp,
                        color = StandViolet,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Counter card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = StandCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, StandPurple.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text("จำนวนบัตร", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StandText)

                    // Counter row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        // Minus button
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    if (standingCount > 0) StandPurple.copy(alpha = 0.12f)
                                    else Color(0xFFF3F4F6)
                                )
                                .let {
                                    if (standingCount > 0) it.border(1.dp, StandPurple.copy(alpha = 0.3f), CircleShape)
                                    else it
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { viewModel.updateStandingCount(-1) },
                                enabled = standingCount > 0,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.Remove, contentDescription = "ลด",
                                    tint = if (standingCount > 0) StandPurple else StandSubText,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Count display
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(StandPurple, StandPink))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$standingCount",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        // Plus button
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    if (standingCount < maxPerBooking && standingCount < capacity)
                                        StandPurple.copy(alpha = 0.12f)
                                    else Color(0xFFF3F4F6)
                                )
                                .let {
                                    if (standingCount < maxPerBooking && standingCount < capacity)
                                        it.border(1.dp, StandPurple.copy(alpha = 0.3f), CircleShape)
                                    else it
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { viewModel.updateStandingCount(1) },
                                enabled = standingCount < maxPerBooking && standingCount < capacity,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.Add, contentDescription = "เพิ่ม",
                                    tint = if (standingCount < maxPerBooking && standingCount < capacity)
                                        StandPurple else StandSubText,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE5E7EB))

                    // Price breakdown
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PriceRow("ราคาต่อใบ", "${String.format(Locale.getDefault(), "%,d", pricePerSeat.toInt())} บาท")
                        PriceRow("จำนวน", "× $standingCount")
                        HorizontalDivider(color = Color(0xFFE5E7EB))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("รวมทั้งสิ้น", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = StandText)
                            Text(
                                "${String.format(Locale.getDefault(), "%,d", totalPrice.toInt())} บาท",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = StandPurple
                            )
                        }
                    }
                }
            }

            // Error message
            if (viewModel.errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = StandRed.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = StandRed, modifier = Modifier.size(16.dp))
                        Text(viewModel.errorMessage ?: "", color = StandRed, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = StandSubText)
        Text(value, fontSize = 13.sp, color = StandText, fontWeight = FontWeight.Medium)
    }
}
