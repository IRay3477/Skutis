package com.example.scootease
import androidx.annotation.DrawableRes

data class Bike(
    val id: Int,
    val name: String,
    val specs: String,
    val price: String,
    val rating: Double,
    @DrawableRes val imageRes: Int
)