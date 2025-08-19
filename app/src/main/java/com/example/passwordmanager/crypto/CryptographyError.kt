package com.example.passwordmanager.crypto

/**
 * Sealed class representing different types of cryptographic errors
 */
sealed class CryptographyError : Exception() {
    object InvalidPassword : CryptographyError() {
        override val message: String = "Invalid master password"
    }
    
    object DataCorrupted : CryptographyError() {
        override val message: String = "Data corruption detected"
    }
    
    object EncryptionFailed : CryptographyError() {
        override val message: String = "Encryption operation failed"
    }
    
    object DecryptionFailed : CryptographyError() {
        override val message: String = "Decryption operation failed"
    }
    
    object KeyDerivationFailed : CryptographyError() {
        override val message: String = "Key derivation failed"
    }
    
    object InvalidSalt : CryptographyError() {
        override val message: String = "Invalid or corrupted salt"
    }
    
    object InvalidEncryptedData : CryptographyError() {
        override val message: String = "Invalid encrypted data format"
    }
    
    data class UnknownError(override val message: String) : CryptographyError()
}