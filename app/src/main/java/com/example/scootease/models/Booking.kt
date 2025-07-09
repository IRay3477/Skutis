package com.example.scootease.models

import androidx.compose.ui.graphics.Color
import com.example.scootease.activity.Bike

// Enum untuk status pemesanan
enum class BookingStatus(val displayName: String, val color: Color) {
    ONGOING("Ongoing", Color(0xFF1E88E5)), // Biru
    COMPLETED("Completed", Color(0xFF43A047)), // Hijau
    CANCELLED("Cancelled", Color(0xFFE53935)) // Merah
}

// Data class untuk satu item pemesanan
data class Booking(
    val id: String,
    val bike: Bike, // Kita gunakan lagi model Bike yang sudah ada
    val startDate: String,
    val endDate: String,
    val totalPrice: String,
    val status: BookingStatus
)