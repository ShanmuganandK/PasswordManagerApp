package com.example.passwordmanager.integration

import com.example.passwordmanager.crypto.*
import com.example.passwordmanager.model.PasswordEntry
import com.example.passwordmanager.model.CreditCardEntry
import org.junit.Test
import org.junit.Assert.*

/**
 * Integration tests for the complete encryption system
 */
class EncryptionIntegrationTest {
    
    private val cryptoManager = CryptographyManagerImpl()
    
    @Test
    fun testCompleteEncryptionWorkflow() {
        val password = "testMasterPassword123"
        val salt = cryptoManager.generateSalt()
        
        // Derive key
        val masterKey = cryptoManager.deriveKeyFromPassword(password, salt)
        
        // Create verification token
        val verificationToken = cryptoManager.createVerificationToken(masterKey)
        
        // Verify password works
        assertTrue(cryptoManager.verifyPassword(password, salt, verificationToken))
        
        // Verify wrong password fails
        assertFalse(cryptoManager.verifyPassword("wrongPassword", salt, verificationToken))
        
        // Test data encryption
        val testData = "Sensitive password data"
        val encryptedData = cryptoManager.encrypt(testData, masterKey)
        val decryptedData = cryptoManager.decrypt(encryptedData, masterKey)
        
        assertEquals(testData, decryptedData)
        
        // Test serialization
        val serialized = encryptedData.toStorageString()
        val deserialized = EncryptedData.fromStorageString(serialized)
        assertNotNull(deserialized)
        
        val decryptedFromSerialized = cryptoManager.decrypt(deserialized!!, masterKey)
        assertEquals(testData, decryptedFromSerialized)
    }
    
    @Test
    fun testPasswordEntryEncryption() {
        val password = "masterPassword123"
        val salt = cryptoManager.generateSalt()
        val masterKey = cryptoManager.deriveKeyFromPassword(password, salt)
        
        val passwordEntry = PasswordEntry(
            context = "Gmail Account",
            username = "user@example.com",
            password = "secretPassword123",
            expiryDate = "2024-12-31",
            notes = "Personal email account",
            id = "test-id-123"
        )
        
        // Encrypt sensitive fields
        val encryptedContext = cryptoManager.encrypt(passwordEntry.context, masterKey)
        val encryptedUsername = cryptoManager.encrypt(passwordEntry.username, masterKey)
        val encryptedPassword = cryptoManager.encrypt(passwordEntry.password, masterKey)
        val encryptedNotes = cryptoManager.encrypt(passwordEntry.notes, masterKey)
        
        // Decrypt and verify
        assertEquals(passwordEntry.context, cryptoManager.decrypt(encryptedContext, masterKey))
        assertEquals(passwordEntry.username, cryptoManager.decrypt(encryptedUsername, masterKey))
        assertEquals(passwordEntry.password, cryptoManager.decrypt(encryptedPassword, masterKey))
        assertEquals(passwordEntry.notes, cryptoManager.decrypt(encryptedNotes, masterKey))
    }
    
    @Test
    fun testCreditCardEntryEncryption() {
        val password = "masterPassword123"
        val salt = cryptoManager.generateSalt()
        val masterKey = cryptoManager.deriveKeyFromPassword(password, salt)
        
        val creditCard = CreditCardEntry(
            cardNumber = "4111111111111111",
            cardHolder = "John Doe",
            bankName = "Test Bank",
            cardType = "Visa",
            expiryDate = "12/25",
            cvv = "123",
            notes = "Primary credit card",
            id = "card-id-123"
        )
        
        // Encrypt sensitive fields
        val encryptedCardNumber = cryptoManager.encrypt(creditCard.cardNumber, masterKey)
        val encryptedCardHolder = cryptoManager.encrypt(creditCard.cardHolder, masterKey)
        val encryptedBankName = cryptoManager.encrypt(creditCard.bankName, masterKey)
        val encryptedCvv = cryptoManager.encrypt(creditCard.cvv, masterKey)
        val encryptedNotes = cryptoManager.encrypt(creditCard.notes, masterKey)
        
        // Decrypt and verify
        assertEquals(creditCard.cardNumber, cryptoManager.decrypt(encryptedCardNumber, masterKey))
        assertEquals(creditCard.cardHolder, cryptoManager.decrypt(encryptedCardHolder, masterKey))
        assertEquals(creditCard.bankName, cryptoManager.decrypt(encryptedBankName, masterKey))
        assertEquals(creditCard.cvv, cryptoManager.decrypt(encryptedCvv, masterKey))
        assertEquals(creditCard.notes, cryptoManager.decrypt(encryptedNotes, masterKey))
    }
    
    @Test
    fun testMultipleEncryptionOperations() {
        val password = "masterPassword123"
        val salt = cryptoManager.generateSalt()
        val masterKey = cryptoManager.deriveKeyFromPassword(password, salt)
        
        val testData = listOf(
            "Password 1",
            "Password 2",
            "Password 3",
            "Very long password with special characters !@#$%^&*()",
            "Unicode test: 你好世界 🔐"
        )
        
        // Encrypt all data
        val encryptedData = testData.map { cryptoManager.encrypt(it, masterKey) }
        
        // Decrypt all data
        val decryptedData = encryptedData.map { cryptoManager.decrypt(it, masterKey) }
        
        // Verify all data matches
        assertEquals(testData, decryptedData)
        
        // Verify each encrypted data has unique IV
        val ivs = encryptedData.map { it.iv }
        val uniqueIvs = ivs.toSet()
        assertEquals(ivs.size, uniqueIvs.size) // All IVs should be unique
    }
    
    @Test
    fun testKeyDerivationConsistency() {
        val password = "consistencyTest123"
        val salt = cryptoManager.generateSalt()
        
        // Derive key multiple times
        val key1 = cryptoManager.deriveKeyFromPassword(password, salt)
        val key2 = cryptoManager.deriveKeyFromPassword(password, salt)
        val key3 = cryptoManager.deriveKeyFromPassword(password, salt)
        
        // All keys should be identical
        assertArrayEquals(key1.encoded, key2.encoded)
        assertArrayEquals(key2.encoded, key3.encoded)
        
        // Test with different salt
        val differentSalt = cryptoManager.generateSalt()
        val key4 = cryptoManager.deriveKeyFromPassword(password, differentSalt)
        
        // Key with different salt should be different
        assertFalse(key1.encoded.contentEquals(key4.encoded))
    }
}