package com.example.passwordmanager.crypto

/**
 * Sealed class representing authentication results
 */
sealed class AuthResult {
    object Success : AuthResult()
    object InvalidPassword : AuthResult()
    object DataCorrupted : AuthResult()
    object SetupRequired : AuthResult()
    data class Error(val message: String) : AuthResult()
    
    /**
     * Check if authentication was successful
     */
    fun isSuccess(): Boolean = this is Success
    
    /**
     * Get user-friendly error message
     */
    fun getErrorMessage(): String = when (this) {
        is Success -> "Authentication successful"
        is InvalidPassword -> "Invalid master password"
        is DataCorrupted -> "Data corruption detected. Please reset the app."
        is SetupRequired -> "Master password setup required"
        is Error -> message
    }
}