package com.example.passwordmanager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PasswordEntry(
    val context: String,
    val username: String = "",
    val password: String,
    val expiryDate: String = "",
    val notes: String = "",
    val id: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable