package com.example.ticketpop.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketpop.data.model.TicketVerifyResponse

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
fun AdminScanScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val ticket by viewModel.verifiedTicket

    var ticketId by remember { mutableStateOf("") }

    // Rotating animation for the scanner ring
    val infiniteTransition = rememberInfiniteTransition(label = "scan_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "rotation"
    )

    LaunchedEffect(Unit) { viewModel.resetState() }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("สแกนตั๋ว", color = AdminText, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AdminText)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminSurface)
                )
                Box(
                    modifier = Modifier.fillMaxWidth().height(2.dp)
                        .background(Brush.horizontalGradient(listOf(AdminRed, AdminPink)))
                )
            }
        },
        containerColor = AdminBg
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // --- QR Scanner Viewfinder ---
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Rotating gradient ring
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .rotate(rotation)
                        .border(
                            3.dp,
                            Brush.sweepGradient(listOf(AdminPurple, AdminPink, Color.Transparent, Color.Transparent)),
                            RoundedCornerShape(28.dp)
                        )
                )

                // Inner Scanner Box
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(AdminCard),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // QR grid icon
                        Icon(
                            Icons.Default.QrCode,
                            contentDescription = null,
                            tint = AdminViolet.copy(alpha = 0.5f),
                            modifier = Modifier.size(90.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "กล้องจะแสดงที่นี่",
                            color = AdminSubText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Corner brackets
                ScanCorners()
            }

            Spacer(Modifier.height(8.dp))
            Text("สแกน QR Code ของตั๋ว", color = AdminSubText, fontSize = 13.sp)

            Spacer(Modifier.height(28.dp))

            // --- Divider ---
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Divider(modifier = Modifier.weight(1f), color = AdminDivider)
                Text("  หรือกรอก Ticket ID  ", color = AdminSubText, fontSize = 12.sp)
                Divider(modifier = Modifier.weight(1f), color = AdminDivider)
            }

            Spacer(Modifier.height(20.dp))

            // --- Manual Input ---
            OutlinedTextField(
                value = ticketId,
                onValueChange = { ticketId = it },
                label = { Text("Ticket ID", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = AdminViolet, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (ticketId.isNotEmpty()) {
                        IconButton(onClick = { ticketId = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = AdminSubText, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AdminViolet,
                    unfocusedBorderColor = AdminDivider,
                    focusedTextColor = AdminText,
                    unfocusedTextColor = AdminText,
                    cursorColor = AdminViolet,
                    focusedLabelColor = AdminViolet,
                    unfocusedLabelColor = AdminSubText,
                    focusedContainerColor = AdminCard,
                    unfocusedContainerColor = AdminCard
                )
            )

            Spacer(Modifier.height(16.dp))

            // --- Verify Button ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (state is AdminState.Loading) Brush.horizontalGradient(listOf(AdminDivider, AdminDivider))
                        else Brush.horizontalGradient(listOf(Color(0xFFD81B60), Color(0xFFFF5252)))
                    )
            ) {
                Button(
                    onClick = { if (ticketId.isNotEmpty()) viewModel.verifyTicket(ticketId) },
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    enabled = ticketId.isNotEmpty() && state !is AdminState.Loading
                ) {
                    if (state is AdminState.Loading) {
                        CircularProgressIndicator(color = AdminSubText, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("ตรวจสอบตั๋ว", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // --- Result Card ---
            AnimatedVisibility(
                visible = ticket != null || state is AdminState.Error,
                enter = fadeIn() + slideInVertically { it }
            ) {
                Column {
                    when {
                        ticket != null -> {
                            PremiumValidCard(ticket!!)
                            
                            // Check-in Button
                            if (!ticket!!.isUsed) {
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.useTicket(ticket!!.ticketId.toString()) },
                                    modifier = Modifier.fillMaxWidth().height(54.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AdminGreen),
                                    enabled = state !is AdminState.Loading
                                ) {
                                    if (state is AdminState.Loading) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                                    } else {
                                        Icon(Icons.Default.Login, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("ยืนยันการเช็คอิน", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        state is AdminState.Error -> PremiumInvalidCard((state as AdminState.Error).message)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ScanCorners() {
    val cornerColor = Color.White
    val strokeWidth = 4.dp
    val cornerSize = 28.dp

    Box(modifier = Modifier.size(240.dp)) {
        // Top-left
        Box(modifier = Modifier.align(Alignment.TopStart).size(cornerSize).border(
            width = strokeWidth, color = cornerColor,
            shape = object : androidx.compose.ui.graphics.Shape {
                override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): androidx.compose.ui.graphics.Outline {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, cornerSize.value * density.density)
                        lineTo(0f, 0f)
                        lineTo(cornerSize.value * density.density, 0f)
                    }
                    return androidx.compose.ui.graphics.Outline.Generic(path)
                }
            }
        ))
        // Top-right
        Box(modifier = Modifier.align(Alignment.TopEnd).size(cornerSize).border(
            width = strokeWidth, color = cornerColor,
            shape = object : androidx.compose.ui.graphics.Shape {
                override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): androidx.compose.ui.graphics.Outline {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, 0f)
                        lineTo(cornerSize.value * density.density, 0f)
                        lineTo(cornerSize.value * density.density, cornerSize.value * density.density)
                    }
                    return androidx.compose.ui.graphics.Outline.Generic(path)
                }
            }
        ))
        // Bottom-left
        Box(modifier = Modifier.align(Alignment.BottomStart).size(cornerSize).border(
            width = strokeWidth, color = cornerColor,
            shape = object : androidx.compose.ui.graphics.Shape {
                override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): androidx.compose.ui.graphics.Outline {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, 0f)
                        lineTo(0f, cornerSize.value * density.density)
                        lineTo(cornerSize.value * density.density, cornerSize.value * density.density)
                    }
                    return androidx.compose.ui.graphics.Outline.Generic(path)
                }
            }
        ))
        // Bottom-right
        Box(modifier = Modifier.align(Alignment.BottomEnd).size(cornerSize).border(
            width = strokeWidth, color = cornerColor,
            shape = object : androidx.compose.ui.graphics.Shape {
                override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): androidx.compose.ui.graphics.Outline {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, cornerSize.value * density.density)
                        lineTo(cornerSize.value * density.density, cornerSize.value * density.density)
                        lineTo(cornerSize.value * density.density, 0f)
                    }
                    return androidx.compose.ui.graphics.Outline.Generic(path)
                }
            }
        ))
    }
}

@Composable
fun PremiumValidCard(ticket: TicketVerifyResponse) {
    val isUsed = ticket.isUsed
    val accent = if (isUsed) AdminAmber else AdminGreen

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AdminCard)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header row
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isUsed) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column {
                    Text(
                        if (isUsed) "ตั๋วถูกใช้แล้ว" else "ตั๋วถูกต้อง ✅",
                        color = accent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    Text("Ticket #${ticket.ticketId}", color = AdminSubText, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
            Divider(color = AdminDivider)
            Spacer(Modifier.height(12.dp))

            listOf(
                Triple(Icons.Default.Person, "ผู้ถือตั๋ว", ticket.holderName),
                Triple(Icons.Default.MusicNote, "คอนเสิร์ต", ticket.concertTitle),
                Triple(Icons.Default.Place, "โซน", ticket.zoneName),
                Triple(Icons.Default.ChairAlt, "ที่นั่ง", ticket.seatLabel ?: "Standing"),
                Triple(Icons.Default.Event, "วันที่", "${ticket.showDate}  ${ticket.showTime}"),
            ).forEach { (icon, label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, tint = AdminSubText, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(label, color = AdminSubText, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Text(value, color = AdminText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun PremiumInvalidCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AdminRed.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AdminCard)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(AdminRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Cancel, contentDescription = null, tint = AdminRed, modifier = Modifier.size(30.dp))
            }
            Column {
                Text("ตั๋วไม่ถูกต้อง ❌", color = AdminRed, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Spacer(Modifier.height(4.dp))
                Text(message, color = AdminSubText, fontSize = 13.sp)
            }
        }
    }
}
