package com.example.passwordmanager.data

import android.content.Context
import android.content.SharedPreferences
import com.example.passwordmanager.model.PasswordEntry
import com.example.passwordmanager.model.CreditCardEntry
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.UUID

class PasswordRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("PasswordManager", Context.MODE_PRIVATE)
    private val gson = GsonBuilder()
        .registerTypeAdapter(PasswordEntry::class.java, PasswordEntryDeserializer())
        .registerTypeAdapter(CreditCardEntry::class.java, CreditCardEntryDeserializer())
        .create()
    private val passwordList = mutableListOf<PasswordEntry>()
    private val creditCardList = mutableListOf<CreditCardEntry>()

    // Custom deserializer to handle missing fields in existing data
    private class PasswordEntryDeserializer : JsonDeserializer<PasswordEntry> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): PasswordEntry {
            val jsonObject = json?.asJsonObject ?: JsonObject()
            
            return PasswordEntry(
                context = jsonObject.get("context")?.asString ?: "",
                username = jsonObject.get("username")?.asString ?: "",
                password = jsonObject.get("password")?.asString ?: "",
                expiryDate = jsonObject.get("expiryDate")?.asString ?: "",
                notes = jsonObject.get("notes")?.asString ?: "",
                id = jsonObject.get("id")?.asString ?: ""
            )
        }
    }

    // Custom deserializer to handle missing fields in existing credit card data
    private class CreditCardEntryDeserializer : JsonDeserializer<CreditCardEntry> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): CreditCardEntry {
            val jsonObject = json?.asJsonObject ?: JsonObject()
            
            return CreditCardEntry(
                cardNumber = jsonObject.get("cardNumber")?.asString ?: "",
                cardHolder = jsonObject.get("cardHolder")?.asString ?: "",
                bankName = jsonObject.get("bankName")?.asString ?: "",
                cardType = jsonObject.get("cardType")?.asString ?: "",
                expiryDate = jsonObject.get("expiryDate")?.asString ?: "",
                cvv = jsonObject.get("cvv")?.asString ?: "",
                notes = jsonObject.get("notes")?.asString ?: "",
                id = jsonObject.get("id")?.asString ?: ""
            )
        }
    }

    init {
        loadData()
    }

    private fun loadData() {
        try {
            // Load passwords with custom deserializer that handles missing fields
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
        } catch (e: Exception) {
            // If there's any error loading data, clear it and start fresh
            passwordList.clear()
            creditCardList.clear()
            sharedPreferences.edit().clear().apply()
        }
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

    fun updateCreditCard(creditCardEntry: CreditCardEntry) {
        val index = creditCardList.indexOfFirst { it.id == creditCardEntry.id }
        if (index != -1) {
            creditCardList[index] = creditCardEntry
            saveData()
        }
    }

    fun deleteCreditCard(cardId: String) {
        creditCardList.removeAll { it.id == cardId }
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