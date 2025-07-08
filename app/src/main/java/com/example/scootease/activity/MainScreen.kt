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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun MainScreen(onLogout: () -> Unit) {
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf("Home", "Map", "Bookings", "Profile")
                val icons = listOf(Icons.Outlined.Home, Icons.Outlined.Map, Icons.Outlined.Article, Icons.Outlined.Person)
                val filledIcons = listOf(Icons.Filled.Home, Icons.Filled.Map, Icons.Filled.Article, Icons.Filled.Person)

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        icon = {
                            val icon = if (selectedItem == index) filledIcons[index] else icons[index]
                            Icon(icon, contentDescription = item)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItem) {
                0 -> HomeScreen(onNavigateToProfile = { selectedItem = 3 })
                1 -> MapScreen(onNavigateBack = { selectedItem = 0 })
                2 -> CenterText(text = "Halaman Booking")
                3 -> ProfileScreen(onLogoutClick = onLogout)
            }
        }
    }
}