package com.example.ticketpop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ticketpop.ui.home.BottomNavigationBar
import com.example.ticketpop.ui.home.HomeScreen
import com.example.ticketpop.ui.home.SplashScreen
import com.example.ticketpop.ui.theme.TICKETPOPTheme
import com.example.ticketpop.utils.Constants

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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // หน้าที่แสดง BottomBar
    val showBottomBar = currentRoute != null && currentRoute in listOf(
        Constants.ROUTE_HOME,
        Constants.ROUTE_MY_TICKET,
        Constants.ROUTE_PROFILE
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Constants.ROUTE_SPLASH
        ) {
            composable(Constants.ROUTE_SPLASH) {
                SplashScreen(navController = navController)
            }
            composable(Constants.ROUTE_HOME) {
                HomeScreen(navController = navController)
            }

            // TODO: Add more routes as other developers complete their screens
            /*
            composable(Constants.ROUTE_LOGIN) { LoginScreen(navController) }
            composable(Constants.ROUTE_REGISTER) { RegisterScreen(navController) }
            composable(Constants.ROUTE_MY_TICKET) { MyTicketScreen(navController) }
            composable(Constants.ROUTE_PROFILE) { ProfileScreen(navController) }
            composable("concert/{concertId}") { backStackEntry ->
                val concertId = backStackEntry.arguments?.getString("concertId")
                ConcertDetailScreen(concertId, navController)
            }
            */
        }
    }
}