package com.example.ticketpop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.ticketpop.ui.concert.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val viewModel: ConcertDetailViewModel = viewModel()

    val concert = viewModel.concert.value
    val isLoading = viewModel.isLoading.value

    LaunchedEffect(Unit) {
        viewModel.loadConcertDetail(1)
    }

    if (isLoading || concert == null) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = "concertDetail"
    ) {

        composable("concertDetail") {

            ConcertDetailScreen(
                concert = concert,
                onBackClick = { navController.popBackStack() },
                onVenueClick = { navController.navigate("venueInfo") },
                onArtistClick = { navController.navigate("artistInfo") },
                onSelectZoneClick = { navController.navigate("seat") }
            )
        }

        composable("venueInfo") {

            VenueInfoScreen(
                concert = concert,
                onBack = { navController.popBackStack() }
            )
        }

        composable("artistInfo") {

            ArtistInfoScreen(
                concert = concert,
                onBack = { navController.popBackStack() }
            )
        }

        composable("seat") {

            Text("Seat Map Screen ของเพื่อน")
        }
    }
}