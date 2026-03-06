package com.example.ticketpop.ui.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketpop.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val user = viewModel.currentUser.value
    val userStats by viewModel.userStats

    var isEditing by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf(user?.fullName ?: "") }
    var editPhone by remember { mutableStateOf(user?.phone ?: "") }

    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmNewPass by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("โปรไฟล์", color = TextBlack, fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = PrimaryPurple
                        )
                    }
                },
                modifier = Modifier.shadow(2.dp)
            )
        },
        containerColor = Color(0xFFFDFBFF) // พื้นหลังขาวอมม่วงจางๆ
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Card Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(LightPurple)
                            .border(BorderStroke(3.dp, PrimaryPurple), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = SecondaryPurple
                        )
                        // Edit Badge
                        if (isEditing) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryPurple)
                                    .align(Alignment.BottomEnd)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isEditing) {
                        Text(
                            text = user?.fullName ?: "N/A",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = user?.email ?: "N/A",
                            color = TextGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        // Member Badge
                        Surface(
                            modifier = Modifier.padding(top = 12.dp),
                            color = PrimaryPurple.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text(
                                "MEMBER",
                                color = PrimaryPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("ชื่อ-นามสกุล") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPurple, focusedLabelColor = PrimaryPurple)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { if (it.length <= 10) editPhone = it },
                            label = { Text("เบอร์โทรศัพท์ (10 หลัก)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPurple, focusedLabelColor = PrimaryPurple)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (editPhone.length != 10) {
                                    Toast.makeText(context, "เบอร์โทรศัพท์ต้องมี 10 หลัก", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                user?.id?.let { id ->
                                    viewModel.updateProfile(id, editName, editPhone)
                                    Toast.makeText(context, "บันทึกข้อมูลเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show()
                                    isEditing = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("บันทึกข้อมูล", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItem("ตั๋วของฉัน", userStats?.ticketCount ?: "0", Icons.Default.ConfirmationNumber, Modifier.weight(1f))
                StatItem("ยอดใช้จ่าย", userStats?.points ?: "0", Icons.Default.AccountBalanceWallet, Modifier.weight(1f))
                StatItem("ประวัติ", userStats?.historyCount ?: "0", Icons.Default.History, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Group
            Text(
                "การตั้งค่าบัญชี",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 8.dp),
                color = TextGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    ProfileMenuAction("เปลี่ยนรหัสผ่าน", Icons.Default.Lock) {
                        showChangePasswordDialog = true
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button (Red style)
            OutlinedButton(
                onClick = { viewModel.logout(); onLogout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                border = BorderStroke(1.5.dp, PrimaryRed),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryRed)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ออกจากระบบ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Change Password Dialog (Styled)
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("เปลี่ยนรหัสผ่าน", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = oldPass,
                        onValueChange = { oldPass = it },
                        label = { Text("รหัสผ่านเดิม") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPurple, focusedLabelColor = PrimaryPurple)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("รหัสผ่านใหม่") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPurple, focusedLabelColor = PrimaryPurple)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmNewPass,
                        onValueChange = { confirmNewPass = it },
                        label = { Text("ยืนยันรหัสผ่านใหม่") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPurple, focusedLabelColor = PrimaryPurple)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass != confirmNewPass) {
                            Toast.makeText(context, "รหัสผ่านใหม่ไม่ตรงกัน", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        user?.id?.let { id ->
                            viewModel.changePassword(id, oldPass, newPass) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    showChangePasswordDialog = false
                                    oldPass = ""; newPass = ""; confirmNewPass = ""
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) { Text("ยืนยันการเปลี่ยน") }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) { Text("ยกเลิก", color = PrimaryPurple) }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun StatItem(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, LightPurple)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = PrimaryPurple, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = TextBlack, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(text = label, color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProfileMenuAction(text: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, color = TextBlack, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextGray, modifier = Modifier.size(20.dp))
        }
    }
}
