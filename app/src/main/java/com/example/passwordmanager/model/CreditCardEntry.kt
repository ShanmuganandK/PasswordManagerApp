package com.example.passwordmanager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreditCardEntry(
    val cardNumber: String = "",
    val cardHolder: String = "",
    val bankName: String = "",
    val cardType: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    val notes: String = "",
    val id: String = ""
) : Parcelable