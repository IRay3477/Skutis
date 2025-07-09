package com.example.scootease.activity

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.scootease.models.Booking
import com.example.scootease.models.BookingStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainScreen(
    username: String,
    email: String,
    allBikes: List<Bike>,
    bookings: List<Booking>,
    onDeleteBooking: (Booking) -> Unit,
    onLogout: () -> Unit,
    onBikeSelectedForBooking: (bike: Bike, startDate: Long, endDate: Long) -> Unit,
    onNavigateTo: (AppScreen) -> Unit,
    activeTab: Int,
    onTabSelected: (Int) -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) }
    var bookingRequest by remember { mutableStateOf<BookingRequest?>(null) }
    var bookings by remember { mutableStateOf(sampleBookings) }

    if (bookingRequest != null) {
        BookingDetailScreen(
            bike = bookingRequest!!.bike,
            startDateMillis = bookingRequest!!.startDate,
            endDateMillis = bookingRequest!!.endDate,
            onNavigateBack = { bookingRequest = null },
            onConfirmBooking = {
                val newBooking = Booking("SC-00${bookings.size + 1}", bookingRequest!!.bike, SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(
                    Date(bookingRequest!!.startDate)
                ), SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(bookingRequest!!.endDate)), "IDR ...", BookingStatus.ONGOING)
                bookings = bookings + newBooking
                bookingRequest = null
                activeTab = 2
                // Toast.makeText(context, "Motorbike successfully booked!", Toast.LENGTH_SHORT).show()
            }
        )
    } else {
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
                        onBikeSelected = { bike, startDate, endDate ->
                            bookingRequest = BookingRequest(bike, startDate, endDate)
                        }
                    )
                    1 -> MapScreen(onNavigateBack = { activeTab = 0 })
                    2 -> BookingsScreen(
                        bookings = bookings,
                        onDeleteBooking = onDeleteBooking
                    )
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
}
