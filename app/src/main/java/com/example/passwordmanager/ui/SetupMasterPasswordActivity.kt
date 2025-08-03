package com.example.passwordmanager.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.passwordmanager.databinding.ActivitySetupMasterPasswordBinding
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.MainActivity

class SetupMasterPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupMasterPasswordBinding
    private lateinit var passwordRepository: PasswordRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupMasterPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        passwordRepository = PasswordRepository(this)
        
        setupUI()
    }

    private fun setupUI() {
        // Set up setup button
        binding.btnSetup.setOnClickListener {
            setupMasterPassword()
        }

        // Set up back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Set up PIN/Password toggle
        binding.switchPinMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // PIN mode
                binding.etPassword.hint = "Enter 4-6 digit PIN"
                binding.etConfirmPassword.hint = "Confirm PIN"
                binding.etPassword.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
                binding.etConfirmPassword.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
            } else {
                // Password mode
                binding.etPassword.hint = "Enter master password"
                binding.etConfirmPassword.hint = "Confirm password"
                binding.etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.etConfirmPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

    private fun setupMasterPassword() {
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val isPinMode = binding.switchPinMode.isChecked

        // Validation
        if (password.isEmpty()) {
            binding.etPassword.error = if (isPinMode) "Please enter a PIN" else "Please enter a password"
            return
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = if (isPinMode) "Please confirm your PIN" else "Please confirm your password"
            return
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return
        }

        // PIN-specific validation
        if (isPinMode) {
            if (!password.matches("^\\d{4,6}$".toRegex())) {
                binding.etPassword.error = "PIN must be 4-6 digits"
                return
            }
        } else {
            // Password-specific validation
            if (password.length < 6) {
                binding.etPassword.error = "Password must be at least 6 characters"
                return
            }
        }

        // Save master password
        if (passwordRepository.setMasterPassword(password, isPinMode)) {
            Toast.makeText(this, "Master password set successfully", Toast.LENGTH_SHORT).show()
            
            // Proceed to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Failed to set master password", Toast.LENGTH_SHORT).show()
        }
    }
} 