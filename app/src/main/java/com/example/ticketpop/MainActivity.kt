package com.example.ticketpop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ticketpop.ui.auth.LoginScreen
import com.example.ticketpop.ui.home.HomeScreen
import com.example.ticketpop.ui.theme.TICKETPOPTheme
import com.example.ticketpop.utils.Constants
import com.example.ticketpop.ui.ticket.MyTicketsScreen
import com.example.ticketpop.ui.ticket.TicketQrScreen
import com.example.ticketpop.ui.ticket.TicketHistoryScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TICKETPOPTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Constants.ROUTE_MY_TICKETS
    ) {
        composable(Constants.ROUTE_LOGIN) {
            LoginScreen()
        }
        composable(Constants.ROUTE_HOME) {
            HomeScreen()
        }

        // ==================== TICKET ROUTES (คนที่ 7) ====================
        composable(Constants.ROUTE_MY_TICKETS) {
            MyTicketsScreen(
                navController = navController,
                userId = 2  // hardcode ชั่วคราว รอคนที่ 2 ส่ง userId จริง
            )
        }
        composable(Constants.ROUTE_TICKET_QR) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId")?.toIntOrNull() ?: 0
            TicketQrScreen(
                navController = navController,
                ticketId = ticketId
            )
        }
        composable(Constants.ROUTE_TICKET_HISTORY) {
            TicketHistoryScreen(
                navController = navController,
                userId = 2  // hardcode ชั่วคราว รอคนที่ 2 ส่ง userId จริง
            )
        }
    }
}
