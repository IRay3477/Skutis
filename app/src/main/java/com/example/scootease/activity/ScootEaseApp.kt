package com.example.scootease.activity

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
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

private data class BookingRequest(val bike: Bike, val startDate: Long, val endDate: Long)

// Data motor yang baru, sesuai dengan yang Anda berikan
val allBikes = listOf(
    Bike(1, "Honda Vario 160", "160cc · Auto", "85k", 4.9, R.drawable.honda_vario, BikeStatus.UNAVAILABLE, BikeType.MATIC),
    Bike(2, "Yamaha NMAX", "155cc · Auto", "120k", 4.8, R.drawable.yamaha_nmax, BikeStatus.AVAILABLE, BikeType.MATIC),
    Bike(3, "Honda Scoopy", "110cc · Auto", "75k", 4.9, R.drawable.honda_scoopy, BikeStatus.AVAILABLE, BikeType.MATIC),
    Bike(4, "Honda PCX", "150cc · Auto", "150k", 4.7, R.drawable.honda_pcx, BikeStatus.UNAVAILABLE, BikeType.MATIC),
    Bike(5, "Harley Sporster 48", "1200cc · Manual", "2000k", 4.6, R.drawable.harley_48, BikeStatus.AVAILABLE, BikeType.MANUAL),
    Bike(6, "BMW R 1200 GS", "1200cc · Manual", "5000k", 4.5, R.drawable.bmw_r1200gs, BikeStatus.AVAILABLE, BikeType.MANUAL),
    Bike(7, "Harley Road Glide", "1800cc · Manual", "5500k", 4.5, R.drawable.harley_rg, BikeStatus.AVAILABLE, BikeType.MANUAL)
)


@Composable
fun ScootEaseApp() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var isAuthenticated by rememberSaveable { mutableStateOf(sessionManager.isLoggedIn()) }

    var bookingRequest by remember { mutableStateOf<BookingRequest?>(null) }
    var bookings by remember { mutableStateOf(sampleBookings) }
    var mainScreenActiveTab by remember { mutableIntStateOf(0) }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (isAuthenticated) {
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
                            totalPrice = "IDR ...",
                            status = BookingStatus.ONGOING
                        )
                        bookings = bookings + newBooking
                        bookingRequest = null
                        mainScreenActiveTab = 2
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
                    onLogout = {
                        sessionManager.clearSession()
                        isAuthenticated = false
                    },
                    onBikeSelectedForBooking = { bike, startDate, endDate ->
                        bookingRequest = BookingRequest(bike, startDate, endDate)
                    },
                    activeTab = mainScreenActiveTab,
                    onTabSelected = { mainScreenActiveTab = it }
                )
            }
        } else {
            AuthScreen(onAuthSuccess = { user ->
                sessionManager.saveLogin(user.email, user.role, user.username)
                isAuthenticated = true
            })
        }
    }
}