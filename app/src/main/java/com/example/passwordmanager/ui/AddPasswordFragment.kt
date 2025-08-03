package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.FragmentAddPasswordBinding
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.model.PasswordEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddPasswordFragment : Fragment() {

    private var _binding: FragmentAddPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var passwordRepository: PasswordRepository
    private val currentYear = LocalDate.now().year
    private val currentMonth = LocalDate.now().monthValue // 1-based index, but we need to account for "No Expiry"
    private val allMonths = arrayOf("No Expiry", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPasswordBinding.inflate(inflater, container, false)
        passwordRepository = PasswordRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupExpiryDateSpinners()
        binding.btnSavePassword.setOnClickListener {
            savePassword()
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

        // Set current month and year as default (skip "No Expiry")
        binding.spinnerMonth.setSelection(currentMonth)
        binding.spinnerYear.setSelection(1) // Current year is at index 1 (after "No Expiry")

        // Add listener to year spinner to dynamically filter months
        binding.spinnerYear.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
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
                        
                        // Set to current month (which is now at index 1, after "No Expiry")
                        binding.spinnerMonth.setSelection(1)
                    } else {
                        // For future years, show all months
                        val newMonthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, allMonths)
                        newMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerMonth.adapter = newMonthAdapter
                        
                        // Set to "No Expiry" for future years
                        binding.spinnerMonth.setSelection(0)
                    }
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun savePassword() {
        val password = binding.etPassword.text.toString()
        val context = binding.etContext.text.toString()
        val username = binding.etUsername.text.toString()
        val expiryDate = getExpiryDateFromSpinners()
        val notes = binding.etNotes.text.toString()

        if (password.isNotEmpty() && context.isNotEmpty()) {
            val passwordEntry = PasswordEntry(context, username, password, expiryDate, notes)
            passwordRepository.addPassword(passwordEntry)
            Toast.makeText(requireContext(), "Password saved successfully", Toast.LENGTH_SHORT).show()
            
            // Navigate back to password list
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}