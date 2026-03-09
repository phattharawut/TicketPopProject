package com.example.ticketpop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ticketpop.ui.auth.LoginScreen
import com.example.ticketpop.ui.home.HomeScreen
import com.example.ticketpop.ui.seat.SeatMapScreen
import com.example.ticketpop.ui.seat.SeatViewModel
import com.example.ticketpop.ui.seat.StandingZoneScreen
import com.example.ticketpop.ui.seat.ZoneSelectScreen
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

    val seatViewModel: SeatViewModel = viewModel()

    NavHost(
        navController = navController,
        //startDestination = Constants.ROUTE_LOGIN
        startDestination = Constants.ROUTE_ZONE_SELECT.replace("{concertId}", "1")
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
        composable(
            route = Constants.ROUTE_ZONE_SELECT,
            arguments = listOf(navArgument("concertId") { type = NavType.IntType })
        ) { backStackEntry ->
            val concertId = backStackEntry.arguments?.getInt("concertId") ?: 0
            ZoneSelectScreen(
                viewModel = seatViewModel,
                concertId = concertId,
                navController = navController
            )
        }

        composable(
            route = Constants.ROUTE_SEAT_MAP,
            arguments = listOf(navArgument("zoneId") { type = NavType.IntType })
        ) { backStackEntry ->
            val zoneId = backStackEntry.arguments?.getInt("zoneId") ?: 0
            SeatMapScreen(
                viewModel = seatViewModel,
                zoneId = zoneId,
                navController = navController
            )
        }

        composable(
            route = Constants.ROUTE_STANDING,
            arguments = listOf(navArgument("zoneId") { type = NavType.IntType })
        ) { backStackEntry ->
            val zoneId = backStackEntry.arguments?.getInt("zoneId") ?: 0
            StandingZoneScreen(
                viewModel = seatViewModel,
                zoneId = zoneId,
                navController = navController
            )
        }
    }
}
