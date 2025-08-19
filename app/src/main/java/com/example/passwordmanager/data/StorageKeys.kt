package com.example.passwordmanager.data

/**
 * SharedPreferences keys for encrypted storage
 */
object StorageKeys {
    // Encryption setup
    const val ENCRYPTION_SALT = "encryption_salt_v2"
    const val VERIFICATION_TOKEN = "verification_token_v2"
    const val ENCRYPTION_VERSION = "encryption_version"
    
    // Encrypted data
    const val ENCRYPTED_PASSWORDS = "encrypted_passwords_v2"
    const val ENCRYPTED_CREDIT_CARDS = "encrypted_credit_cards_v2"
    

    
    // Current encryption version
    const val CURRENT_ENCRYPTION_VERSION = 1
}