package com.example.ticketpop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
        }
    }
}