package com.example.ticketpop.ui.concert

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

import com.example.ticketpop.R
import com.example.ticketpop.data.model.Concert




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConcertDetailScreen(
    concert: Concert,
    onBackClick: () -> Unit,
    onVenueClick: () -> Unit,
    onArtistClick: () -> Unit,
    onSelectZoneClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("รายละเอียดคอนเสิร์ต", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },

        bottomBar = {
            Surface(shadowElevation = 10.dp) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "ราคาเริ่มต้น",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )

                        Text(
                            text = if (concert.minPrice != null && concert.minPrice > 0)
                                "฿ ${String.format("%,.0f", concert.minPrice)}"
                            else
                                "ดูราคาในโซน",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { onSelectZoneClick() },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("เลือกโซนที่นั่ง")
                    }
                }
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            AsyncImage(
                model = concert.posterImageUrl,
                contentDescription = "Concert Poster",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.poster),
                error = painterResource(R.drawable.poster)
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = concert.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = concert.description ?: "",
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // DATE CARD
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF2F2F7)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {

                            Text(
                                text = concert.showDate,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = concert.showTime,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // VENUE CARD
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF2F2F7)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onVenueClick() }
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = concert.venueName,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "ดูแผนที่และการเดินทาง",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }

                // ARTIST CARD
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF2F2F7)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onArtistClick() }
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Artist",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "รายชื่อศิลปิล",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

