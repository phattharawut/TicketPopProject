package com.example.ticketpop.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketpop.ui.theme.*

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        // Soft Purple Gradient Background (Consistency with Login)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(LightPurple.copy(alpha = 0.5f), DarkBackground),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "สร้างบัญชีใหม่",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple
            )
            Text(
                text = "เริ่มต้นประสบการณ์การดูหนังที่ดีที่สุด",
                color = TextGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthTextField(value = fullName, onValueChange = { fullName = it }, label = "ชื่อ-นามสกุล")
            Spacer(modifier = Modifier.height(16.dp))
            AuthTextField(value = email, onValueChange = { email = it }, label = "Email")
            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = phone,
                onValueChange = { if (it.length <= 10) phone = it },
                label = "เบอร์โทรศัพท์ (10 หลัก)",
                isNumber = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            AuthTextField(value = password, onValueChange = { password = it }, label = "รหัสผ่าน", isPassword = true)
            Spacer(modifier = Modifier.height(16.dp))
            AuthTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "ยืนยันรหัสผ่าน", isPassword = true)

            Spacer(modifier = Modifier.height(32.dp))

            val errorMessage = localError ?: (if (authState is AuthState.Error) (authState as AuthState.Error).message else null)
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    when {
                        fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() -> {
                            localError = "กรุณากรอกข้อมูลให้ครบทุกช่อง"
                        }
                        phone.length != 10 -> {
                            localError = "เบอร์โทรศัพท์ต้องมี 10 หลัก"
                        }
                        password != confirmPassword -> {
                            localError = "รหัสผ่านไม่ตรงกัน"
                        }
                        else -> {
                            localError = null
                            viewModel.register(fullName, email, phone, password)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("ลงทะเบียน", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            TextButton(onClick = onNavigateBack) {
                Text("มีบัญชีอยู่แล้ว? ", color = TextGray)
                Text("เข้าสู่ระบบ", color = PrimaryPurple, fontWeight = FontWeight.Bold)
            }
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    isNumber: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextGray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryPurple,
            unfocusedBorderColor = SurfaceGray,
            focusedTextColor = TextBlack,
            unfocusedTextColor = TextBlack,
            cursorColor = PrimaryPurple,
            focusedLabelColor = PrimaryPurple
        )
    )
}
