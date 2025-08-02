package com.example.passwordmanager.utils

import java.security.SecureRandom

object PasswordGenerator {
    private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DIGITS = "0123456789"
    private const val SPECIAL_CHARACTERS = "!@#$%^&*()-_=+<>?"

    fun generatePassword(length: Int, useLowercase: Boolean, useUppercase: Boolean, useDigits: Boolean, useSpecialCharacters: Boolean): String {
        val characterPool = StringBuilder()
        if (useLowercase) characterPool.append(LOWERCASE)
        if (useUppercase) characterPool.append(UPPERCASE)
        if (useDigits) characterPool.append(DIGITS)
        if (useSpecialCharacters) characterPool.append(SPECIAL_CHARACTERS)

        if (characterPool.isEmpty()) {
            throw IllegalArgumentException("At least one character type must be selected")
        }

        val random = SecureRandom()
        return (1..length)
            .map { characterPool[random.nextInt(characterPool.length)] }
            .joinToString("")
    }
}