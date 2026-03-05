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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    
    // Edit States
    var editName by remember { mutableStateOf(user?.fullName ?: "") }
    var editPhone by remember { mutableStateOf(user?.phone ?: "") }
    
    // Change Password States
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmNewPass by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("โปรไฟล์", color = TextWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DarkBackground),
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = PrimaryRed
                        )
                    }
                }
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(SurfaceGray)
                    .border(BorderStroke(2.dp, PrimaryRed), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = TextGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isEditing) {
                Text(text = user?.fullName ?: "N/A", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(text = user?.email ?: "N/A", color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("ชื่อ-นามสกุล", color = TextGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, unfocusedBorderColor = SurfaceGray, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editPhone,
                    onValueChange = { if (it.length <= 10) editPhone = it },
                    label = { Text("เบอร์โทรศัพท์ (10 หลัก)", color = TextGray) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryRed, unfocusedBorderColor = SurfaceGray, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("บันทึกข้อมูล", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem("ตั๋วของฉัน", userStats?.ticketCount ?: "0", Icons.Default.List)
                StatItem("ยอดใช้จ่าย", userStats?.points ?: "0", Icons.Default.Star)
                StatItem("ประวัติ", userStats?.historyCount ?: "0", Icons.Default.Refresh)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("การตั้งค่า", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                ProfileMenuAction("เปลี่ยนรหัสผ่าน", Icons.Default.Lock) { 
                    showChangePasswordDialog = true 
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { viewModel.logout(); onLogout() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = PrimaryRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ออกจากระบบ", color = PrimaryRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("เปลี่ยนรหัสผ่าน", color = TextWhite) },
            text = {
                Column {
                    OutlinedTextField(
                        value = oldPass,
                        onValueChange = { oldPass = it },
                        label = { Text("รหัสผ่านเดิม", color = TextGray) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("รหัสผ่านใหม่", color = TextGray) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmNewPass,
                        onValueChange = { confirmNewPass = it },
                        label = { Text("ยืนยันรหัสผ่านใหม่", color = TextGray) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
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
                        if (newPass.length < 6) {
                            Toast.makeText(context, "รหัสผ่านใหม่ต้องมีอย่างน้อย 6 ตัวอักษร", Toast.LENGTH_SHORT).show()
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
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) { Text("ยืนยัน") }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) { Text("ยกเลิก") }
            },
            containerColor = SurfaceGray
        )
    }
}

@Composable
fun StatItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(15.dp)).background(SurfaceGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = PrimaryRed, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = label, color = TextGray, fontSize = 12.sp)
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
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = TextWhite, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, color = TextWhite, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextGray)
        }
    }
}