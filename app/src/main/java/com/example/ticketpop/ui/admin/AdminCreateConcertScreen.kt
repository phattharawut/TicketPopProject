package com.example.ticketpop.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketpop.data.model.CreateConcertRequest
import com.example.ticketpop.data.model.ZoneRequest

private val AdminBg      = Color(0xFF0F0F1A)
private val AdminSurface = Color(0xFF1A1A2E)
private val AdminCard    = Color(0xFF16213E)
private val AdminPurple  = Color(0xFF7B2FBE)
private val AdminViolet  = Color(0xFF9D4EDD)
private val AdminPink    = Color(0xFFE040FB)
private val AdminRed     = Color(0xFFFF3B5C)
private val AdminGreen   = Color(0xFF00C897)
private val AdminText    = Color(0xFFF0F0FF)
private val AdminSubText = Color(0xFF9E9EBE)
private val AdminDivider = Color(0xFF2A2A4A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCreateConcertScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var venueName by remember { mutableStateOf("") }
    var showDate by remember { mutableStateOf("") }
    var showTime by remember { mutableStateOf("") }
    var posterImageUrl by remember { mutableStateOf("") }
    val zones = remember { mutableStateListOf(ZoneState()) }

    LaunchedEffect(state) {
        when (state) {
            is AdminState.Success -> {
                snackbarHostState.showSnackbar((state as AdminState.Success).message)
                viewModel.resetState()
                onBack()
            }
            is AdminState.Error -> {
                snackbarHostState.showSnackbar((state as AdminState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text("สร้างคอนเสิร์ต", color = AdminText, fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AdminText)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminSurface)
                )
                // Gradient separator line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Brush.horizontalGradient(listOf(AdminPurple, AdminPink)))
                )
            }
        },
        containerColor = AdminBg,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- Concert Info Section ---
            AdminSectionHeader(icon = Icons.Default.MusicNote, title = "ข้อมูลคอนเสิร์ต")

            DarkTextField(value = title, onValueChange = { title = it }, label = "ชื่อคอนเสิร์ต", leadingIcon = Icons.Default.Title)
            DarkTextField(value = description, onValueChange = { description = it }, label = "รายละเอียด", leadingIcon = Icons.Default.Description, minLines = 2)
            DarkTextField(value = venueName, onValueChange = { venueName = it }, label = "สถานที่จัดงาน", leadingIcon = Icons.Default.LocationOn)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DarkTextField(
                    value = showDate, onValueChange = { showDate = it },
                    label = "วันที่", leadingIcon = Icons.Default.CalendarMonth,
                    placeholder = "YYYY-MM-DD", modifier = Modifier.weight(1f)
                )
                DarkTextField(
                    value = showTime, onValueChange = { showTime = it },
                    label = "เวลา", leadingIcon = Icons.Default.Schedule,
                    placeholder = "HH:MM", modifier = Modifier.weight(1f)
                )
            }

            DarkTextField(value = posterImageUrl, onValueChange = { posterImageUrl = it }, label = "URL รูปโปสเตอร์", leadingIcon = Icons.Default.Image)

            // --- Zones Section ---
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AdminSectionHeader(icon = Icons.Default.ChairAlt, title = "โซนและราคา")
                TextButton(
                    onClick = { zones.add(ZoneState()) },
                    colors = ButtonDefaults.textButtonColors(contentColor = AdminViolet)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("เพิ่มโซน", fontWeight = FontWeight.SemiBold)
                }
            }

            zones.forEachIndexed { index, zone ->
                PremiumZoneCard(zone = zone, index = index, onRemove = { if (zones.size > 1) zones.removeAt(index) })
            }

            Spacer(Modifier.height(8.dp))

            // --- Save Button ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (state is AdminState.Loading) Brush.horizontalGradient(listOf(AdminDivider, AdminDivider))
                        else Brush.horizontalGradient(listOf(AdminPurple, AdminPink))
                    )
            ) {
                Button(
                    onClick = {
                        viewModel.createConcert(
                            CreateConcertRequest(
                                title = title,
                                description = description,
                                venueName = venueName,
                                showDate = showDate,
                                showTime = showTime,
                                posterImageUrl = posterImageUrl,
                                zones = zones.map {
                                    ZoneRequest(it.name, it.type, it.price.toDoubleOrNull() ?: 0.0, it.capacity.toIntOrNull() ?: 0, it.color)
                                }
                            )
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    enabled = state !is AdminState.Loading
                ) {
                    if (state is AdminState.Loading) {
                        CircularProgressIndicator(color = AdminSubText, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text("บันทึกคอนเสิร์ต", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun AdminSectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = AdminViolet, modifier = Modifier.size(18.dp))
        Text(title, color = AdminSubText, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isNumber: Boolean = false,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder, color = AdminSubText, fontSize = 12.sp) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = AdminViolet, modifier = Modifier.size(20.dp)) },
        modifier = modifier.fillMaxWidth(),
        minLines = minLines,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text),
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
}

@Composable
fun PremiumZoneCard(zone: ZoneState, index: Int, onRemove: () -> Unit) {
    val accentColor = when (index % 4) {
        0 -> AdminViolet
        1 -> AdminPink
        2 -> AdminGreen
        else -> AdminRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminCard)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Text("โซนที่ ${index + 1}", color = AdminText, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "ลบ", tint = AdminRed, modifier = Modifier.size(18.dp))
                }
            }

            DarkTextField(value = zone.name, onValueChange = { zone.name = it }, label = "ชื่อโซน", leadingIcon = Icons.Default.Label)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DarkTextField(
                    value = zone.price, onValueChange = { zone.price = it },
                    label = "ราคา (บาท)", leadingIcon = Icons.Default.Payments,
                    isNumber = true, modifier = Modifier.weight(1f)
                )
                DarkTextField(
                    value = zone.capacity, onValueChange = { zone.capacity = it },
                    label = "จำนวนที่นั่ง", leadingIcon = Icons.Default.Groups,
                    isNumber = true, modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SeatedChip(zone = zone, accentColor = accentColor)
                StandingChip(zone = zone, accentColor = accentColor)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatedChip(zone: ZoneState, accentColor: androidx.compose.ui.graphics.Color) {
    val selected = zone.type == "Seated"
    FilterChip(
        selected = selected,
        onClick = { zone.type = "Seated" },
        label = { Text("มีที่นั่ง", fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = accentColor.copy(alpha = 0.2f),
            selectedLabelColor = accentColor,
            containerColor = Color(0xFF2A2A4A),
            labelColor = Color(0xFF9E9EBE)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true, selected = selected,
            selectedBorderColor = accentColor.copy(alpha = 0.5f),
            borderColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandingChip(zone: ZoneState, accentColor: androidx.compose.ui.graphics.Color) {
    val selected = zone.type == "Standing"
    FilterChip(
        selected = selected,
        onClick = { zone.type = "Standing" },
        label = { Text("ยืน", fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = accentColor.copy(alpha = 0.2f),
            selectedLabelColor = accentColor,
            containerColor = Color(0xFF2A2A4A),
            labelColor = Color(0xFF9E9EBE)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true, selected = selected,
            selectedBorderColor = accentColor.copy(alpha = 0.5f),
            borderColor = Color.Transparent
        )
    )
}

// === State holder สำหรับโซน ===
class ZoneState {
    var name by mutableStateOf("")
    var type by mutableStateOf("Seated")
    var price by mutableStateOf("")
    var capacity by mutableStateOf("")
    var color by mutableStateOf("#7B2FBE")
}
