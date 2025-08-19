package com.example.passwordmanager.crypto

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for EncryptedData serialization and validation
 */
class EncryptedDataTest {
    
    @Test
    fun testSerializationDeserialization() {
        val encryptedBytes = byteArrayOf(1, 2, 3, 4, 5)
        val iv = byteArrayOf(6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
        val authTag = byteArrayOf(18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33)
        
        val originalData = EncryptedData(encryptedBytes, iv, authTag)
        
        // Serialize
        val serialized = originalData.toStorageString()
        assertNotNull(serialized)
        assertTrue(serialized.contains(":"))
        
        // Deserialize
        val deserialized = EncryptedData.fromStorageString(serialized)
        assertNotNull(deserialized)
        
        // Verify data integrity
        assertEquals(originalData, deserialized)
        assertArrayEquals(originalData.encryptedBytes, deserialized!!.encryptedBytes)
        assertArrayEquals(originalData.iv, deserialized.iv)
        assertArrayEquals(originalData.authTag, deserialized.authTag)
        assertEquals(originalData.algorithm, deserialized.algorithm)
    }
    
    @Test
    fun testInvalidSerializedData() {
        // Test various invalid formats
        assertNull(EncryptedData.fromStorageString(""))
        assertNull(EncryptedData.fromStorageString("invalid"))
        assertNull(EncryptedData.fromStorageString("only:two:parts"))
        assertNull(EncryptedData.fromStorageString("too:many:parts:here:extra"))
        assertNull(EncryptedData.fromStorageString("invalid:base64:data:here"))
    }
    
    @Test
    fun testEqualsAndHashCode() {
        val encryptedBytes = byteArrayOf(1, 2, 3)
        val iv = byteArrayOf(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        val authTag = byteArrayOf(16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31)
        
        val data1 = EncryptedData(encryptedBytes, iv, authTag)
        val data2 = EncryptedData(encryptedBytes.copyOf(), iv.copyOf(), authTag.copyOf())
        val data3 = EncryptedData(byteArrayOf(9, 8, 7), iv, authTag)
        
        // Same data should be equal
        assertEquals(data1, data2)
        assertEquals(data1.hashCode(), data2.hashCode())
        
        // Different data should not be equal
        assertNotEquals(data1, data3)
    }
    
    @Test
    fun testDefaultAlgorithm() {
        val encryptedData = EncryptedData(
            byteArrayOf(1, 2, 3),
            byteArrayOf(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
            byteArrayOf(16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31)
        )
        
        assertEquals(CryptoConstants.ALGORITHM, encryptedData.algorithm)
    }
}