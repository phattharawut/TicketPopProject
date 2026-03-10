package com.example.ticketpop.ui.seat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ticketpop.data.model.Zone
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import com.example.ticketpop.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneSelectScreen(
    viewModel: SeatViewModel,
    concertId: Int,
    navController: NavController
) {
    val context = LocalContext.current

    LaunchedEffect(concertId) {
        android.util.Log.d("DEBUG", "Loading zones for concertId: $concertId") // ← เพิ่มบรรทัดนี้
        viewModel.loadZones(concertId)
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            Toast.makeText(context, it,
                    Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "เลือกโซนที่ต้องการ",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.zoneList) { zone ->
                    ZoneCard(zone = zone) {
                        if (zone.capacity > 0) {
                            viewModel.selectZone(zone)

                            val route = if (zone.type == "Standing") {
                                Constants.ROUTE_STANDING.replace("{zoneId}", zone.zoneId.toString())
                            } else {
                                Constants.ROUTE_SEAT_MAP.replace("{zoneId}", zone.zoneId.toString())
                            }
                            navController.navigate(route) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ZoneCard(
    zone: Zone,
    onClick: () -> Unit
) {
    val isFull = zone.capacity <= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isFull) { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFull) Color(0xFFF5F5F5) else Color(0xFFFFE4F1)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = zone.zoneName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFull) Color.Gray else Color.Black
                )

                Text(
                    text = "ประเภท: ${if (zone.type == "Standing") "โซนยืน" else "โซนนั่ง"}",
                    color = Color.Gray,
                    fontSize = 16.sp
                )

                Text(
                    text = if (isFull) "ขออภัย โซนนี้เต็มแล้ว" else "คงเหลือ: ${zone.capacity} ที่",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isFull) Color.Red else Color(0xFF4CAF50)
                )
            }

            Text(
                text = "${String.format("%,.0f", zone.price)} บาท",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFull) Color.Gray else Color(0xFFBB86FC)
            )
        }
    }
}