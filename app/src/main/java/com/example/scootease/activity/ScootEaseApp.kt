package com.example.scootease.activity

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.scootease.services.SessionManager

@Composable
fun ScootEaseApp() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var isAuthenticated by rememberSaveable {
        mutableStateOf(sessionManager.isLoggedIn())
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (isAuthenticated) {
            MainScreen(onLogout = {
                sessionManager.clearSession()
                isAuthenticated = false
            })
        } else {
            AuthScreen(onAuthSuccess = { email, role ->
                sessionManager.saveLogin(email, role)
                isAuthenticated = true
            })
        }
    }
}