package com.example.scootease.activity

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.scootease.helpers.DBHelper
import com.example.scootease.models.Booking
import com.example.scootease.models.BookingStatus
import com.example.scootease.services.SessionManager
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class AppScreen {
    AUTH, MAIN, DOC_VERIFICATION, HELP, ABOUT_US
}

data class BookingRequest(val bike: Bike, val startDate: Long, val endDate: Long)

@Composable
fun ScootEaseApp() {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    val sessionManager = remember { SessionManager(context) }

    var currentScreen by remember { mutableStateOf(if (sessionManager.isLoggedIn()) AppScreen.MAIN else AppScreen.AUTH) }
    var bookingRequest by remember { mutableStateOf<BookingRequest?>(null) }
    var selectedBookingDetail by remember { mutableStateOf<Booking?>(null) }
    var mainScreenActiveTab by rememberSaveable { mutableIntStateOf(0) }

    var allBikes by remember { mutableStateOf<List<Bike>>(emptyList()) }
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = currentScreen) {
        if (currentScreen == AppScreen.MAIN) {
            allBikes = dbHelper.getAllBikes()
            bookings = dbHelper.getAllBookings()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            AppScreen.AUTH -> {
                AuthScreen(onAuthSuccess = { user ->
                    sessionManager.saveLogin(user.email, user.role, user.username)
                    currentScreen = AppScreen.MAIN
                })
            }
            AppScreen.MAIN -> {
                if (selectedBookingDetail != null) {
                    ActiveBookingDetailScreen(booking = selectedBookingDetail!!, onNavigateBack = { selectedBookingDetail = null })
                } else if (bookingRequest != null) {
                    val request = bookingRequest!!
                    val durationInDays = TimeUnit.MILLISECONDS.toDays(request.endDate - request.startDate).coerceAtLeast(1)
                    val pricePerDay = request.bike.price.filter { it.isDigit() }.toIntOrNull() ?: 0
                    val total = pricePerDay * 1000 * durationInDays
                    val totalPriceFormatted = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(total)

                    BookingDetailScreen(
                        bike = request.bike,
                        startDateMillis = request.startDate,
                        endDateMillis = request.endDate,
                        onNavigateBack = { bookingRequest = null },
                        onConfirmBooking = {
                            val newBooking = Booking(
                                id = "SC-${System.currentTimeMillis()}",
                                bike = request.bike,
                                startDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(request.startDate)),
                                endDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(request.endDate)),
                                totalPrice = totalPriceFormatted,
                                status = BookingStatus.ONGOING
                            )
                            coroutineScope.launch {
                                if (dbHelper.insertBooking(newBooking)) {
                                    bookings = dbHelper.getAllBookings()
                                    bookingRequest = null
                                    mainScreenActiveTab = 2
                                }
                            }
                        }
                    )
                } else {
                    val ongoingBookings = bookings.filter { it.status == BookingStatus.ONGOING }
                    val historyBookings = bookings.filter { it.status == BookingStatus.COMPLETED || it.status == BookingStatus.CANCELLED }

                    MainScreen(
                        username = sessionManager.getUserName() ?: "Pengguna",
                        email = sessionManager.getUserEmail() ?: "Tidak ada email",
                        allBikes = allBikes,
                        bikeCount = allBikes.size,
                        ongoingBookings = ongoingBookings,
                        historyBookings = historyBookings,
                        onBookingSelected = { selectedBookingDetail = it },
                        onDeleteBooking = { bookingToDelete ->
                            coroutineScope.launch {
                                if (dbHelper.deleteBooking(bookingToDelete.id)) {
                                    bookings = dbHelper.getAllBookings()
                                }
                            }
                        },
                        onCompleteBooking = { bookingToComplete ->
                            coroutineScope.launch {
                                if (dbHelper.updateBookingStatus(bookingToComplete.id, BookingStatus.COMPLETED)) {
                                    bookings = dbHelper.getAllBookings()
                                }
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