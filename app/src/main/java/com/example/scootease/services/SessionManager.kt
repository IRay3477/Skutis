package com.example.scootease.services

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("scootease_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_EMAIL = "email"
        const val KEY_ROLE = "role"
        const val KEY_USERNAME = "username"
    }

    fun saveLogin(email: String, role: String, username: String) {
        prefs.edit {
            putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_EMAIL, email)
                .putString(KEY_ROLE, role)
                .putString(KEY_USERNAME, username)
        }
    }

    fun clearSession() {
        prefs.edit { clear() }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun getUserEmail(): String? {
        return prefs.getString("email", null)
    }

    fun getUserRole(): String? {
        return prefs.getString("role", null)
    }

    fun getUserName(): String? {
        return prefs.getString("username", null)
    }
}