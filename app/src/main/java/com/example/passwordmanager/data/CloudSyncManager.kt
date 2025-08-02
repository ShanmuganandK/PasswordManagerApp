package com.example.passwordmanager.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.passwordmanager.model.PasswordEntry
import com.example.passwordmanager.model.CreditCardEntry

class CloudSyncManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun syncPasswords(passwords: List<PasswordEntry>, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onComplete(false)
        val passwordMaps = passwords.map { password ->
            mapOf(
                "context" to password.context,
                "password" to password.password,
                "expiryDate" to password.expiryDate,
                "id" to password.id
            )
        }
        firestore.collection("users").document(userId).set(mapOf("passwords" to passwordMaps))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun syncCreditCards(creditCards: List<CreditCardEntry>, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onComplete(false)
        val creditCardMaps = creditCards.map { card ->
            mapOf(
                "cardNumber" to card.cardNumber,
                "cardHolder" to card.cardHolder,
                "expiryDate" to card.expiryDate,
                "cvv" to card.cvv,
                "frontImageUri" to card.frontImageUri,
                "backImageUri" to card.backImageUri,
                "id" to card.id
            )
        }
        firestore.collection("users").document(userId).set(mapOf("creditCards" to creditCardMaps))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun fetchPasswords(onFetch: (List<PasswordEntry>?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onFetch(null)
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val passwordMaps = document.get("passwords") as? List<Map<String, Any>>
                val passwords = passwordMaps?.map { map ->
                    PasswordEntry(
                        context = map["context"] as? String ?: "",
                        password = map["password"] as? String ?: "",
                        expiryDate = map["expiryDate"] as? String ?: "",
                        id = map["id"] as? String ?: ""
                    )
                }
                onFetch(passwords)
            }
            .addOnFailureListener { onFetch(null) }
    }

    fun fetchCreditCards(onFetch: (List<CreditCardEntry>?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onFetch(null)
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val creditCardMaps = document.get("creditCards") as? List<Map<String, Any>>
                val creditCards = creditCardMaps?.map { map ->
                    CreditCardEntry(
                        cardNumber = map["cardNumber"] as? String ?: "",
                        cardHolder = map["cardHolder"] as? String ?: "",
                        expiryDate = map["expiryDate"] as? String ?: "",
                        cvv = map["cvv"] as? String ?: "",
                        frontImageUri = map["frontImageUri"] as? String ?: "",
                        backImageUri = map["backImageUri"] as? String ?: "",
                        id = map["id"] as? String ?: ""
                    )
                }
                onFetch(creditCards)
            }
            .addOnFailureListener { onFetch(null) }
    }
}