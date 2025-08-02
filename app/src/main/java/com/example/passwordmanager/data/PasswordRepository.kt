package com.example.passwordmanager.data

import android.content.Context
import android.content.SharedPreferences
import com.example.passwordmanager.model.PasswordEntry
import com.example.passwordmanager.model.CreditCardEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class PasswordRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("PasswordManager", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val passwordList = mutableListOf<PasswordEntry>()
    private val creditCardList = mutableListOf<CreditCardEntry>()

    init {
        loadData()
    }

    private fun loadData() {
        // Load passwords
        val passwordJson = sharedPreferences.getString("passwords", "[]")
        val passwordType = object : TypeToken<List<PasswordEntry>>() {}.type
        val loadedPasswords = gson.fromJson<List<PasswordEntry>>(passwordJson, passwordType) ?: emptyList()
        passwordList.clear()
        passwordList.addAll(loadedPasswords)

        // Load credit cards
        val creditCardJson = sharedPreferences.getString("credit_cards", "[]")
        val creditCardType = object : TypeToken<List<CreditCardEntry>>() {}.type
        val loadedCreditCards = gson.fromJson<List<CreditCardEntry>>(creditCardJson, creditCardType) ?: emptyList()
        creditCardList.clear()
        creditCardList.addAll(loadedCreditCards)
    }

    private fun saveData() {
        val passwordJson = gson.toJson(passwordList)
        val creditCardJson = gson.toJson(creditCardList)
        
        sharedPreferences.edit()
            .putString("passwords", passwordJson)
            .putString("credit_cards", creditCardJson)
            .apply()
    }

    fun addPassword(passwordEntry: PasswordEntry): PasswordEntry {
        val newPassword = passwordEntry.copy(id = UUID.randomUUID().toString())
        passwordList.add(newPassword)
        saveData()
        return newPassword
    }

    fun getAllPasswords(): List<PasswordEntry> {
        return passwordList.toList()
    }

    fun updatePassword(passwordEntry: PasswordEntry) {
        val index = passwordList.indexOfFirst { it.id == passwordEntry.id }
        if (index != -1) {
            passwordList[index] = passwordEntry
            saveData()
        }
    }

    fun deletePassword(passwordId: String) {
        passwordList.removeAll { it.id == passwordId }
        saveData()
    }

    fun addCreditCard(creditCardEntry: CreditCardEntry): CreditCardEntry {
        val newCreditCard = creditCardEntry.copy(id = UUID.randomUUID().toString())
        creditCardList.add(newCreditCard)
        saveData()
        return newCreditCard
    }

    fun getAllCreditCards(): List<CreditCardEntry> {
        return creditCardList.toList()
    }

    fun getPasswordById(passwordId: String): PasswordEntry? {
        return passwordList.find { it.id == passwordId }
    }

    fun getCreditCardById(cardId: String): CreditCardEntry? {
        return creditCardList.find { it.id == cardId }
    }

    fun getExpiredPasswords(): List<PasswordEntry> {
        val currentDate = java.time.LocalDate.now()
        return passwordList.filter { password ->
            if (password.expiryDate.isNotEmpty()) {
                try {
                    val expiryDate = java.time.LocalDate.parse(password.expiryDate)
                    expiryDate.isBefore(currentDate)
                } catch (e: Exception) {
                    false
                }
            } else {
                false
            }
        }
    }

    // Additional methods for context-specific storage and expiry notifications can be added here
}