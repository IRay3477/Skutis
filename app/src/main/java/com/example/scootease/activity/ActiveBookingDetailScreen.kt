package com.example.scootease.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scootease.R
import com.example.scootease.models.Booking
import com.example.scootease.models.BookingStatus
import com.example.scootease.ui.theme.ScootEaseTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveBookingDetailScreen(
    booking: Booking,
    onNavigateBack: () -> Unit
) {
    // Fungsi untuk menghitung sisa hari
    val remainingDays = remember(booking.endDate) {
        try {
            val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
            val endDate = dateFormat.parse(booking.endDate)
            val diff = endDate.time - System.currentTimeMillis()
            // Jika sudah lewat, tampilkan 0 hari
            TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(0)
        } catch (e: Exception) {
            -1 // Kembalikan nilai error jika format tanggal salah
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan Aktif") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Detail Motor
            Image(
                painter = painterResource(id = booking.bike.imageRes),
                contentDescription = booking.bike.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )
            Text(booking.bike.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            // Detail Pesanan
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailItem(icon = Icons.Default.CalendarMonth, label = "Tanggal Mulai", value = booking.startDate)
                    DetailItem(icon = Icons.Default.CalendarMonth, label = "Tanggal Selesai", value = booking.endDate)
                    DetailItem(icon = Icons.Default.Money, label = "Total Harga", value = booking.totalPrice)
                    DetailItem(icon = Icons.Default.Info, label = "Status", value = booking.status.displayName, valueColor = booking.status.color)
                    if (remainingDays >= 0) {
                        DetailItem(icon = Icons.Default.Timer, label = "Sisa Waktu", value = "$remainingDays hari lagi", valueColor = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(icon: ImageVector, label: String, value: String, valueColor: Color = LocalContentColor.current) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = valueColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActiveBookingDetailScreenPreview() {
    val sampleBooking = Booking(
        id = "SC-PREVIEW",
        bike = Bike(1, "Yamaha NMAX", "155cc · Auto", "120k", 4.8, R.drawable.yamaha_nmax, com.example.scootease.models.BikeStatus.UNAVAILABLE, com.example.scootease.models.BikeType.MATIC),
        startDate = "9 Jul 2025",
        endDate = "12 Jul 2025",
        totalPrice = "IDR 360.000,00",
        status = BookingStatus.ONGOING
    )
    ScootEaseTheme {
        ActiveBookingDetailScreen(booking = sampleBooking, onNavigateBack = {})
    }
}