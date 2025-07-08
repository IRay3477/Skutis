package com.example.scootease.activity

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun ScootEaseApp() {
    var isAuthenticated by rememberSaveable { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (isAuthenticated) {
            // Berikan aksi logout ke MainScreen
            MainScreen(onLogout = { isAuthenticated = false })
        } else {
            AuthScreen(onAuthSuccess = { isAuthenticated = true })
        }
    }
}