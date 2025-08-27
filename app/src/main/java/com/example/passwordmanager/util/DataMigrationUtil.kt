package com.example.passwordmanager.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object DataMigrationUtil {
    private const val TAG = "DataMigrationUtil"
    private const val MIGRATION_PREFS = "migration_prefs"
    private const val KEY_DATA_VERSION = "data_version"
    private const val CURRENT_DATA_VERSION = 2 // Increment when data structure changes
    
    fun checkAndMigrateData(context: Context): Boolean {
        val migrationPrefs = context.getSharedPreferences(MIGRATION_PREFS, Context.MODE_PRIVATE)
        val currentVersion = migrationPrefs.getInt(KEY_DATA_VERSION, 1)
        
        if (currentVersion < CURRENT_DATA_VERSION) {
            Log.i(TAG, "Data migration needed from version $currentVersion to $CURRENT_DATA_VERSION")
            return performMigration(context, currentVersion, migrationPrefs)
        }
        
        return true // No migration needed
    }
    
    private fun performMigration(
        context: Context, 
        fromVersion: Int, 
        migrationPrefs: SharedPreferences
    ): Boolean {
        return try {
            when (fromVersion) {
                1 -> migrateFromV1ToV2(context)
                else -> {
                    Log.w(TAG, "Unknown version $fromVersion, clearing all data")
                    clearAllAppData(context)
                }
            }
            
            // Update version after successful migration
            migrationPrefs.edit()
                .putInt(KEY_DATA_VERSION, CURRENT_DATA_VERSION)
                .apply()
            
            Log.i(TAG, "Migration completed successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Migration failed", e)
            // If migration fails, clear all data to prevent crashes
            clearAllAppData(context)
            migrationPrefs.edit()
                .putInt(KEY_DATA_VERSION, CURRENT_DATA_VERSION)
                .apply()
            true
        }
    }
    
    private fun migrateFromV1ToV2(context: Context): Boolean {
        Log.i(TAG, "Migrating from V1 to V2 - clearing incompatible data")
        
        // Clear old encrypted data that might be incompatible
        val sharedPrefs = context.getSharedPreferences("password_manager_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        
        // Remove old data keys that might cause issues
        editor.remove("encrypted_passwords")
        editor.remove("encrypted_credit_cards")
        editor.remove("master_password_hash") // Old hash-based system
        editor.apply()
        
        // Clear database if it exists
        try {
            context.deleteDatabase("password_manager.db")
            Log.i(TAG, "Cleared old database")
        } catch (e: Exception) {
            Log.w(TAG, "Could not clear database", e)
        }
        
        return true
    }
    
    fun clearAllAppData(context: Context) {
        Log.i(TAG, "Clearing all app data")
        
        try {
            // Clear SharedPreferences
            val sharedPrefs = context.getSharedPreferences("password_manager_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().clear().apply()
            
            // Clear session data
            val sessionPrefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)
            sessionPrefs.edit().clear().apply()
            
            // Clear database
            context.deleteDatabase("password_manager.db")
            
            Log.i(TAG, "App data cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing app data", e)
        }
    }
    
    fun isFirstRun(context: Context): Boolean {
        val migrationPrefs = context.getSharedPreferences(MIGRATION_PREFS, Context.MODE_PRIVATE)
        return !migrationPrefs.contains(KEY_DATA_VERSION)
    }
}