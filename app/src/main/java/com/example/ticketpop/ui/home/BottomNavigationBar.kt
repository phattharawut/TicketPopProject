package com.example.ticketpop.ui.home

import androidx.compose.ui.unit.dp
import com.example.ticketpop.utils.Constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.filled.DateRange




@Composable
fun BottomNavigationBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == Constants.ROUTE_HOME,
            onClick = {
                navController.navigate(Constants.ROUTE_HOME) {
                    popUpTo(Constants.ROUTE_HOME) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF7B2FBE),
                selectedTextColor = Color(0xFF7B2FBE),
                indicatorColor = Color(0xFFF3E8FF),
                unselectedIconColor = Color(0xFF9E9E9E),
                unselectedTextColor = Color(0xFF9E9E9E)
            )
        )

        NavigationBarItem(
            selected = currentRoute == Constants.ROUTE_MY_TICKET,
            onClick = {
                navController.navigate(Constants.ROUTE_MY_TICKET) {
                    popUpTo(Constants.ROUTE_HOME) { inclusive = false }
                    launchSingleTop = true
                }
            },
            // แก้ตรง icon My Ticket
            icon = { Icon(Icons.Default.DateRange, contentDescription = "My Ticket") },            label = { Text("My Ticket", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF7B2FBE),
                selectedTextColor = Color(0xFF7B2FBE),
                indicatorColor = Color(0xFFF3E8FF),
                unselectedIconColor = Color(0xFF9E9E9E),
                unselectedTextColor = Color(0xFF9E9E9E)
            )
        )

        NavigationBarItem(
            selected = currentRoute == Constants.ROUTE_PROFILE,
            onClick = {
                navController.navigate(Constants.ROUTE_PROFILE) {
                    popUpTo(Constants.ROUTE_HOME) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF7B2FBE),
                selectedTextColor = Color(0xFF7B2FBE),
                indicatorColor = Color(0xFFF3E8FF),
                unselectedIconColor = Color(0xFF9E9E9E),
                unselectedTextColor = Color(0xFF9E9E9E)
            )
        )
    }
}