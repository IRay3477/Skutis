package com.example.scootease.activity
import androidx.annotation.DrawableRes
import com.example.scootease.models.BikeStatus
import com.example.scootease.models.BikeType

data class Bike(
    val id: Int,
    val name: String,
    val specs: String,
    val price: String,
    val rating: Double,
    @DrawableRes val imageRes: Int,
    val status: BikeStatus,
    val type: BikeType
)