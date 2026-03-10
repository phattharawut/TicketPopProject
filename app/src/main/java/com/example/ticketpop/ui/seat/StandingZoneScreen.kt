package com.example.ticketpop.ui.seat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ticketpop.utils.Constants
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandingZoneScreen(
    viewModel: SeatViewModel,
    zoneId: Int,
    navController: NavController
) {
    val zone = viewModel.selectedZone
    val standingCount = viewModel.standingCount
    val errorMessage = viewModel.errorMessage
    val pricePerSeat = zone?.price ?: 0.0
    val capacity = zone?.capacity ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("โซนยืน - ${zone?.zoneName ?: ""}") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.Default.ArrowBack,
                            contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            val totalPrice = viewModel.getTotalPrice()

            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "รวม: ${String.format(Locale.getDefault(), "%,d", totalPrice.toInt())} บาท",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFBB86FC)
                    )
                    Button(
                        onClick = { navController.navigate(Constants.ROUTE_ORDER_SUMMARY) },
                        enabled = standingCount > 0,
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBB86FC),
                            contentColor = Color.White
                        )
                    ) {
                        Text("ยืนยันการจอง")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFE4F1)
                ),
                border = BorderStroke(1.dp, Color(0xFFBB86FC))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "เลือกจำนวนบัตร",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "จองได้สูงสุด 4 ใบต่อครั้ง",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Minus Button
                        IconButton(
                            onClick = {
                                viewModel.updateStandingCount(-1)
                            },
                            enabled = viewModel.standingCount > 0
                        ) {
                            Text("-", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        }

                        // number of tickets
                        Text(
                            text = "${viewModel.standingCount}",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBB86FC)
                        )

                        // Plus button
                        IconButton(
                            onClick = {
                                viewModel.updateStandingCount(1)
                            },
                            enabled = viewModel.standingCount < 4 && viewModel.standingCount < capacity
                        ) {
                            Text("+", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ราคาใบละ")
                        Text("${String.format(Locale.getDefault(), "%,d", pricePerSeat.toInt())} บาท")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("จำนวน")
                        Text("x$standingCount")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "รวม",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${String.format(Locale.getDefault(), "%,d", viewModel.getTotalPrice().toInt())} บาท",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBB86FC)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("(เหลือที่ว่างในโซน: ${capacity})", fontSize = 12.sp, color = Color.Gray)

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}