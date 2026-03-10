package com.example.ticketpop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.ticketpop.ui.admin.AdminDashboardScreen
import com.example.ticketpop.ui.auth.AuthViewModel
import com.example.ticketpop.ui.auth.LoginScreen
import com.example.ticketpop.ui.auth.ProfileScreen
import com.example.ticketpop.ui.auth.RegisterScreen
import com.example.ticketpop.ui.home.BottomNavigationBar
import com.example.ticketpop.ui.home.HomeScreen
import com.example.ticketpop.ui.home.SplashScreen
import com.example.ticketpop.ui.theme.TICKETPOPTheme
import com.example.ticketpop.utils.Constants
import com.example.ticketpop.ui.concert.*

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
    val authViewModel: AuthViewModel = viewModel()
    val concertViewModel: ConcertDetailViewModel = viewModel()
    
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
            startDestination = Constants.ROUTE_SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Constants.ROUTE_SPLASH) {
                SplashScreen(navController = navController)
            }

            composable(Constants.ROUTE_LOGIN) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        val user = authViewModel.currentUser.value
                        if (user?.role == "Admin") {
                            navController.navigate(Constants.ROUTE_ADMIN_DASH) {
                                popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Constants.ROUTE_PROFILE) {
                                popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                            }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Constants.ROUTE_REGISTER)
                    }
                )
            }
            
            composable(Constants.ROUTE_REGISTER) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Constants.ROUTE_PROFILE) {
                            popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Constants.ROUTE_PROFILE) {
                ProfileScreen(
                    viewModel = authViewModel,
                    onLogout = {
                        navController.navigate(Constants.ROUTE_LOGIN) {
                            popUpTo(Constants.ROUTE_PROFILE) { inclusive = true }
                        }
                    }
                )
            }

            composable(Constants.ROUTE_ADMIN_DASH) {
                AdminDashboardScreen(
                    viewModel = authViewModel,
                    onLogout = {
                        navController.navigate(Constants.ROUTE_LOGIN) {
                            popUpTo(Constants.ROUTE_ADMIN_DASH) { inclusive = true }
                        }
                    },
                    onNavigateToCreateConcert = {
                        // navController.navigate(Constants.ROUTE_ADMIN_CREATE)
                    }
                )
            }

            composable(Constants.ROUTE_HOME) {
                HomeScreen(navController = navController)
            }

            // Concert Routes
            composable(
                route = Constants.ROUTE_CONCERT_DETAIL,
                arguments = listOf(navArgument("concertId") { type = NavType.StringType })
            ) { backStackEntry ->
                val concertIdStr = backStackEntry.arguments?.getString("concertId")
                val concertId = concertIdStr?.toIntOrNull() ?: 1
                
                val concert by concertViewModel.concert
                val isLoading by concertViewModel.isLoading

                LaunchedEffect(concertId) {
                    concertViewModel.loadConcertDetail(concertId)
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (concert != null) {
                    ConcertDetailScreen(
                        concert = concert!!,
                        onBackClick = { navController.popBackStack() },
                        onVenueClick = { navController.navigate("venue/${concertId}") },
                        onArtistClick = { navController.navigate("artist/${concertId}") },
                        onSelectZoneClick = { navController.navigate("zone/${concertId}") }
                    )
                }
            }

            composable(
                route = Constants.ROUTE_VENUE_INFO,
                arguments = listOf(navArgument("concertId") { type = NavType.StringType })
            ) {
                val concert by concertViewModel.concert
                if (concert != null) {
                    VenueInfoScreen(
                        concert = concert!!,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(
                route = Constants.ROUTE_ARTIST_INFO,
                arguments = listOf(navArgument("concertId") { type = NavType.StringType })
            ) {
                val concert by concertViewModel.concert
                if (concert != null) {
                    ArtistInfoScreen(
                        concert = concert!!,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(
                route = Constants.ROUTE_ZONE_SELECT,
                arguments = listOf(navArgument("concertId") { type = NavType.StringType })
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Seat Map Screen ของเพื่อน")
                }
            }
        }
    }
}