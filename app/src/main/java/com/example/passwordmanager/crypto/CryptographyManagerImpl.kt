package com.example.passwordmanager.crypto

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Implementation of CryptographyManager using industry-standard algorithms
 */
class CryptographyManagerImpl : CryptographyManager {
    
    private val secureMemoryManager = SecureMemoryManager()
    
    override fun deriveKeyFromPassword(password: String, salt: ByteArray): SecretKey {
        return try {
            val factory = SecretKeyFactory.getInstance(CryptoConstants.KEY_DERIVATION_ALGORITHM)
            val spec = PBEKeySpec(
                password.toCharArray(),
                salt,
                CryptoConstants.PBKDF2_ITERATIONS,
                CryptoConstants.KEY_LENGTH
            )
            
            val derivedKey = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(derivedKey.encoded, CryptoConstants.AES_KEY_ALGORITHM)
            
            // Clear the PBEKeySpec
            spec.clearPassword()
            
            secretKey
        } catch (e: Exception) {
            throw CryptographyError.KeyDerivationFailed
        }
    }
    
    override fun generateSalt(): ByteArray {
        return secureMemoryManager.secureRandom(CryptoConstants.SALT_LENGTH)
    }
    
    override fun encrypt(data: String, key: SecretKey): EncryptedData {
        return try {
            val cipher = Cipher.getInstance(CryptoConstants.ALGORITHM)
            
            // Generate random IV
            val iv = secureMemoryManager.secureRandom(CryptoConstants.IV_LENGTH)
            val gcmSpec = GCMParameterSpec(CryptoConstants.AUTH_TAG_LENGTH * 8, iv)
            
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec)
            
            val dataBytes = data.toByteArray(Charsets.UTF_8)
            val encryptedWithTag = cipher.doFinal(dataBytes)
            
            // Split encrypted data and auth tag
            val encryptedData = encryptedWithTag.copyOfRange(0, encryptedWithTag.size - CryptoConstants.AUTH_TAG_LENGTH)
            val authTag = encryptedWithTag.copyOfRange(encryptedWithTag.size - CryptoConstants.AUTH_TAG_LENGTH, encryptedWithTag.size)
            
            // Clear sensitive data
            secureMemoryManager.clearByteArray(dataBytes)
            secureMemoryManager.clearByteArray(encryptedWithTag)
            
            EncryptedData(encryptedData, iv, authTag)
        } catch (e: Exception) {
            throw CryptographyError.EncryptionFailed
        }
    }
    
    override fun decrypt(encryptedData: EncryptedData, key: SecretKey): String? {
        return try {
            val cipher = Cipher.getInstance(CryptoConstants.ALGORITHM)
            val gcmSpec = GCMParameterSpec(CryptoConstants.AUTH_TAG_LENGTH * 8, encryptedData.iv)
            
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)
            
            // Combine encrypted data and auth tag
            val encryptedWithTag = encryptedData.encryptedBytes + encryptedData.authTag
            
            val decryptedBytes = cipher.doFinal(encryptedWithTag)
            val result = String(decryptedBytes, Charsets.UTF_8)
            
            // Clear sensitive data
            secureMemoryManager.clearByteArray(decryptedBytes)
            secureMemoryManager.clearByteArray(encryptedWithTag)
            
            result
        } catch (e: Exception) {
            // Decryption failure could mean wrong password or corrupted data
            null
        }
    }
    
    override fun createVerificationToken(key: SecretKey): EncryptedData {
        return try {
            encrypt(CryptoConstants.VERIFICATION_TOKEN_DATA, key)
        } catch (e: Exception) {
            throw CryptographyError.EncryptionFailed
        }
    }
    
    override fun verifyPassword(password: String, salt: ByteArray, verificationToken: EncryptedData): Boolean {
        return try {
            val key = deriveKeyFromPassword(password, salt)
            val decryptedToken = decrypt(verificationToken, key)
            
            val isValid = decryptedToken == CryptoConstants.VERIFICATION_TOKEN_DATA
            
            // Clear the key
            clearKey(key)
            
            isValid
        } catch (e: Exception) {
            false
        }
    }
    
    override fun clearKey(key: SecretKey) {
        secureMemoryManager.clearKey(key)
    }
    
    override fun clearSensitiveData(data: CharArray) {
        secureMemoryManager.clearCharArray(data)
    }
}