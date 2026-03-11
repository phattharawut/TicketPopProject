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
import com.example.ticketpop.ui.admin.AdminCreateConcertScreen
import com.example.ticketpop.ui.admin.AdminScanScreen
import com.example.ticketpop.ui.admin.AdminViewModel
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
import com.example.ticketpop.ui.seat.*
import com.example.ticketpop.ui.payment.PaymentViewModel
import com.example.ticketpop.ui.payment.OrderSummaryScreen
import com.example.ticketpop.ui.payment.PaymentScreen
import com.example.ticketpop.ui.payment.PaymentSuccessScreen
import com.example.ticketpop.ui.ticket.MyTicketsScreen
import com.example.ticketpop.ui.ticket.TicketQrScreen
import com.example.ticketpop.ui.ticket.TicketHistoryScreen
import com.example.ticketpop.ui.ticket.TicketViewModel

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
    val adminViewModel: AdminViewModel = viewModel()
    val concertViewModel: ConcertDetailViewModel = viewModel()
    val seatViewModel: SeatViewModel = viewModel()
    val paymentViewModel: PaymentViewModel = viewModel()
    val ticketViewModel: TicketViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != null && currentRoute in listOf(
        Constants.ROUTE_HOME,
        Constants.ROUTE_MY_TICKETS,
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
                            navController.navigate(Constants.ROUTE_HOME) {
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
                    navController = navController,
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
                    adminViewModel = adminViewModel,
                    onLogout = {
                        navController.navigate(Constants.ROUTE_LOGIN) {
                            popUpTo(Constants.ROUTE_ADMIN_DASH) { inclusive = true }
                        }
                    },
                    onNavigateToCreateConcert = {
                        navController.navigate(Constants.ROUTE_ADMIN_CREATE)
                    },
                    onNavigateToScan = {
                        navController.navigate(Constants.ROUTE_ADMIN_SCAN)
                    },
                    onNavigateToSeats = { concertId ->
                        navController.navigate("admin/seat_layout/$concertId")
                    },
                    onNavigateToHome = {
                        navController.navigate(Constants.ROUTE_HOME) {
                            popUpTo(Constants.ROUTE_ADMIN_DASH) { inclusive = true }
                        }
                    },
                    onEditConcert = { concertId ->
                        navController.navigate("admin/edit_concert/$concertId")
                    }
                )
            }

            composable(
                route = Constants.ROUTE_ADMIN_SEAT_LAYOUT,
                arguments = listOf(navArgument("concertId") { type = NavType.IntType })
            ) { backStackEntry ->
                val concertId = backStackEntry.arguments?.getInt("concertId") ?: 0
                val adminSeatLayoutViewModel: com.example.ticketpop.ui.admin.AdminSeatLayoutViewModel = viewModel()
                com.example.ticketpop.ui.admin.AdminSeatLayoutScreen(
                    concertId = concertId,
                    viewModel = adminSeatLayoutViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Constants.ROUTE_ADMIN_EDIT,
                arguments = listOf(navArgument("concertId") { type = NavType.IntType })
            ) { backStackEntry ->
                val concertId = backStackEntry.arguments?.getInt("concertId") ?: 0
                com.example.ticketpop.ui.admin.AdminEditConcertScreen(
                    concertId = concertId,
                    viewModel = adminViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Constants.ROUTE_ADMIN_CREATE) {
                AdminCreateConcertScreen(
                    viewModel = adminViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Constants.ROUTE_ADMIN_SCAN) {
                AdminScanScreen(
                    viewModel = adminViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Constants.ROUTE_HOME) {
                HomeScreen(navController = navController, authViewModel = authViewModel)
            }

            composable(Constants.ROUTE_MY_TICKETS) {
                val user = authViewModel.currentUser.value
                val userId = user?.id?.toIntOrNull()
                if (user != null && userId != null) {
                    MyTicketsScreen(
                        navController = navController,
                        userId = userId,
                        viewModel = ticketViewModel
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("กรุณาเข้าสู่ระบบเพื่อดูตั๋วของคุณ")
                    }
                }
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

            // Seat Routes
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

            composable(Constants.ROUTE_ORDER_SUMMARY) {
                val concert by concertViewModel.concert
                val zone = seatViewModel.selectedZone
                val selectedSeats = seatViewModel.selectedSeats

                if (concert != null && zone != null) {
                    OrderSummaryScreen(
                        concert = concert!!,
                        zone = zone,
                        selectedSeats = selectedSeats,
                        standingCount = seatViewModel.standingCount,
                        onBack = { navController.popBackStack() },
                        onNext = { method ->
                            navController.navigate("payment/$method")
                        }
                    )
                }
            }

            composable(
                route = Constants.ROUTE_PAYMENT,
                arguments = listOf(navArgument("method") { type = NavType.StringType })
            ) { backStackEntry ->
                val method = backStackEntry.arguments?.getString("method") ?: "PromptPay"
                val user = authViewModel.currentUser.value
                val concert = concertViewModel.concert.value
                val zone = seatViewModel.selectedZone

                PaymentScreen(
                    paymentMethod = method,
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        val userId = user?.id?.toIntOrNull()
                        if (user != null && userId != null && concert != null && zone != null) {
                            paymentViewModel.createBooking(
                                userId = userId,
                                concertId = concert.concertId,
                                zoneId = zone.zoneId,
                                seatIds = seatViewModel.selectedSeats.map { it.seatId },
                                standingCount = if (zone.type == "Standing") seatViewModel.standingCount else null,
                                totalAmount = seatViewModel.getTotalPrice() + 40.0,
                                paymentMethod = method,
                                onSuccess = { bookingId ->
                                    navController.navigate("success/$bookingId") {
                                        popUpTo(Constants.ROUTE_HOME)
                                    }
                                }
                            )
                        }
                    }
                )
            }

            composable(
                route = Constants.ROUTE_SUCCESS,
                arguments = listOf(navArgument("bookingId") { type = NavType.IntType })
            ) {
                PaymentSuccessScreen(
                    onGoHome = {
                        navController.navigate(Constants.ROUTE_HOME) {
                            popUpTo(0)
                        }
                    }
                )
            }

            // ==================== TICKET ROUTES ====================
            composable(
                route = Constants.ROUTE_TICKET_QR,
                arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
            ) { backStackEntry ->
                val ticketId = backStackEntry.arguments?.getInt("ticketId") ?: 0
                TicketQrScreen(
                    navController = navController,
                    ticketId = ticketId
                )
            }

            composable(Constants.ROUTE_TICKET_HISTORY) {
                val user = authViewModel.currentUser.value
                val userId = user?.id?.toIntOrNull()
                if (user != null && userId != null) {
                    TicketHistoryScreen(
                        navController = navController,
                        userId = userId
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("กรุณาเข้าสู่ระบบเพื่อดูประวัติการเข้าชม")
                    }
                }
            }
        }
    }
}