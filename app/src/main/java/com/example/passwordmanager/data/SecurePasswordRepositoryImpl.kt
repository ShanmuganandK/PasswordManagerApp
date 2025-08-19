package com.example.passwordmanager.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.example.passwordmanager.crypto.*
import com.example.passwordmanager.model.PasswordEntry
import com.example.passwordmanager.model.CreditCardEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID
import javax.crypto.SecretKey

/**
 * Implementation of SecurePasswordRepository with full encryption support
 */
class SecurePasswordRepositoryImpl(context: Context) : SecurePasswordRepository {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("MaureanManager", Context.MODE_PRIVATE)
    private val cryptoManager: CryptographyManager = CryptographyManagerImpl()
    private val gson = Gson()
    
    // In-memory cache for decrypted data (cleared on app background)
    private var cachedPasswords: List<PasswordEntry>? = null
    private var cachedCreditCards: List<CreditCardEntry>? = null
    private var currentMasterKey: SecretKey? = null
    
    override fun setupMasterPassword(password: String): Boolean {
        return try {
            // Generate salt and derive key
            val salt = cryptoManager.generateSalt()
            val masterKey = cryptoManager.deriveKeyFromPassword(password, salt)
            
            // Create verification token
            val verificationToken = cryptoManager.createVerificationToken(masterKey)
            
            // Store salt and verification token
            val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
            val tokenString = verificationToken.toStorageString()
            
            sharedPreferences.edit()
                .putString(StorageKeys.ENCRYPTION_SALT, saltBase64)
                .putString(StorageKeys.VERIFICATION_TOKEN, tokenString)
                .putInt(StorageKeys.ENCRYPTION_VERSION, StorageKeys.CURRENT_ENCRYPTION_VERSION)
                .apply()
            
            // Clear sensitive data
            cryptoManager.clearKey(masterKey)
            cryptoManager.clearSensitiveData(password.toCharArray())
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override fun authenticateUser(password: String): AuthResult {
        return try {
            if (!isEncryptionSetup()) {
                return AuthResult.SetupRequired
            }
            
            val saltBase64 = sharedPreferences.getString(StorageKeys.ENCRYPTION_SALT, null)
                ?: return AuthResult.DataCorrupted
            
            val tokenString = sharedPreferences.getString(StorageKeys.VERIFICATION_TOKEN, null)
                ?: return AuthResult.DataCorrupted
            
            val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
            val verificationToken = EncryptedData.fromStorageString(tokenString)
                ?: return AuthResult.DataCorrupted
            
            val isValid = cryptoManager.verifyPassword(password, salt, verificationToken)
            
            if (isValid) {
                // Cache the master key for this session
                currentMasterKey = cryptoManager.deriveKeyFromPassword(password, salt)
                AuthResult.Success
            } else {
                AuthResult.InvalidPassword
            }
        } catch (e: Exception) {
            AuthResult.Error("Authentication failed: ${e.message}")
        }
    }
    
    override fun isEncryptionSetup(): Boolean {
        return sharedPreferences.contains(StorageKeys.ENCRYPTION_SALT) &&
               sharedPreferences.contains(StorageKeys.VERIFICATION_TOKEN)
    }
    
    override fun addPasswordEntry(entry: PasswordEntry, masterKey: SecretKey): PasswordEntry {
        val newEntry = entry.copy(
            id = if (entry.id.isEmpty()) UUID.randomUUID().toString() else entry.id,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        val currentPasswords = getAllPasswordEntries(masterKey).toMutableList()
        currentPasswords.add(newEntry)
        
        saveEncryptedPasswords(currentPasswords, masterKey)
        
        // Update cache
        cachedPasswords = currentPasswords
        
        return newEntry
    }
    
    override fun getAllPasswordEntries(masterKey: SecretKey): List<PasswordEntry> {
        // Return cached data if available
        cachedPasswords?.let { return it }
        
        val encryptedData = sharedPreferences.getString(StorageKeys.ENCRYPTED_PASSWORDS, null)
            ?: return emptyList()
        
        return try {
            val decryptedJson = decryptStoredData(encryptedData, masterKey)
            val type = object : TypeToken<List<PasswordEntry>>() {}.type
            val passwords = gson.fromJson<List<PasswordEntry>>(decryptedJson, type) ?: emptyList()
            
            // Cache the decrypted data
            cachedPasswords = passwords
            passwords
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override fun updatePasswordEntry(entry: PasswordEntry, masterKey: SecretKey): Boolean {
        return try {
            val currentPasswords = getAllPasswordEntries(masterKey).toMutableList()
            val index = currentPasswords.indexOfFirst { it.id == entry.id }
            
            if (index != -1) {
                currentPasswords[index] = entry.copy(updatedAt = System.currentTimeMillis())
                saveEncryptedPasswords(currentPasswords, masterKey)
                cachedPasswords = currentPasswords
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    override fun deletePasswordEntry(id: String): Boolean {
        return try {
            val masterKey = currentMasterKey ?: return false
            val currentPasswords = getAllPasswordEntries(masterKey).toMutableList()
            val removed = currentPasswords.removeAll { it.id == id }
            
            if (removed) {
                saveEncryptedPasswords(currentPasswords, masterKey)
                cachedPasswords = currentPasswords
            }
            
            removed
        } catch (e: Exception) {
            false
        }
    }
    
    override fun getPasswordById(id: String, masterKey: SecretKey): PasswordEntry? {
        return getAllPasswordEntries(masterKey).find { it.id == id }
    }
    
    override fun addCreditCardEntry(entry: CreditCardEntry, masterKey: SecretKey): CreditCardEntry {
        val newEntry = entry.copy(
            id = if (entry.id.isEmpty()) UUID.randomUUID().toString() else entry.id,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        val currentCards = getAllCreditCardEntries(masterKey).toMutableList()
        currentCards.add(newEntry)
        
        saveEncryptedCreditCards(currentCards, masterKey)
        
        // Update cache
        cachedCreditCards = currentCards
        
        return newEntry
    }
    
    override fun getAllCreditCardEntries(masterKey: SecretKey): List<CreditCardEntry> {
        // Return cached data if available
        cachedCreditCards?.let { return it }
        
        val encryptedData = sharedPreferences.getString(StorageKeys.ENCRYPTED_CREDIT_CARDS, null)
            ?: return emptyList()
        
        return try {
            val decryptedJson = decryptStoredData(encryptedData, masterKey)
            val type = object : TypeToken<List<CreditCardEntry>>() {}.type
            val creditCards = gson.fromJson<List<CreditCardEntry>>(decryptedJson, type) ?: emptyList()
            
            // Cache the decrypted data
            cachedCreditCards = creditCards
            creditCards
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override fun updateCreditCardEntry(entry: CreditCardEntry, masterKey: SecretKey): Boolean {
        return try {
            val currentCards = getAllCreditCardEntries(masterKey).toMutableList()
            val index = currentCards.indexOfFirst { it.id == entry.id }
            
            if (index != -1) {
                currentCards[index] = entry.copy(updatedAt = System.currentTimeMillis())
                saveEncryptedCreditCards(currentCards, masterKey)
                cachedCreditCards = currentCards
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    override fun deleteCreditCardEntry(id: String): Boolean {
        return try {
            val masterKey = currentMasterKey ?: return false
            val currentCards = getAllCreditCardEntries(masterKey).toMutableList()
            val removed = currentCards.removeAll { it.id == id }
            
            if (removed) {
                saveEncryptedCreditCards(currentCards, masterKey)
                cachedCreditCards = currentCards
            }
            
            removed
        } catch (e: Exception) {
            false
        }
    }
    
    override fun getCreditCardById(id: String, masterKey: SecretKey): CreditCardEntry? {
        return getAllCreditCardEntries(masterKey).find { it.id == id }
    }
    

    
    override fun clearAllData(): Boolean {
        return try {
            sharedPreferences.edit().clear().apply()
            clearCache()
            true
        } catch (e: Exception) {
            false
        }
    }
    

    
    override fun deriveKeyFromPassword(password: String): SecretKey? {
        return try {
            val saltBase64 = sharedPreferences.getString(StorageKeys.ENCRYPTION_SALT, null) ?: return null
            val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
            cryptoManager.deriveKeyFromPassword(password, salt)
        } catch (e: Exception) {
            null
        }
    }
    
    // Private helper methods
    
    private fun saveEncryptedPasswords(passwords: List<PasswordEntry>, masterKey: SecretKey) {
        val json = gson.toJson(passwords)
        val encryptedData = encryptDataForStorage(json, masterKey)
        sharedPreferences.edit()
            .putString(StorageKeys.ENCRYPTED_PASSWORDS, encryptedData)
            .apply()
    }
    
    private fun saveEncryptedCreditCards(creditCards: List<CreditCardEntry>, masterKey: SecretKey) {
        val json = gson.toJson(creditCards)
        val encryptedData = encryptDataForStorage(json, masterKey)
        sharedPreferences.edit()
            .putString(StorageKeys.ENCRYPTED_CREDIT_CARDS, encryptedData)
            .apply()
    }
    
    private fun encryptDataForStorage(data: String, masterKey: SecretKey): String {
        val encryptedData = cryptoManager.encrypt(data, masterKey)
        return encryptedData.toStorageString()
    }
    
    private fun decryptStoredData(encryptedString: String, masterKey: SecretKey): String {
        val encryptedData = EncryptedData.fromStorageString(encryptedString)
            ?: throw CryptographyError.InvalidEncryptedData
        return cryptoManager.decrypt(encryptedData, masterKey)
            ?: throw CryptographyError.DecryptionFailed
    }
    
    /**
     * Clear cached data and master key (call when app goes to background)
     */
    fun clearCache() {
        cachedPasswords = null
        cachedCreditCards = null
        currentMasterKey?.let { cryptoManager.clearKey(it) }
        currentMasterKey = null
    }
}