package com.example.scootease.models

data class User(
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val alamat: List<Alamat>? = emptyList(),
    val no_hp: String? = null,
    val tanggal_lahir: String? = null,
    val role: String = "user"
)