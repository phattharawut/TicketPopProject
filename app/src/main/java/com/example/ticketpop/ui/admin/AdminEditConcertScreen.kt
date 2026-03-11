package com.example.ticketpop.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ticketpop.data.model.EditConcertRequest
import java.util.Calendar

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
fun AdminEditConcertScreen(
    concertId: Int,
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val uploadState by viewModel.uploadState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Use concertList instead of recentConcerts for full data model
    val concert = viewModel.concertList.find { it.concertId == concertId }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var venueName by remember { mutableStateOf("") }
    var showDate by remember { mutableStateOf("") }
    var showTime by remember { mutableStateOf("19:00") }
    var status by remember { mutableStateOf("Active") }

    // Load full concert list on start
    LaunchedEffect(Unit) {
        viewModel.loadConcerts()
    }

    // Sync state when concert data is loaded/found
    LaunchedEffect(concert) {
        concert?.let {
            title = it.title
            description = it.description ?: ""
            venueName = it.venueName
            showDate = it.showDate
            showTime = it.showTime
            status = it.status
        }
    }

    // Dialog visibility
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val posterImageUrl = if (viewModel.uploadedPosterUrl.value.isNotEmpty()) {
        viewModel.uploadedPosterUrl.value
    } else {
        concert?.posterImageUrl ?: ""
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            viewModel.uploadPoster(uri, context)
        }
    }

    val canSave = state !is AdminState.Loading && uploadState !is UploadState.Loading

    LaunchedEffect(state) {
        when (state) {
            is AdminState.Success -> {
                snackbarHostState.showSnackbar((state as AdminState.Success).message)
                viewModel.resetState()
                viewModel.resetUpload()
                viewModel.loadDashboardData() // Reload to reflect changes
                onBack()
            }
            is AdminState.Error -> {
                snackbarHostState.showSnackbar((state as AdminState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // ===== DATE PICKER DIALOG =====
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance().apply { timeInMillis = millis }
                        showDate = "%d-%02d-%02d".format(
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH)
                        )
                    }
                    showDatePicker = false
                }) {
                    Text("เลือก", color = AdminViolet, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("ยกเลิก", color = AdminSubText)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = AdminSurface,
                titleContentColor = AdminText,
                headlineContentColor = AdminText,
                weekdayContentColor = AdminSubText,
                subheadContentColor = AdminSubText,
                navigationContentColor = AdminText,
                yearContentColor = AdminText,
                currentYearContentColor = AdminViolet,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = AdminPurple,
                dayContentColor = AdminText,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = AdminPurple,
                todayContentColor = AdminViolet,
                todayDateBorderColor = AdminViolet
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ===== TIME PICKER DIALOG =====
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = 19,
            initialMinute = 0,
            is24Hour = true
        )
        Dialog(
            onDismissRequest = { showTimePicker = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(24.dp),
                shape = RoundedCornerShape(20.dp),
                color = AdminSurface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("เลือกเวลา", color = AdminText, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    TimeInput(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = AdminCard,
                            selectorColor = AdminPurple,
                            containerColor = AdminCard,
                            periodSelectorBorderColor = AdminDivider,
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = AdminText,
                            timeSelectorSelectedContainerColor = AdminPurple,
                            timeSelectorUnselectedContainerColor = AdminCard,
                            timeSelectorSelectedContentColor = Color.White,
                            timeSelectorUnselectedContentColor = AdminSubText
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("ยกเลิก", color = AdminSubText)
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showTime = "%02d:%02d".format(timePickerState.hour, timePickerState.minute)
                                showTimePicker = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AdminPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("เลือก", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("แก้ไขคอนเสิร์ต", color = AdminText, fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AdminText)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminSurface)
                )
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
            // --- Concert Info ---
            AdminEditSectionHeader(icon = Icons.Default.MusicNote, title = "ข้อมูลคอนเสิร์ต")

            AdminEditDarkTextField(value = title, onValueChange = { title = it }, label = "ชื่อคอนเสิร์ต", leadingIcon = Icons.Default.Title)
            AdminEditDarkTextField(value = description, onValueChange = { description = it }, label = "รายละเอียด", leadingIcon = Icons.Default.Description, minLines = 2)
            AdminEditDarkTextField(value = venueName, onValueChange = { venueName = it }, label = "สถานที่จัดงาน", leadingIcon = Icons.Default.LocationOn)

            // Date + Time row — tap to open picker
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminEditDateTimePickerField(
                    value = showDate,
                    label = "วันที่",
                    placeholder = "เลือกวัน",
                    icon = Icons.Default.CalendarMonth,
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                )
                AdminEditDateTimePickerField(
                    value = showTime,
                    label = "เวลา",
                    placeholder = "เลือกเวลา",
                    icon = Icons.Default.Schedule,
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f)
                )
            }

            // --- Status ---
            AdminEditSectionHeader(icon = Icons.Default.Info, title = "สถานะคอนเสิร์ต")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminEditStatusChip(
                    text = "เปิดขาย (Active)",
                    selected = status == "Active",
                    color = AdminGreen,
                    onClick = { status = "Active" }
                )
                AdminEditStatusChip(
                    text = "ยกเลิก (Cancelled)",
                    selected = status == "Cancelled",
                    color = AdminRed,
                    onClick = { status = "Cancelled" }
                )
                AdminEditStatusChip(
                    text = "ที่นั่งอาจเต็ม (SoldOut)",
                    selected = status == "SoldOut",
                    color = AdminAmber,
                    onClick = { status = "SoldOut" }
                )
            }


            // --- Poster Upload ---
            Spacer(Modifier.height(8.dp))
            AdminEditSectionHeader(icon = Icons.Default.Image, title = "รูปโปสเตอร์")
            AdminEditPosterUploadSection(
                uploadState = uploadState,
                selectedImageUri = selectedImageUri,
                uploadedUrl = posterImageUrl,
                onPickImage = { imagePicker.launch("image/*") },
                onRetry = {
                    selectedImageUri?.let { viewModel.uploadPoster(it, context) }
                        ?: imagePicker.launch("image/*")
                }
            )


            Spacer(Modifier.height(8.dp))

            // --- Save Button ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (!canSave) Brush.horizontalGradient(listOf(AdminDivider, AdminDivider))
                        else Brush.horizontalGradient(listOf(AdminPurple, AdminPink))
                    )
            ) {
                Button(
                    onClick = {
                        viewModel.updateConcert(
                            concertId,
                            EditConcertRequest(
                                title = title,
                                description = description,
                                venueName = venueName,
                                showDate = showDate,
                                showTime = showTime,
                                posterImageUrl = posterImageUrl,
                                status = status
                            )
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    enabled = canSave
                ) {
                    if (state is AdminState.Loading) {
                        CircularProgressIndicator(color = AdminSubText, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text("อัปเดตข้อมูล", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ===== Date/Time Picker Field (read-only, tap to open dialog) =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditDateTimePickerField(
    value: String,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = { Text(placeholder, color = AdminSubText, fontSize = 12.sp) },
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = AdminViolet, modifier = Modifier.size(20.dp))
        },
        trailingIcon = {
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = AdminSubText, modifier = Modifier.size(20.dp))
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        enabled = false,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = if (value.isEmpty()) AdminDivider else AdminViolet,
            disabledTextColor = AdminText,
            disabledLabelColor = if (value.isEmpty()) AdminSubText else AdminViolet,
            disabledLeadingIconColor = AdminViolet,
            disabledTrailingIconColor = AdminSubText,
            disabledContainerColor = AdminCard,
            disabledPlaceholderColor = AdminSubText
        )
    )
}

// ===== Poster Upload Section =====
@Composable
fun AdminEditPosterUploadSection(
    uploadState: UploadState,
    selectedImageUri: Uri?,
    uploadedUrl: String,
    onPickImage: () -> Unit,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clickable { onPickImage() }
        ) {
            when (uploadState) {
                is UploadState.Idle -> {
                    if (uploadedUrl.isNotEmpty() || selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri ?: uploadedUrl,
                            contentDescription = "รูปโปสเตอร์",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                        )
                    } else {
                        AdminEditIdlePosterPlaceholder(onClick = onPickImage)
                    }
                }

                is UploadState.Loading -> {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = AdminViolet, modifier = Modifier.size(40.dp), strokeWidth = 3.dp)
                            Text("กำลังอัพโหลด...", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                is UploadState.Success -> {
                    AsyncImage(
                        model = uploadedUrl.ifEmpty { selectedImageUri },
                        contentDescription = "รูปโปสเตอร์",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                    )
                    Box(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = AdminGreen.copy(alpha = 0.9f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Text("อัพโหลดสำเร็จ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .clickable { onPickImage() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "เปลี่ยนรูป", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                is UploadState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, AdminRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = AdminRed, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("อัพโหลดไม่สำเร็จ", color = AdminRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = uploadState.message,
                            color = AdminSubText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onRetry,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AdminViolet),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AdminViolet)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("ลองใหม่", fontSize = 13.sp)
                            }
                            OutlinedButton(
                                onClick = onPickImage,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AdminSubText),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AdminDivider)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("เลือกรูปใหม่", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (uploadState is UploadState.Idle && selectedImageUri == null) {
        Text(
            text = "รองรับ JPG, PNG, WEBP ขนาดไม่เกิน 5MB",
            color = AdminSubText,
            fontSize = 11.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun AdminEditIdlePosterPlaceholder(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(listOf(AdminPurple.copy(alpha = 0.5f), AdminPink.copy(alpha = 0.5f))),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AdminPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = AdminViolet, modifier = Modifier.size(32.dp))
            }
            Text("แตะเพื่อเลือกรูปโปสเตอร์", color = AdminViolet, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("จากแกลเลอรีในเครื่อง", color = AdminSubText, fontSize = 12.sp)
        }
    }
}

// ===== Section Header =====
@Composable
fun AdminEditSectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = AdminViolet, modifier = Modifier.size(18.dp))
        Text(title, color = AdminSubText, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
    }
}

// ===== DarkTextField =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditDarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    placeholder: String = "",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditStatusChip(text: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.2f),
            selectedLabelColor = color,
            containerColor = Color(0xFF2A2A4A),
            labelColor = Color(0xFF9E9EBE)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true, selected = selected,
            selectedBorderColor = color.copy(alpha = 0.5f),
            borderColor = Color.Transparent
        )
    )
}
private val AdminAmber = Color(0xFFFFB300)
