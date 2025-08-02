package com.example.passwordmanager.model

data class PasswordEntry(
    val context: String,
    val password: String,
    val expiryDate: String = "",
    val id: String = ""
)