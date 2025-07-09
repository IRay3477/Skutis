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
    // --- PERUBAHAN: Terima dua daftar terpisah ---
    ongoingBookings: List<Booking>,
    historyBookings: List<Booking>,
    onDeleteBooking: (Booking) -> Unit,
    onLogout: () -> Unit,
    onBikeSelectedForBooking: (bike: Bike, startDate: Long, endDate: Long) -> Unit,
    onNavigateTo: (AppScreen) -> Unit,
    activeTab: Int,
    onTabSelected: (Int) -> Unit,
    onCompleteBooking: (Booking) -> Unit
) {
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
                        onClick = { onTabSelected(index) },
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
                    onNavigateToProfile = { onTabSelected(3) },
                    onBikeSelected = { bike, startDate, endDate ->
                        onBikeSelectedForBooking(bike, startDate, endDate)
                    }
                )
                1 -> MapScreen(onNavigateBack = { onTabSelected(0) })
                2 -> BookingsScreen(
                    // --- PERUBAHAN: Berikan dua daftar terpisah ---
                    ongoingBookings = ongoingBookings,
                    historyBookings = historyBookings,
                    onDeleteBooking = onDeleteBooking,
                    onCompleteBooking = onCompleteBooking
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