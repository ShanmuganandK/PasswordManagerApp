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

import com.example.passwordmanager.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
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
        // Biometric authentication is not supported with encrypted system
        // The master password is required for decryption
        binding.ivFingerprint.visibility = android.view.View.GONE
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



    override fun onResume() {
        super.onResume()
        // Clear password field when returning to login
        binding.etPassword.text?.clear()
        // Refresh UI in case migration status changed
        setupUI()
    }
} 