package com.example.passwordmanager.data

import com.example.passwordmanager.crypto.AuthResult
import com.example.passwordmanager.model.PasswordEntry
import com.example.passwordmanager.model.CreditCardEntry
import javax.crypto.SecretKey

/**
 * Repository interface for secure encrypted password and credit card storage
 */
interface SecurePasswordRepository {
    
    // Setup and authentication
    fun setupMasterPassword(password: String): Boolean
    fun authenticateUser(password: String): AuthResult
    fun isEncryptionSetup(): Boolean
    
    // Encrypted password operations
    fun addPasswordEntry(entry: PasswordEntry, masterKey: SecretKey): PasswordEntry
    fun getAllPasswordEntries(masterKey: SecretKey): List<PasswordEntry>
    fun updatePasswordEntry(entry: PasswordEntry, masterKey: SecretKey): Boolean
    fun deletePasswordEntry(id: String): Boolean
    fun getPasswordById(id: String, masterKey: SecretKey): PasswordEntry?
    
    // Encrypted credit card operations
    fun addCreditCardEntry(entry: CreditCardEntry, masterKey: SecretKey): CreditCardEntry
    fun getAllCreditCardEntries(masterKey: SecretKey): List<CreditCardEntry>
    fun updateCreditCardEntry(entry: CreditCardEntry, masterKey: SecretKey): Boolean
    fun deleteCreditCardEntry(id: String): Boolean
    fun getCreditCardById(id: String, masterKey: SecretKey): CreditCardEntry?
    

    
    // Security and maintenance
    fun clearAllData(): Boolean
    
    // Key management
    fun deriveKeyFromPassword(password: String): SecretKey?
}

