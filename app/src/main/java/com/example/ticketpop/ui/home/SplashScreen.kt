package com.example.ticketpop.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ticketpop.utils.Constants
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    // Animation fade-in
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000),
        label = "splash_alpha"
    )

    // ตรวจ JWT แล้ว navigate
    LaunchedEffect(Unit) {
        delay(1500) // รอให้ animation เล่นจบ

        // TODO: เปลี่ยนเป็นอ่านจาก SharedPreferences จริงเมื่อคนที่ 2 ทำ AuthViewModel เสร็จ
        // val token = prefs.getString(Constants.KEY_JWT_TOKEN, null)
        val token: String? = null  // Mock: ยังไม่มี token

        if (token != null) {
            navController.navigate(Constants.ROUTE_HOME) {
                popUpTo(Constants.ROUTE_SPLASH) { inclusive = true }
            }
        } else {
            // เปลี่ยนจาก ROUTE_LOGIN → ROUTE_HOME ก่อน เพราะยังไม่มี route login
            navController.navigate(Constants.ROUTE_HOME) {
                popUpTo(Constants.ROUTE_SPLASH) { inclusive = true }
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7B2FBE)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha)
        ) {
            Text(
                text = "TICKET",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "POP",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFD700)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "จองบัตรคอนเสิร์ตง่ายๆ แค่ปลายนิ้ว",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}