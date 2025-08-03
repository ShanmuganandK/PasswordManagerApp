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
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var passwordRepository: PasswordRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        passwordRepository = PasswordRepository(this)
        
        setupUI()
        checkBiometricAvailability()
    }

    private fun setupUI() {
        // Set up login button
        binding.btnLogin.setOnClickListener {
            authenticateUser()
        }

        // Set up biometric button
        binding.btnBiometric.setOnClickListener {
            authenticateWithBiometric()
        }

        // Set up setup button (for first time users)
        binding.btnSetup.setOnClickListener {
            startSetupActivity()
        }

        // Check if master password is already set
        if (passwordRepository.isMasterPasswordSet()) {
            binding.btnSetup.visibility = android.view.View.GONE
            binding.tvWelcome.text = "Enter your master password"
        } else {
            binding.btnLogin.visibility = android.view.View.GONE
            binding.btnBiometric.visibility = android.view.View.GONE
            binding.tvWelcome.text = "Welcome to Password Manager"
        }
    }

    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                binding.btnBiometric.visibility = android.view.View.VISIBLE
            }
            else -> {
                binding.btnBiometric.visibility = android.view.View.GONE
            }
        }
    }

    private fun authenticateUser() {
        val enteredPassword = binding.etPassword.text.toString()
        
        if (enteredPassword.isEmpty()) {
            binding.etPassword.error = "Please enter your master password"
            return
        }

        if (passwordRepository.verifyMasterPassword(enteredPassword)) {
            // Success - proceed to main activity
            startMainActivity()
        } else {
            binding.etPassword.error = "Incorrect master password"
            binding.etPassword.text?.clear()
        }
    }

    private fun authenticateWithBiometric() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                startMainActivity()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@LoginActivity, "Biometric authentication failed", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@LoginActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Use your biometric to access Password Manager")
            .setNegativeButtonText("Use Password")
            .build()

        biometricPrompt.authenticate(promptInfo)
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
    }
} 