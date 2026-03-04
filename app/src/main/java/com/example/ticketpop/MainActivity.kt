package com.example.ticketpop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ticketpop.ui.auth.AuthViewModel
import com.example.ticketpop.ui.auth.LoginScreen
import com.example.ticketpop.ui.auth.ProfileScreen
import com.example.ticketpop.ui.auth.RegisterScreen
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
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Constants.ROUTE_LOGIN
    ) {
        composable(Constants.ROUTE_LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Constants.ROUTE_PROFILE) {
                        popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
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

        composable(Constants.ROUTE_HOME) {
            HomeScreen()
        }
    }
}