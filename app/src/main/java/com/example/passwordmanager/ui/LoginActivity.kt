package com.example.passwordmanager.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.ActivityLoginBinding
import com.example.passwordmanager.util.DataMigrationUtil
import com.example.passwordmanager.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Perform data migration if needed
        if (!DataMigrationUtil.checkAndMigrateData(this)) {
            Toast.makeText(this, "Data migration failed. App data has been reset.", Toast.LENGTH_LONG).show()
        }
        
        setupUI()
        checkBiometricAvailability()
    }

    private fun setupUI() {
        // Set up login button
        binding.btnLogin.setOnClickListener {
            authenticateUser()
        }

        // Set up setup button (for first time users only)
        binding.btnSetup.setOnClickListener {
            startSetupActivity()
        }
        
        // Long press to clear app data (emergency reset)
        binding.btnSetup.setOnLongClickListener {
            showResetConfirmation()
            true
        }
        
        // Enable biometric setup option
        binding.btnLogin.setOnLongClickListener {
            if (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
                // Show option to enable biometric
                Toast.makeText(this, "Long press detected - Biometric setup would go here", Toast.LENGTH_SHORT).show()
            }
            true
        }
        


        // Check if encryption is set up
        val secureRepository = com.example.passwordmanager.data.SecurePasswordRepositoryImpl(this)
        
        if (secureRepository.isEncryptionSetup()) {
            // Encryption is set up - show login UI
            binding.btnSetup.visibility = android.view.View.GONE
            binding.btnLogin.visibility = android.view.View.VISIBLE
        } else {
            // No encryption setup - show setup UI
            binding.btnLogin.visibility = android.view.View.GONE
            binding.btnSetup.visibility = android.view.View.VISIBLE
            binding.btnSetup.text = "Set Up Master Password"
        }
    }

    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                binding.ivFingerprint.visibility = android.view.View.VISIBLE
                binding.ivFingerprint.setOnClickListener { authenticateWithBiometric() }
            }
            else -> binding.ivFingerprint.visibility = android.view.View.GONE
        }
    }
    
    private fun authenticateWithBiometric() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this as androidx.fragment.app.FragmentActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Get stored master password (encrypted with biometric key)
                    val storedPassword = getStoredMasterPassword()
                    if (storedPassword != null) {
                        authenticateWithPassword(storedPassword)
                    }
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@LoginActivity, "Biometric authentication failed: $errString", Toast.LENGTH_SHORT).show()
                }
            })
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Use your fingerprint to unlock")
            .setNegativeButtonText("Cancel")
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
    
    private fun getStoredMasterPassword(): String? {
        // This would need to decrypt stored master password using Android Keystore
        // For now, return null to maintain security
        return null
    }
    
    private fun authenticateWithPassword(password: String) {
        binding.etPassword.setText(password)
        authenticateUser()
    }

    private fun authenticateUser() {
        val enteredPassword = binding.etPassword.text.toString()
        
        if (enteredPassword.isEmpty()) {
            binding.etPassword.error = "Please enter your master password"
            return
        }

        // Use secure authentication
        val secureRepository = com.example.passwordmanager.data.SecurePasswordRepositoryImpl(this)
        val authResult = secureRepository.authenticateUser(enteredPassword)
        
        when (authResult) {
            is com.example.passwordmanager.crypto.AuthResult.Success -> {
                // Set master key in session manager
                val masterKey = secureRepository.deriveKeyFromPassword(enteredPassword)
                if (masterKey != null) {
                    val sessionManager = com.example.passwordmanager.security.SessionManager.getInstance(this)
                    sessionManager.setMasterKey(masterKey)
                }
                startMainActivity()
            }
            is com.example.passwordmanager.crypto.AuthResult.InvalidPassword -> {
                binding.etPassword.error = "Invalid master password"
                binding.etPassword.text?.clear()
            }
            is com.example.passwordmanager.crypto.AuthResult.DataCorrupted -> {
                binding.etPassword.error = "Data corruption detected. Please reset the app."
                binding.etPassword.text?.clear()
            }
            is com.example.passwordmanager.crypto.AuthResult.SetupRequired -> {
                // This shouldn't happen in login, but handle it
                startSetupActivity()
            }
            is com.example.passwordmanager.crypto.AuthResult.Error -> {
                binding.etPassword.error = authResult.message
                binding.etPassword.text?.clear()
            }
        }
    }



    private fun startSetupActivity() {
        val intent = Intent(this, SetupMasterPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }



    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }



    private fun showResetConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Reset App Data")
            .setMessage("This will permanently delete all your saved passwords and credit cards. Are you sure?")
            .setPositiveButton("Reset") { _, _ ->
                DataMigrationUtil.clearAllAppData(this)
                Toast.makeText(this, "App data cleared. You can now set up a new master password.", Toast.LENGTH_LONG).show()
                setupUI() // Refresh UI
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Clear password field when returning to login
        binding.etPassword.text?.clear()
        // Refresh UI in case migration status changed
        setupUI()
    }
} 