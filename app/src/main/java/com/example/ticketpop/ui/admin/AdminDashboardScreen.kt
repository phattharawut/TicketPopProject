package com.example.ticketpop.ui.admin

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketpop.ui.auth.AuthViewModel

// Admin-only dark color palette
private val AdminBg      = Color(0xFF0F0F1A)
private val AdminSurface = Color(0xFF1A1A2E)
private val AdminCard    = Color(0xFF16213E)
private val AdminPurple  = Color(0xFF7B2FBE)
private val AdminViolet  = Color(0xFF9D4EDD)
private val AdminPink    = Color(0xFFE040FB)
private val AdminRed     = Color(0xFFFF3B5C)
private val AdminGreen   = Color(0xFF00C897)
private val AdminAmber   = Color(0xFFFFAB40)
private val AdminText    = Color(0xFFF0F0FF)
private val AdminSubText = Color(0xFF9E9EBE)
private val AdminDivider = Color(0xFF2A2A4A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToCreateConcert: () -> Unit,
    onNavigateToScan: () -> Unit
) {
    val user = viewModel.currentUser.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Admin Panel", color = AdminSubText, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
                        Text("TICKETPOP", color = AdminText, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                },
                actions = {
                    // Admin avatar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AdminPurple, AdminPink))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            user?.fullName?.take(1)?.uppercase() ?: "A",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { viewModel.logout(); onLogout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = AdminRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminSurface)
            )
        },
        containerColor = AdminBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Greeting Banner ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(listOf(AdminPurple, AdminPink))
                        )
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("สวัสดี, ${user?.fullName ?: "Admin"} 👋", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text("วันนี้มีกิจกรรม 3 รายการ", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                        }
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(56.dp))
                    }
                }
            }

            // --- Stats Grid ---
            item {
                Text("ภาพรวมระบบ", color = AdminSubText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatCard("ยอดขายวันนี้", "฿45,000", Icons.Default.TrendingUp, AdminGreen, Modifier.weight(1f))
                    AdminStatCard("ตั๋วที่ขาย", "128", Icons.Default.ConfirmationNumber, AdminAmber, Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatCard("คอนเสิร์ต", "5", Icons.Default.LibraryMusic, AdminViolet, Modifier.weight(1f))
                    AdminStatCard("อัตราเข้าชม", "76%", Icons.Default.GroupAdd, AdminPink, Modifier.weight(1f))
                }
            }

            // --- Quick Actions ---
            item {
                Text("เครื่องมือ Admin", color = AdminSubText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminActionButton(
                        label = "สร้างคอนเสิร์ต",
                        icon = Icons.Default.AddCircle,
                        gradient = listOf(Color(0xFF5B2BE8), Color(0xFF9D4EDD)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCreateConcert
                    )
                    AdminActionButton(
                        label = "สแกนบัตร",
                        icon = Icons.Default.QrCodeScanner,
                        gradient = listOf(Color(0xFFD81B60), Color(0xFFFF5252)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToScan
                    )
                }
            }

            // --- Recent Concerts ---
            item {
                Text("คอนเสิร์ตล่าสุด", color = AdminSubText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            }

            items(listOf(
                Triple("BTS World Tour 2025", "20 มี.ค. 2025", "กำลังขาย"),
                Triple("BLACKPINK Live Stage", "05 เม.ย. 2025", "กำลังขาย"),
                Triple("Jazz in the Park", "18 เม.ย. 2025", "เร็วๆ นี้"),
            )) { (name, date, status) ->
                AdminConcertRow(name, date, status)
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, icon: ImageVector, accent: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(value, color = AdminText, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = AdminSubText, fontSize = 11.sp)
        }
    }
}

@Composable
fun AdminActionButton(label: String, icon: ImageVector, gradient: List<Color>, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                Spacer(Modifier.height(4.dp))
                Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AdminConcertRow(name: String, date: String, status: String) {
    val statusColor = if (status == "กำลังขาย") AdminGreen else AdminAmber
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AdminCard)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AdminPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = AdminViolet)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = AdminText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(date, color = AdminSubText, fontSize = 12.sp)
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = statusColor.copy(alpha = 0.15f)
            ) {
                Text(
                    status,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}