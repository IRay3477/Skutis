package com.example.scootease.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scootease.activity.Bike
import com.example.scootease.ui.theme.ScootEaseTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bike: Bike,
    startDateMillis: Long,
    endDateMillis: Long,
    onConfirmBooking: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // Helper untuk memformat tanggal
    fun formatMillisToDate(millis: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // Menghitung durasi sewa dalam hari
    val durationInDays by remember(startDateMillis, endDateMillis) {
        derivedStateOf {
            val diff = endDateMillis - startDateMillis
            TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(1) // Minimal sewa 1 hari
        }
    }

    // Menghitung total biaya
    val totalPrice by remember(durationInDays) {
        derivedStateOf {
            val pricePerDay = bike.price.filter { it.isDigit() }.toIntOrNull() ?: 0
            val total = pricePerDay * 1000 * durationInDays
            // Format ke dalam Rupiah
            NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(total)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirm Booking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // Bagian bawah layar dengan tombol konfirmasi
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Cost", style = MaterialTheme.typography.bodyMedium)
                        Text(totalPrice, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    }
                    Button(
                        onClick = onConfirmBooking,
                        modifier = Modifier.height(50.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Confirm Book")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Detail Motor
            BikeDetailItem(bike = bike)
            Spacer(modifier = Modifier.height(24.dp))

            // Detail Pesanan
            BookingSummaryItem(
                startDate = formatMillisToDate(startDateMillis),
                endDate = formatMillisToDate(endDateMillis),
                durationInDays = durationInDays
            )
        }
    }
}

@Composable
fun BikeDetailItem(bike: Bike) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Image(
                painter = painterResource(id = bike.imageRes),
                contentDescription = bike.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(bike.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, "Rating", tint = Color(0xFFFFC700), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${bike.rating}  ·  ${bike.specs}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun BookingSummaryItem(startDate: String, endDate: String, durationInDays: Long) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Booking Detail", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Starting Date", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Starting Date", style = MaterialTheme.typography.labelMedium)
                    Text(startDate, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                }
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Ending Date", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Ending Date", style = MaterialTheme.typography.labelMedium)
                    Text(endDate, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                }
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = "Duration", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Total Book Duration", style = MaterialTheme.typography.labelMedium)
                    Text("$durationInDays Days", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}