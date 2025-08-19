package com.example.passwordmanager.crypto

import javax.crypto.SecretKey

/**
 * Interface for all cryptographic operations in the password manager
 */
interface CryptographyManager {
    
    /**
     * Derive an encryption key from a password using PBKDF2
     * @param password The master password
     * @param salt Cryptographically secure salt
     * @return Derived SecretKey for AES encryption
     * @throws CryptographyError.KeyDerivationFailed if key derivation fails
     */
    @Throws(CryptographyError::class)
    fun deriveKeyFromPassword(password: String, salt: ByteArray): SecretKey
    
    /**
     * Generate a cryptographically secure random salt
     * @return Random salt bytes
     */
    fun generateSalt(): ByteArray
    
    /**
     * Encrypt data using AES-256-GCM
     * @param data Plain text data to encrypt
     * @param key AES encryption key
     * @return EncryptedData containing encrypted bytes, IV, and auth tag
     * @throws CryptographyError.EncryptionFailed if encryption fails
     */
    @Throws(CryptographyError::class)
    fun encrypt(data: String, key: SecretKey): EncryptedData
    
    /**
     * Decrypt data using AES-256-GCM
     * @param encryptedData EncryptedData to decrypt
     * @param key AES decryption key
     * @return Decrypted plain text data, or null if decryption fails
     * @throws CryptographyError.DecryptionFailed if decryption fails
     */
    @Throws(CryptographyError::class)
    fun decrypt(encryptedData: EncryptedData, key: SecretKey): String?
    
    /**
     * Create a verification token encrypted with the given key
     * This token is used to verify the master password without storing a hash
     * @param key Encryption key derived from master password
     * @return Encrypted verification token
     * @throws CryptographyError.EncryptionFailed if token creation fails
     */
    @Throws(CryptographyError::class)
    fun createVerificationToken(key: SecretKey): EncryptedData
    
    /**
     * Verify a password by attempting to decrypt the verification token
     * @param password Master password to verify
     * @param salt Salt used for key derivation
     * @param verificationToken Encrypted verification token
     * @return true if password is correct, false otherwise
     */
    fun verifyPassword(password: String, salt: ByteArray, verificationToken: EncryptedData): Boolean
    
    /**
     * Securely clear a key from memory (best effort)
     * @param key SecretKey to clear
     */
    fun clearKey(key: SecretKey)
    
    /**
     * Securely clear sensitive data from memory
     * @param data Sensitive character data to clear
     */
    fun clearSensitiveData(data: CharArray)
}