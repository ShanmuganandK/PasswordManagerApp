package com.example.passwordmanager.model

data class CreditCardEntry(
    val cardNumber: String = "",
    val cardHolder: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    val frontImageUri: String = "",
    val backImageUri: String = "",
    val id: String = ""
)