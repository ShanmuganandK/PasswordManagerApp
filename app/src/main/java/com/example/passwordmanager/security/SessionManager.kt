package com.example.passwordmanager.security

import android.content.Context
import com.example.passwordmanager.crypto.CryptographyManager
import com.example.passwordmanager.crypto.CryptographyManagerImpl
import com.example.passwordmanager.data.SecurePasswordRepositoryImpl
import javax.crypto.SecretKey

/**
 * Manages the user session and master key lifecycle
 */
class SessionManager private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: SessionManager? = null
        
        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val cryptoManager: CryptographyManager = CryptographyManagerImpl()
    private var currentMasterKey: SecretKey? = null
    private var isAuthenticated = false
    private var lastActivityTime = System.currentTimeMillis()
    
    // Auto-logout after 2 minutes of inactivity
    private val AUTO_LOGOUT_TIMEOUT = 2 * 60 * 1000L // 2 minutes
    
    // Simplified session manager without ProcessLifecycleOwner dependency
    
    /**
     * Set the master key for the current session
     */
    fun setMasterKey(masterKey: SecretKey) {
        currentMasterKey = masterKey
        isAuthenticated = true
        updateLastActivityTime()
    }
    
    /**
     * Get the current master key if available and session is valid
     */
    fun getMasterKey(): SecretKey? {
        if (!isAuthenticated || isSessionExpired()) {
            clearSession()
            return null
        }
        updateLastActivityTime()
        return currentMasterKey
    }
    
    /**
     * Check if user is currently authenticated
     */
    fun isAuthenticated(): Boolean {
        if (isSessionExpired()) {
            clearSession()
            return false
        }
        return isAuthenticated
    }
    
    /**
     * Update the last activity time
     */
    fun updateLastActivityTime() {
        lastActivityTime = System.currentTimeMillis()
    }
    
    /**
     * Clear the current session and master key
     */
    fun clearSession() {
        currentMasterKey?.let { cryptoManager.clearKey(it) }
        currentMasterKey = null
        isAuthenticated = false
        
        // Also clear repository cache
        val repository = SecurePasswordRepositoryImpl(context)
        repository.clearCache()
    }
    
    /**
     * Check if the session has expired due to inactivity
     */
    private fun isSessionExpired(): Boolean {
        return System.currentTimeMillis() - lastActivityTime > AUTO_LOGOUT_TIMEOUT
    }
    
    /**
     * Call this method when app goes to background
     */
    fun onAppBackground() {
        clearSession()
    }
    
    /**
     * Call this method when app comes to foreground
     */
    fun onAppForeground() {
        updateLastActivityTime()
    }
}