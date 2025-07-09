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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.scootease.helpers.DBHelper
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
    val dbHelper = remember { DBHelper(context) }
    val sessionManager = remember { SessionManager(context) }

    // --- State Management Terpusat ---
    var currentScreen by remember { mutableStateOf(if (sessionManager.isLoggedIn()) AppScreen.MAIN else AppScreen.AUTH) }
    var bookingRequest by remember { mutableStateOf<BookingRequest?>(null) }
    var bookings by remember { mutableStateOf(dbHelper.getAllBookings()) }
    var mainScreenActiveTab by rememberSaveable { mutableIntStateOf(0) }

    Surface(modifier = Modifier.fillMaxSize()) {
        // --- Logika Navigasi Tunggal ---
        when (currentScreen) {
            AppScreen.AUTH -> {
                AuthScreen(onAuthSuccess = { user ->
                    sessionManager.saveLogin(user.email, user.role, user.username)
                    currentScreen = AppScreen.MAIN
                })
            }
            AppScreen.MAIN -> {
                if (bookingRequest != null) {
                    BookingDetailScreen(
                        bike = bookingRequest!!.bike,
                        startDateMillis = bookingRequest!!.startDate,
                        endDateMillis = bookingRequest!!.endDate,
                        onNavigateBack = { bookingRequest = null },
                        onConfirmBooking = {
                            val newBooking = Booking(
                                id = "SC-00${bookings.size + 1}",
                                bike = bookingRequest!!.bike,
                                startDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(bookingRequest!!.startDate)),
                                endDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(bookingRequest!!.endDate)),
                                totalPrice = "IDR ...", // Kalkulasi bisa disempurnakan
                                status = BookingStatus.ONGOING
                            )
                            dbHelper.insertBooking(newBooking)
                            bookings = dbHelper.getAllBookings() // Muat ulang dari DB
                            bookingRequest = null
                            mainScreenActiveTab = 2 // Pindah ke tab booking setelah konfirmasi
                            Toast.makeText(context, "Motorbike successfully booked!", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    val username = sessionManager.getUserName() ?: "Pengguna"
                    val email = sessionManager.getUserEmail() ?: "Tidak ada email"
                    MainScreen(
                        username = username,
                        email = email,
                        allBikes = allBikes,
                        bookings = bookings,
                        onDeleteBooking = { bookingToDelete ->
                            if (dbHelper.deleteBooking(bookingToDelete.id)) {
                                bookings = dbHelper.getAllBookings() // Muat ulang dari DB
                                Toast.makeText(context, "Riwayat dihapus", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Gagal menghapus riwayat", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onLogout = {
                            sessionManager.clearSession()
                            currentScreen = AppScreen.AUTH
                        },
                        onBikeSelectedForBooking = { bike, startDate, endDate ->
                            bookingRequest = BookingRequest(bike, startDate, endDate)
                        },
                        onNavigateTo = { screen -> currentScreen = screen },
                        activeTab = mainScreenActiveTab,
                        onTabSelected = { newTabIndex -> mainScreenActiveTab = newTabIndex }
                    )
                }
            }
            AppScreen.DOC_VERIFICATION -> DocumentVerificationScreen(onNavigateBack = { currentScreen = AppScreen.MAIN })
            AppScreen.HELP -> HelpScreen(onNavigateBack = { currentScreen = AppScreen.MAIN })
            AppScreen.ABOUT_US -> AboutUsScreen(onNavigateBack = { currentScreen = AppScreen.MAIN })
        }
    }
}