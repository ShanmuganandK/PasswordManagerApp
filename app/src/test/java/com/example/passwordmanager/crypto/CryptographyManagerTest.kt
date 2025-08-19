package com.example.passwordmanager.crypto

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for CryptographyManager implementation
 */
class CryptographyManagerTest {
    
    private val cryptoManager = CryptographyManagerImpl()
    
    @Test
    fun testKeyDerivation() {
        val password = "testPassword123"
        val salt = cryptoManager.generateSalt()
        
        val key1 = cryptoManager.deriveKeyFromPassword(password, salt)
        val key2 = cryptoManager.deriveKeyFromPassword(password, salt)
        
        // Same password and salt should produce same key
        assertArrayEquals(key1.encoded, key2.encoded)
    }
    
    @Test
    fun testSaltGeneration() {
        val salt1 = cryptoManager.generateSalt()
        val salt2 = cryptoManager.generateSalt()
        
        // Salts should be different
        assertFalse(salt1.contentEquals(salt2))
        
        // Salt should be correct length
        assertEquals(CryptoConstants.SALT_LENGTH, salt1.size)
        assertEquals(CryptoConstants.SALT_LENGTH, salt2.size)
    }
    
    @Test
    fun testEncryptionDecryption() {
        val password = "testPassword123"
        val salt = cryptoManager.generateSalt()
        val key = cryptoManager.deriveKeyFromPassword(password, salt)
        
        val originalData = "This is sensitive test data"
        
        // Encrypt data
        val encryptedData = cryptoManager.encrypt(originalData, key)
        
        // Decrypt data
        val decryptedData = cryptoManager.decrypt(encryptedData, key)
        
        assertEquals(originalData, decryptedData)
    }
    
    @Test
    fun testEncryptionWithDifferentKeys() {
        val salt = cryptoManager.generateSalt()
        val key1 = cryptoManager.deriveKeyFromPassword("password1", salt)
        val key2 = cryptoManager.deriveKeyFromPassword("password2", salt)
        
        val originalData = "Test data"
        val encryptedData = cryptoManager.encrypt(originalData, key1)
        
        // Decryption with wrong key should return null
        val decryptedData = cryptoManager.decrypt(encryptedData, key2)
        assertNull(decryptedData)
    }
    
    @Test
    fun testVerificationToken() {
        val password = "testPassword123"
        val salt = cryptoManager.generateSalt()
        val key = cryptoManager.deriveKeyFromPassword(password, salt)
        
        // Create verification token
        val verificationToken = cryptoManager.createVerificationToken(key)
        
        // Verify with correct password
        assertTrue(cryptoManager.verifyPassword(password, salt, verificationToken))
        
        // Verify with wrong password
        assertFalse(cryptoManager.verifyPassword("wrongPassword", salt, verificationToken))
    }
    
    @Test
    fun testEncryptedDataSerialization() {
        val password = "testPassword123"
        val salt = cryptoManager.generateSalt()
        val key = cryptoManager.deriveKeyFromPassword(password, salt)
        
        val originalData = "Test serialization data"
        val encryptedData = cryptoManager.encrypt(originalData, key)
        
        // Serialize to string
        val serialized = encryptedData.toStorageString()
        
        // Deserialize from string
        val deserialized = EncryptedData.fromStorageString(serialized)
        assertNotNull(deserialized)
        
        // Decrypt deserialized data
        val decryptedData = cryptoManager.decrypt(deserialized!!, key)
        assertEquals(originalData, decryptedData)
    }
    
    @Test
    fun testInvalidEncryptedDataDeserialization() {
        val invalidData = "invalid:data:format"
        val result = EncryptedData.fromStorageString(invalidData)
        assertNull(result)
    }
}