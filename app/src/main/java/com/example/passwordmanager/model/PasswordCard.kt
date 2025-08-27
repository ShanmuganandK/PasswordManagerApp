package com.example.passwordmanager.model

data class PasswordCard(
    val id: String,
    val bankName: String,
    val cardType: String,
    val cardNumber: String,
    val cardHolder: String,
    val expiryDate: String,
    val cvv: String,
    val cardBrand: CardBrand = CardBrand.DEFAULT,
    val gradientType: CardGradient = CardGradient.DEFAULT
)

enum class CardBrand {
    VISA,
    MASTERCARD,
    AMEX,
    DISCOVER,
    DEFAULT
}

enum class CardGradient {
    DEFAULT,
    GOLD,
    PLATINUM,
    BLACK,
    BLUE,
    GREEN
}