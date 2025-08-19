package com.example.passwordmanager.data

import android.content.Context
import com.example.passwordmanager.model.PasswordEntry
import com.example.passwordmanager.model.CreditCardEntry
import com.example.passwordmanager.security.SessionManager

/**
 * Repository manager for the encrypted system only
 * This provides a clean interface for fragments to use
 */
class RepositoryManager(private val context: Context) {
    
    private val secureRepository = SecurePasswordRepositoryImpl(context)
    private val sessionManager = SessionManager.getInstance(context)
    
    /**
     * Get all passwords from the encrypted system
     */
    fun getAllPasswords(): List<PasswordEntry> {
        return try {
            val masterKey = sessionManager.getMasterKey()
            if (masterKey != null) {
                secureRepository.getAllPasswordEntries(masterKey)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get all credit cards from the encrypted system
     */
    fun getAllCreditCards(): List<CreditCardEntry> {
        return try {
            val masterKey = sessionManager.getMasterKey()
            if (masterKey != null) {
                secureRepository.getAllCreditCardEntries(masterKey)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Add a password to the encrypted system
     */
    fun addPassword(passwordEntry: PasswordEntry): PasswordEntry {
        return try {
            val masterKey = sessionManager.getMasterKey()
            if (masterKey != null) {
                secureRepository.addPasswordEntry(passwordEntry, masterKey)
            } else {
                passwordEntry
            }
        } catch (e: Exception) {
            passwordEntry
        }
    }
    
    /**
     * Add a credit card to the encrypted system
     */
    fun addCreditCard(creditCardEntry: CreditCardEntry): CreditCardEntry {
        return try {
            val masterKey = sessionManager.getMasterKey()
            if (masterKey != null) {
                secureRepository.addCreditCardEntry(creditCardEntry, masterKey)
            } else {
                creditCardEntry
            }
        } catch (e: Exception) {
            creditCardEntry
        }
    }
    
    /**
     * Update a password in the encrypted system
     */
    fun updatePassword(passwordEntry: PasswordEntry): Boolean {
        return try {
            val masterKey = sessionManager.getMasterKey()
            if (masterKey != null) {
                secureRepository.updatePasswordEntry(passwordEntry, masterKey)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Update a credit card in the encrypted system
     */
    fun updateCreditCard(creditCardEntry: CreditCardEntry): Boolean {
        return try {
            val masterKey = sessionManager.getMasterKey()
            if (masterKey != null) {
                secureRepository.updateCreditCardEntry(creditCardEntry, masterKey)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Delete a password from the encrypted system
     */
    fun deletePassword(passwordId: String): Boolean {
        return try {
            secureRepository.deletePasswordEntry(passwordId)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Delete a credit card from the encrypted system
     */
    fun deleteCreditCard(cardId: String): Boolean {
        return try {
            secureRepository.deleteCreditCardEntry(cardId)
            true
        } catch (e: Exception) {
            false
        }
    }
}