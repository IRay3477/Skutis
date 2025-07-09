package com.example.scootease.activity

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.scootease.models.Booking
import com.example.scootease.models.BookingStatus
import com.example.scootease.services.SessionManager
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.runtime.remember
import com.example.scootease.R
import com.example.scootease.models.BikeStatus
import com.example.scootease.models.BikeType
import java.util.Locale

enum class AppScreen {
    AUTH,
    MAIN,
    DOC_VERIFICATION,
    HELP,
    ABOUT_US
}

data class BookingRequest(val bike: Bike, val startDate: Long, val endDate: Long)

val allBikes = listOf(
    Bike(1, "Honda Vario 160", "160cc · Auto", "85k", 4.9, R.drawable.honda_vario, BikeStatus.UNAVAILABLE, BikeType.MATIC),
    Bike(2, "Yamaha NMAX", "155cc · Auto", "120k", 4.8, R.drawable.yamaha_nmax, BikeStatus.AVAILABLE, BikeType.MATIC),
    Bike(3, "Honda Scoopy", "110cc · Auto", "75k", 4.9, R.drawable.honda_scoopy, BikeStatus.AVAILABLE, BikeType.MATIC),
    Bike(4, "Honda PCX", "150cc · Auto", "150k", 4.7, R.drawable.honda_pcx, BikeStatus.UNAVAILABLE, BikeType.MATIC),
    Bike(5, "Harley Sportster 48", "1200cc · Manual", "2000k", 4.6, R.drawable.harley_48, BikeStatus.AVAILABLE, BikeType.MANUAL),
    Bike(6, "BMW R 1200 GS", "1200cc · Manual", "5000k", 4.5, R.drawable.bmw_r1200gs, BikeStatus.AVAILABLE, BikeType.MANUAL),
    Bike(7, "Harley Road Glide", "1800cc · Manual", "5500k", 4.5, R.drawable.harley_rg, BikeStatus.AVAILABLE, BikeType.MANUAL)
)

@Composable
fun ScootEaseApp() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // State untuk mengontrol layar mana yang sedang aktif
    var currentScreen by remember { mutableStateOf(if (sessionManager.isLoggedIn()) AppScreen.MAIN else AppScreen.AUTH) }

    // State untuk data yang bisa berubah
    var bookingRequest by remember { mutableStateOf<BookingRequest?>(null) }
    var bookings by remember { mutableStateOf(sampleBookings) }

    Surface(modifier = Modifier.fillMaxSize()) {
        // --- PERBAIKAN: Gunakan SATU blok `when` untuk semua navigasi ---
        when (currentScreen) {
            AppScreen.AUTH -> {
                AuthScreen(onAuthSuccess = { user ->
                    sessionManager.saveLogin(user.email, user.role, user.username)
                    currentScreen = AppScreen.MAIN
                })
            }
            AppScreen.MAIN -> {
                // Saat di layar utama, kita cek apakah ada permintaan booking
                if (bookingRequest != null) {
                    BookingDetailScreen(
                        bike = bookingRequest!!.bike,
                        startDateMillis = bookingRequest!!.startDate,
                        endDateMillis = bookingRequest!!.endDate,
                        onNavigateBack = { bookingRequest = null }, // Kembali ke MainScreen
                        onConfirmBooking = {
                            val newBooking = Booking(
                                id = "SC-00${bookings.size + 1}",
                                bike = bookingRequest!!.bike,
                                startDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(bookingRequest!!.startDate)),
                                endDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(bookingRequest!!.endDate)),
                                totalPrice = "IDR ...",
                                status = BookingStatus.ONGOING
                            )
                            bookings = bookings + newBooking
                            bookingRequest = null
                            // Tidak perlu mengubah tab secara manual lagi, biarkan MainScreen yang mengaturnya
                            Toast.makeText(context, "Motorbike successfully booked!", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    // Jika tidak ada permintaan booking, tampilkan MainScreen
                    val username = sessionManager.getUserName() ?: "Pengguna"
                    val email = sessionManager.getUserEmail() ?: "Tidak ada email"
                    MainScreen(
                        username = username,
                        email = email,
                        allBikes = allBikes,
                        onLogout = {
                            sessionManager.clearSession()
                            currentScreen = AppScreen.AUTH
                        },
                        onBikeSelectedForBooking = { bike, startDate, endDate ->
                            bookingRequest = BookingRequest(bike, startDate, endDate)
                        },
                        onNavigateTo = { screen -> currentScreen = screen }
                    )
                }
            }
            AppScreen.DOC_VERIFICATION -> DocumentVerificationScreen(onNavigateBack = { currentScreen = AppScreen.MAIN })
            AppScreen.HELP -> HelpScreen(onNavigateBack = { currentScreen = AppScreen.MAIN })
            AppScreen.ABOUT_US -> AboutUsScreen(onNavigateBack = { currentScreen = AppScreen.MAIN })
        }
    }
}

@Composable
fun MainScreen(
    username: String, email: String, allBikes: List<Bike>, onLogout: () -> Unit,
    onBikeSelectedForBooking: (bike: Bike, startDate: Long, endDate: Long) -> Unit,
    onNavigateTo: (AppScreen) -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf("Home", "Map", "Bookings", "Profile")
                val icons = listOf(Icons.Outlined.Home, Icons.Outlined.Map, Icons.Outlined.Article, Icons.Outlined.Person)
                val filledIcons = listOf(Icons.Filled.Home, Icons.Filled.Map, Icons.Filled.Article, Icons.Filled.Person)

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        label = { Text(item) },
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        icon = {
                            val icon = if (activeTab == index) filledIcons[index] else icons[index]
                            Icon(icon, contentDescription = item)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (activeTab) {
                0 -> HomeScreen(
                    allBikes = allBikes,
                    onNavigateToProfile = { onNavigateTo(AppScreen.MAIN); activeTab = 3 },
                    onBikeSelected = onBikeSelectedForBooking
                )
                1 -> MapScreen(onNavigateBack = { activeTab = 0 })
                2 -> BookingsScreen()
                3 -> ProfileScreen(
                    username = username, email = email, onLogoutClick = onLogout,
                    onNavigateToDocVerification = { onNavigateTo(AppScreen.DOC_VERIFICATION) },
                    onNavigateToHelp = { onNavigateTo(AppScreen.HELP) },
                    onNavigateToAbout = { onNavigateTo(AppScreen.ABOUT_US) }
                )
            }
        }
    }
}