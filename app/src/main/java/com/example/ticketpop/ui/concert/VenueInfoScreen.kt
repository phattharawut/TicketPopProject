package com.example.ticketpop.ui.concert

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.ticketpop.data.model.Concert
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.ticketpop.R
import androidx.compose.ui.layout.ContentScale

@Composable
fun VenueInfoScreen(
    concert: Concert,
    onBack: () -> Unit
) {

    Column {

        Box {

            Image(
                painter = painterResource(R.drawable.map),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(12.dp)
                    .size(40.dp)
                    .background(
                        Color.White.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = concert.venueName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text("Concert: ${concert.title}")
        }
    }
}
