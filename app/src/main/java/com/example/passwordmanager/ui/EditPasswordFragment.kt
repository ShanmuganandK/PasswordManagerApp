package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.FragmentEditPasswordBinding
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.model.PasswordEntry
import com.example.passwordmanager.util.CardColorUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditPasswordFragment : Fragment() {

    private var _binding: FragmentEditPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var passwordRepository: PasswordRepository
    private lateinit var passwordEntry: PasswordEntry
    private val currentYear = LocalDate.now().year
    
    // Password preview views
    private lateinit var previewContext: TextView
    private lateinit var previewAppName: TextView
    private lateinit var previewUsername: TextView
    private lateinit var previewPassword: TextView
    private lateinit var previewExpiry: TextView
    private val currentMonth = LocalDate.now().monthValue // 1-based index, but we need to account for "No Expiry"
    private val allMonths = arrayOf("No Expiry", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPasswordBinding.inflate(inflater, container, false)
        passwordRepository = PasswordRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set blurred background to match main page
        binding.root.setBackgroundResource(R.drawable.blurred_background)

        // Get password entry from arguments
        arguments?.let { args ->
            passwordEntry = args.getParcelable("passwordEntry") ?: return@let
        }

        setupPasswordPreview()
        setupExpiryDateSpinners()
        setupTextWatchers()
        // Load existing password data
        loadPasswordData()
        
        // Update preview after loading data
        updatePasswordPreview()
        
        // Initial preview update
        binding.passwordPreview.root.post {
            updatePasswordPreview()
        }

        binding.btnUpdatePassword.setOnClickListener {
            updatePassword()
        }

        binding.btnDeletePassword.setOnClickListener {
            deletePassword()
        }
        
        // Setup back button
        binding.btnBack.setOnClickListener {
            requireParentFragment().findNavController().navigateUp()
        }
        
        // Setup password visibility toggle
        binding.passwordVisibilityToggle.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun setupExpiryDateSpinners() {
        // Setup year spinner (current year to 20 years from now)
        val years = mutableListOf<String>()
        years.add("No Expiry")
        for (i in 0..20) {
            years.add((currentYear + i).toString())
        }
        val yearAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item_white, years)
        yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_white)
        binding.spinnerYear.setPopupBackgroundResource(R.drawable.spinner_popup_background)
        binding.spinnerYear.adapter = yearAdapter

        // Create dynamic month adapter
        val monthAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item_white, allMonths)
        monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_white)
        binding.spinnerMonth.setPopupBackgroundResource(R.drawable.spinner_popup_background)
        binding.spinnerMonth.adapter = monthAdapter

        // Add listener to year spinner to dynamically filter months
        setupYearSpinnerListener()
    }

    private fun setupYearSpinnerListener() {
        val years = mutableListOf<String>()
        years.add("No Expiry")
        for (i in 0..20) {
            years.add((currentYear + i).toString())
        }
        
        binding.spinnerYear.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // Get the current month name before changing the adapter
                val currentMonthName = binding.spinnerMonth.selectedItem?.toString()
                
                if (position == 0) {
                    // "No Expiry" selected, set month to "No Expiry"
                    binding.spinnerMonth.setSelection(0)
                } else {
                    val selectedYear = years[position].toInt()
                    if (selectedYear == currentYear) {
                        // For current year, show only months from current month onwards (including "No Expiry")
                        val validMonths = arrayOf("No Expiry") + allMonths.slice(currentMonth until allMonths.size).drop(1).toTypedArray()
                        val newMonthAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item_white, validMonths)
                        newMonthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_white)
                        binding.spinnerMonth.setPopupBackgroundResource(R.drawable.spinner_popup_background)
                        binding.spinnerMonth.adapter = newMonthAdapter
                        
                        // Try to preserve the current selection if it's valid for current year
                        if (currentMonthName != null && validMonths.contains(currentMonthName)) {
                            val preservedIndex = validMonths.indexOf(currentMonthName)
                            binding.spinnerMonth.setSelection(preservedIndex)
                        } else {
                            // Set to current month (which is now at index 1, after "No Expiry")
                            binding.spinnerMonth.setSelection(1)
                        }
                    } else {
                        // For future years, show all months
                        val newMonthAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item_white, allMonths)
                        newMonthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_white)
                        binding.spinnerMonth.setPopupBackgroundResource(R.drawable.spinner_popup_background)
                        binding.spinnerMonth.adapter = newMonthAdapter
                        
                        // Try to preserve the current selection
                        if (currentMonthName != null && allMonths.contains(currentMonthName)) {
                            val preservedIndex = allMonths.indexOf(currentMonthName)
                            binding.spinnerMonth.setSelection(preservedIndex)
                        } else {
                            // Set to "No Expiry" for future years
                            binding.spinnerMonth.setSelection(0)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
        
        // Add listeners for expiry date preview updates
        binding.spinnerMonth.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                updateExpiryPreview()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun loadPasswordData() {
        binding.etContext.setText(passwordEntry.context)
        binding.etUsername.setText(passwordEntry.username)
        binding.etPassword.setText(passwordEntry.password)
        
        // Temporarily disable the year spinner listener
        binding.spinnerYear.onItemSelectedListener = null
        
        setExpiryDateInSpinners(passwordEntry.expiryDate)
        
        // Re-enable the year spinner listener
        setupYearSpinnerListener()
        
        binding.etNotes.setText(passwordEntry.notes)
    }

    private fun setExpiryDateInSpinners(expiryDate: String) {
        if (expiryDate.isEmpty()) {
            binding.spinnerMonth.setSelection(0) // "No Expiry"
            binding.spinnerYear.setSelection(0) // "No Expiry"
            return
        }

        try {
            val date = LocalDate.parse(expiryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val monthName = date.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
            val year = date.year.toString()

            // Set year first
            val yearIndex = year.toInt() - currentYear + 1 // +1 because "No Expiry" is at index 0
            if (yearIndex >= 0 && yearIndex <= 21) { // 0-20 years + "No Expiry"
                binding.spinnerYear.setSelection(yearIndex)
            }

            // Set month - the listener will handle filtering if needed
            val monthIndex = allMonths.indexOf(monthName)
            if (monthIndex != -1) {
                binding.spinnerMonth.setSelection(monthIndex)
            } else {
                binding.spinnerMonth.setSelection(0)
            }
        } catch (e: Exception) {
            // If parsing fails, set to "No Expiry"
            binding.spinnerMonth.setSelection(0)
            binding.spinnerYear.setSelection(0)
        }
    }

    private fun updatePassword() {
        val context = binding.etContext.text.toString()
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        val expiryDate = getExpiryDateFromSpinners()
        val notes = binding.etNotes.text.toString()

        if (context.isNotEmpty() && password.isNotEmpty()) {
            val updatedPassword = passwordEntry.copy(
                context = context,
                username = username,
                password = password,
                expiryDate = expiryDate,
                notes = notes
            )
            passwordRepository.updatePassword(updatedPassword)
            Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getExpiryDateFromSpinners(): String {
        val selectedMonth = binding.spinnerMonth.selectedItem.toString()
        val selectedYear = binding.spinnerYear.selectedItem.toString()
        
        if (selectedMonth == "No Expiry" || selectedYear == "No Expiry") {
            return ""
        }
        
        val monthMap = mapOf(
            "January" to "01", "February" to "02", "March" to "03", "April" to "04",
            "May" to "05", "June" to "06", "July" to "07", "August" to "08",
            "September" to "09", "October" to "10", "November" to "11", "December" to "12"
        )
        
        val monthNumber = monthMap[selectedMonth] ?: "01"
        return "$selectedYear-$monthNumber-01"
    }

    private fun deletePassword() {
        passwordRepository.deletePassword(passwordEntry.id)
        Toast.makeText(requireContext(), "Password deleted successfully", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun setupPasswordPreview() {
        try {
            val passwordPreviewRoot = binding.passwordPreview.root
            previewContext = passwordPreviewRoot.findViewById(R.id.context_text)
            previewAppName = passwordPreviewRoot.findViewById(R.id.app_name_text)
            previewUsername = passwordPreviewRoot.findViewById(R.id.username_text)
            previewPassword = passwordPreviewRoot.findViewById(R.id.password_text)
            previewExpiry = passwordPreviewRoot.findViewById(R.id.expiry_text)
            
            // Apply glassmorphism background using password's position in list
            val passwordPosition = getPasswordPosition()
            val backgroundRes = CardColorUtil.getGlassmorphismCardBackground(requireContext(), passwordPosition)
            passwordPreviewRoot.setBackgroundResource(backgroundRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun getPasswordPosition(): Int {
        val allPasswords = passwordRepository.getAllPasswords()
        return allPasswords.indexOfFirst { it.id == passwordEntry.id }.takeIf { it >= 0 } ?: 0
    }
    
    private fun setupTextWatchers() {
        binding.etContext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updatePasswordPreview()
            }
        })
        
        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updatePasswordPreview()
            }
        })
        
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updatePasswordPreview()
            }
        })
    }
    
    private fun updatePasswordPreview() {
        try {
            val context = binding.etContext.text.toString()
            val username = binding.etUsername.text.toString()
            
            if (::previewContext.isInitialized) {
                previewContext.text = if (context.isNotEmpty()) context else "Context"
            }
            
            if (::previewAppName.isInitialized) {
                previewAppName.text = if (context.isNotEmpty()) "$context App" else "App Name"
            }
            
            if (::previewUsername.isInitialized) {
                previewUsername.text = if (username.isNotEmpty()) username else "Username"
            }
            
            if (::previewPassword.isInitialized) {
                val password = binding.etPassword.text.toString()
                previewPassword.text = if (password.isNotEmpty()) "•".repeat(password.length.coerceAtMost(12)) else "••••••••"
            }
            
            updateExpiryPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun updateExpiryPreview() {
        try {
            if (::previewExpiry.isInitialized) {
                val selectedMonth = binding.spinnerMonth.selectedItem?.toString()
                val selectedYear = binding.spinnerYear.selectedItem?.toString()
                
                if (selectedMonth == "No Expiry" || selectedYear == "No Expiry" || selectedMonth == null || selectedYear == null) {
                    previewExpiry.text = "Never"
                } else {
                    val monthMap = mapOf(
                        "January" to "01", "February" to "02", "March" to "03", "April" to "04",
                        "May" to "05", "June" to "06", "July" to "07", "August" to "08",
                        "September" to "09", "October" to "10", "November" to "11", "December" to "12"
                    )
                    val monthNumber = monthMap[selectedMonth] ?: "01"
                    val yearShort = selectedYear.takeLast(2)
                    previewExpiry.text = "$monthNumber/$yearShort"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun togglePasswordVisibility() {
        val passwordField = binding.etPassword
        val toggleIcon = binding.passwordVisibilityToggle
        
        if (passwordField.inputType == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD or android.text.InputType.TYPE_CLASS_TEXT) {
            // Show password
            passwordField.inputType = android.text.InputType.TYPE_CLASS_TEXT
            toggleIcon.setImageResource(R.drawable.ic_visibility)
        } else {
            // Hide password
            passwordField.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD or android.text.InputType.TYPE_CLASS_TEXT
            toggleIcon.setImageResource(R.drawable.ic_visibility_off)
        }
        
        // Move cursor to end
        passwordField.setSelection(passwordField.text?.length ?: 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 