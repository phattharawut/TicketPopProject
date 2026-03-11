package com.example.ticketpop.ui.admin

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ticketpop.data.model.Concert
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
    adminViewModel: AdminViewModel,
    onLogout: () -> Unit,
    onNavigateToCreateConcert: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToSeats: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
    onEditConcert: (Int) -> Unit
) {
    val user = viewModel.currentUser.value
    val stats = adminViewModel.adminStats
    val isStatsLoading = adminViewModel.isStatsLoading
    val dashboardError = adminViewModel.dashboardError
    val allConcerts = adminViewModel.concertList
    val isConcertsLoading = adminViewModel.isConcertsLoading
    val concertsError = adminViewModel.concertsError

    LaunchedEffect(Unit) {
        adminViewModel.loadDashboardData()
        adminViewModel.loadConcerts()
    }

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
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AdminPurple, AdminPink)))
                            .clickable { onNavigateToHome() },
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
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Home, contentDescription = "Go to App", tint = AdminText)
                    }
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
                        .background(Brush.horizontalGradient(listOf(AdminPurple, AdminPink)))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("สวัสดี, ${user?.fullName ?: "Admin"} 👋", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text("ยินดีต้อนรับสู่ระบบจัดการ", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                        }
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(56.dp))
                    }
                }
            }

            // --- Stats Grid ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ภาพรวมระบบ", color = AdminSubText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                    if (dashboardError != null) {
                        TextButton(onClick = { adminViewModel.loadDashboardData() }) {
                            Text("ลองใหม่", color = AdminAmber, fontSize = 11.sp)
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                if (isStatsLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AdminViolet, modifier = Modifier.size(28.dp))
                    }
                } else if (dashboardError != null && stats == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AdminRed.copy(alpha = 0.1f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.WifiOff, contentDescription = null, tint = AdminRed, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.height(6.dp))
                            Text(dashboardError ?: "", color = AdminRed, fontSize = 12.sp)
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AdminStatCard(
                            "ยอดขายรวม",
                            "฿${stats?.totalRevenue ?: "-"}",
                            Icons.Default.TrendingUp, AdminGreen, Modifier.weight(1f)
                        )
                        AdminStatCard(
                            "ตั๋วทั้งหมด",
                            stats?.totalTickets ?: "-",
                            Icons.Default.ConfirmationNumber, AdminAmber, Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AdminStatCard(
                            "คอนเสิร์ต",
                            stats?.totalConcerts ?: "-",
                            Icons.Default.LibraryMusic, AdminViolet, Modifier.weight(1f)
                        )
                        AdminStatCard(
                            "อัตราเข้าชม",
                            stats?.attendanceRate ?: "-",
                            Icons.Default.GroupAdd, AdminPink, Modifier.weight(1f)
                        )
                    }
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

            // --- All Concerts ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("คอนเสิร์ตทั้งหมด", color = AdminSubText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isConcertsLoading) {
                            CircularProgressIndicator(color = AdminViolet, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                        }
                        if (concertsError != null) {
                            TextButton(onClick = { adminViewModel.loadConcerts() }) {
                                Text("ลองใหม่", color = AdminAmber, fontSize = 11.sp)
                            }
                        } else {
                            Text(
                                "${allConcerts.size} รายการ",
                                color = AdminViolet,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                if (concertsError != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(concertsError ?: "", color = AdminRed, fontSize = 11.sp)
                }
            }

            if (allConcerts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.LibraryMusic, contentDescription = null, tint = AdminSubText, modifier = Modifier.size(36.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("ยังไม่มีคอนเสิร์ตในระบบ", color = AdminSubText, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(allConcerts, key = { it.concertId }) { concert ->
                    AdminConcertCard(
                        concert = concert,
                        onClick = { onEditConcert(concert.concertId) }
                    )
                }
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
            Text(value, color = AdminText, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
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
fun AdminConcertCard(concert: Concert, onClick: () -> Unit) {
    val statusColor = when (concert.status.lowercase()) {
        "active" -> AdminGreen
        "cancelled" -> AdminRed
        "soldout" -> AdminRed
        else -> AdminAmber
    }
    val statusLabel = when (concert.status.lowercase()) {
        "active" -> "กำลังขาย"
        "cancelled" -> "ยกเลิก"
        "soldout" -> "จำหน่ายหมด"
        else -> concert.status
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminCard)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster thumbnail
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AdminPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (!concert.posterImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = concert.posterImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = AdminViolet, modifier = Modifier.size(28.dp))
                }
            }

            Spacer(Modifier.width(14.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    concert.title,
                    color = AdminText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = AdminSubText, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(concert.venueName, color = AdminSubText, fontSize = 11.sp, maxLines = 1)
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AdminSubText, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(concert.showDate, color = AdminSubText, fontSize = 11.sp)
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            statusLabel,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = AdminViolet, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("แตะเพื่อแก้ไข", color = AdminViolet, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
