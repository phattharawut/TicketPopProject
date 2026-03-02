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
        startDestination = Constants.ROUTE_LOGIN
    ) {
        composable(Constants.ROUTE_LOGIN) {
            LoginScreen()
        }
        composable(Constants.ROUTE_HOME) {
            HomeScreen()
        }
        // TODO: Add more routes as other developers complete their screens
        /*
        composable(Constants.ROUTE_REGISTER) { RegisterScreen() }
        composable(Constants.ROUTE_CONCERT_DETAIL) { backStackEntry -> 
            val concertId = backStackEntry.arguments?.getString("concertId")
            ConcertDetailScreen(concertId) 
        }
        */
    }
}
