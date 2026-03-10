package com.example.ticketpop.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketpop.ui.auth.AuthViewModel
import com.example.ticketpop.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToCreateConcert: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", color = TextWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground),
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        // เปลี่ยนจาก Logout เป็น ExitToApp (Core Icon)
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = PrimaryRed)
                    }
                }
            )
        },
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateConcert,
                containerColor = PrimaryRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Concert")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "ภาพรวมระบบ",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // เปลี่ยนจาก Event เป็น DateRange
                    AdminStatCard("คอนเสิร์ต", "5", Icons.Default.DateRange, Modifier.weight(1f))
                    // เปลี่ยนจาก People เป็น Person
                    AdminStatCard("ผู้ใช้งาน", "150", Icons.Default.Person, Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // เปลี่ยนจาก ConfirmationNumber เป็น List
                    AdminStatCard("ยอดจองวันนี้", "12", Icons.Default.List, Modifier.weight(1f))
                    // เปลี่ยนจาก AttachMoney เป็น ShoppingCart
                    AdminStatCard("รายได้ (บาท)", "45,000", Icons.Default.ShoppingCart, Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "จัดการคอนเสิร์ต",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // ตัวอย่างรายการคอนเสิร์ต
            items(listOf("World Tour 2024", "Indie Night", "Jazz in the Park")) { concert ->
                AdminConcertItem(concert)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = TextGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun AdminConcertItem(title: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, color = TextWhite, fontWeight = FontWeight.Bold)
                Text(text = "สถานะ: กำลังขาย", color = Color.Green, fontSize = 12.sp)
            }
            Row {
                IconButton(onClick = { /* Edit */ }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextGray)
                }
                IconButton(onClick = { /* Delete */ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = PrimaryRed)
                }
            }
        }
    }
}