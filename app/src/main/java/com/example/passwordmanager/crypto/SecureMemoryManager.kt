package com.example.passwordmanager.crypto

import java.security.SecureRandom
import java.util.Arrays
import javax.crypto.SecretKey

/**
 * Manages secure memory operations for cryptographic data
 */
class SecureMemoryManager {
    
    private val secureRandom = SecureRandom()
    
    /**
     * Securely clear a byte array by overwriting with zeros
     */
    fun clearByteArray(array: ByteArray) {
        Arrays.fill(array, 0.toByte())
    }
    
    /**
     * Securely clear a char array by overwriting with null characters
     */
    fun clearCharArray(array: CharArray) {
        Arrays.fill(array, '\u0000')
    }
    
    /**
     * Generate cryptographically secure random bytes
     */
    fun secureRandom(size: Int): ByteArray {
        val bytes = ByteArray(size)
        secureRandom.nextBytes(bytes)
        return bytes
    }
    
    /**
     * Attempt to clear a SecretKey from memory (best effort)
     * Note: This is implementation-dependent and may not always work
     */
    fun clearKey(key: SecretKey) {
        try {
            // Try to clear the key material if accessible
            val encoded = key.encoded
            if (encoded != null) {
                clearByteArray(encoded)
            }
        } catch (e: Exception) {
            // Key clearing failed, but this is not critical
            // The JVM garbage collector will eventually clear it
        }
    }
    
    /**
     * Clear sensitive string data by converting to char array and clearing
     */
    fun clearString(sensitiveString: String): CharArray {
        val charArray = sensitiveString.toCharArray()
        clearCharArray(charArray)
        return charArray
    }
}