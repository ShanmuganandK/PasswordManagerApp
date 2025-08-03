package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.passwordmanager.databinding.FragmentEditPasswordBinding
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.model.PasswordEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditPasswordFragment : Fragment() {

    private var _binding: FragmentEditPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var passwordRepository: PasswordRepository
    private lateinit var passwordEntry: PasswordEntry
    private val currentYear = LocalDate.now().year
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

        // Get password entry from arguments
        arguments?.let { args ->
            passwordEntry = args.getParcelable("passwordEntry") ?: return@let
        }

        setupExpiryDateSpinners()
        // Load existing password data
        loadPasswordData()

        binding.btnUpdatePassword.setOnClickListener {
            updatePassword()
        }

        binding.btnDeletePassword.setOnClickListener {
            deletePassword()
        }
    }

    private fun setupExpiryDateSpinners() {
        // Setup year spinner (current year to 20 years from now)
        val years = mutableListOf<String>()
        years.add("No Expiry")
        for (i in 0..20) {
            years.add((currentYear + i).toString())
        }
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerYear.adapter = yearAdapter

        // Create dynamic month adapter
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, allMonths)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
                        val newMonthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, validMonths)
                        newMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
                        val newMonthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, allMonths)
                        newMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 