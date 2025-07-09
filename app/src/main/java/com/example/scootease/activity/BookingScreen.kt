package com.example.scootease.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scootease.R
import com.example.scootease.models.BikeStatus
import com.example.scootease.models.BikeType
import com.example.scootease.models.Booking
import com.example.scootease.models.BookingStatus
import com.example.scootease.ui.theme.ScootEaseTheme

// Data contoh HANYA untuk preview, tidak digunakan dalam aplikasi utama.
val sampleBookings = listOf(
    Booking("SC-001", Bike(1, "Honda Vario 160", "160cc", "85k", 4.9, R.drawable.honda_vario, BikeStatus.AVAILABLE, BikeType.MATIC), "9 Jul 2025", "11 Jul 2025", "IDR 255k", BookingStatus.ONGOING),
    Booking("SC-002", Bike(7, "Harley Road Glide", "1800cc", "5500k", 4.6, R.drawable.harley_rg, BikeStatus.AVAILABLE, BikeType.MANUAL), "1 Jul 2025", "2 Jul 2025", "IDR 5500k", BookingStatus.COMPLETED),
    Booking("SC-004", Bike(2, "Yamaha NMAX", "155cc", "120k", 4.8, R.drawable.yamaha_nmax, BikeStatus.UNAVAILABLE, BikeType.MATIC), "25 Jun 2025", "26 Jun 2025", "IDR 120k", BookingStatus.CANCELLED)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    ongoingBookings: List<Booking>,
    historyBookings: List<Booking>,
    onDeleteBooking: (Booking) -> Unit,
    onCompleteBooking: (Booking) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dalam Proses", "Riwayat")
    var bookingToDelete by remember { mutableStateOf<Booking?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesanan Saya", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // --- PERBAIKAN: Pindahkan TabRow ke dalam Column ---
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> {
                    if (ongoingBookings.isEmpty()) {
                        EmptyState(tabName = tabs[selectedTabIndex])
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(ongoingBookings, key = { it.id }) { booking ->
                                BookingItemCard(booking = booking, onCompleteClick = { onCompleteBooking(booking) })
                            }
                        }
                    }
                }
                1 -> {
                    if (historyBookings.isEmpty()) {
                        EmptyState(tabName = tabs[selectedTabIndex])
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(historyBookings, key = { it.id }) { booking ->
                                BookingItemCard(booking = booking, onDeleteClick = { bookingToDelete = booking })
                            }
                        }
                    }
                }
            }
        }

        if (bookingToDelete != null) {
            DeleteConfirmationDialog(
                booking = bookingToDelete!!,
                onConfirm = {
                    onDeleteBooking(bookingToDelete!!)
                    bookingToDelete = null
                },
                onDismiss = { bookingToDelete = null }
            )
        }
    }
}

// --- PERBAIKAN: Fungsi ini sekarang berada di level atas ---
@Composable
fun BookingItemCard(
    booking: Booking,
    onDeleteClick: (() -> Unit)? = null,
    onCompleteClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = booking.bike.imageRes),
                contentDescription = booking.bike.name,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(booking.bike.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${booking.startDate} - ${booking.endDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(booking.totalPrice, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            when (booking.status) {
                BookingStatus.ONGOING -> {
                    onCompleteClick?.let {
                        Button(onClick = it, modifier = Modifier.height(40.dp)) {
                            Text("Selesaikan")
                        }
                    }
                }
                BookingStatus.COMPLETED, BookingStatus.CANCELLED -> {
                    onDeleteClick?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus Riwayat", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

// --- PERBAIKAN: Fungsi ini sekarang berada di level atas ---
@Composable
fun DeleteConfirmationDialog(
    booking: Booking,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, contentDescription = "Peringatan") },
        title = { Text("Konfirmasi Hapus") },
        text = { Text("Anda yakin ingin menghapus riwayat pesanan ${booking.bike.name}?") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

// --- PERBAIKAN: Fungsi ini sekarang berada di level atas ---
@Composable
fun EmptyState(tabName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tidak ada pesanan di kategori '$tabName'",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun BookingsScreenPreview() {
    ScootEaseTheme {
        // --- PERBAIKAN: Berikan parameter yang benar untuk preview ---
        BookingsScreen(
            ongoingBookings = sampleBookings.filter { it.status == BookingStatus.ONGOING },
            historyBookings = sampleBookings.filter { it.status != BookingStatus.ONGOING },
            onDeleteBooking = {},
            onCompleteBooking = {},
        )
    }
}

@Preview(name = "Ongoing Item Card", showBackground = true)
@Composable
fun BookingItemCardOngoingPreview() {
    val sampleBooking = sampleBookings.first { it.status == BookingStatus.ONGOING }
    ScootEaseTheme {
        BookingItemCard(
            booking = sampleBooking,
            onCompleteClick = {}
        )
    }
}

@Preview(name = "History Item Card", showBackground = true)
@Composable
fun BookingItemCardHistoryPreview() {
    val sampleBooking = sampleBookings.first { it.status == BookingStatus.COMPLETED }
    ScootEaseTheme {
        BookingItemCard(
            booking = sampleBooking,
            onDeleteClick = {}
        )
    }
}