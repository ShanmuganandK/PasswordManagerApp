package com.example.passwordmanager.crypto

import android.util.Base64
import java.util.Arrays

/**
 * Container for encrypted data with all necessary cryptographic metadata
 */
data class EncryptedData(
    val encryptedBytes: ByteArray,
    val iv: ByteArray,
    val authTag: ByteArray,
    val algorithm: String = CryptoConstants.ALGORITHM
) {
    
    /**
     * Serialize encrypted data to Base64 string for storage
     * Format: algorithm:iv:authTag:encryptedData (all Base64 encoded)
     */
    fun toStorageString(): String {
        val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
        val authTagBase64 = Base64.encodeToString(authTag, Base64.NO_WRAP)
        val encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        
        return "$algorithm:$ivBase64:$authTagBase64:$encryptedBase64"
    }
    
    companion object {
        /**
         * Deserialize encrypted data from Base64 storage string
         */
        fun fromStorageString(data: String): EncryptedData? {
            return try {
                val parts = data.split(":")
                if (parts.size != 4) return null
                
                val algorithm = parts[0]
                val iv = Base64.decode(parts[1], Base64.NO_WRAP)
                val authTag = Base64.decode(parts[2], Base64.NO_WRAP)
                val encryptedBytes = Base64.decode(parts[3], Base64.NO_WRAP)
                
                EncryptedData(encryptedBytes, iv, authTag, algorithm)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as EncryptedData
        
        if (!encryptedBytes.contentEquals(other.encryptedBytes)) return false
        if (!iv.contentEquals(other.iv)) return false
        if (!authTag.contentEquals(other.authTag)) return false
        if (algorithm != other.algorithm) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = encryptedBytes.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + authTag.contentHashCode()
        result = 31 * result + algorithm.hashCode()
        return result
    }
}