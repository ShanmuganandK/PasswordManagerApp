package com.example.passwordmanager.crypto

/**
 * Cryptographic constants following NIST and OWASP security guidelines
 */
object CryptoConstants {
    // PBKDF2 parameters
    const val PBKDF2_ITERATIONS = 100_000
    const val SALT_LENGTH = 32 // bytes
    const val KEY_LENGTH = 256 // bits
    
    // AES-GCM parameters
    const val IV_LENGTH = 12 // bytes for GCM
    const val AUTH_TAG_LENGTH = 16 // bytes
    
    // Algorithm identifiers
    const val ALGORITHM = "AES/GCM/NoPadding"
    const val KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256"
    const val AES_KEY_ALGORITHM = "AES"
    
    // Verification token
    const val VERIFICATION_TOKEN_DATA = "MaureanManager_Verification_Token_v1"
}