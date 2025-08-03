package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.passwordmanager.databinding.FragmentEditCreditCardBinding
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.model.CreditCardEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditCreditCardFragment : Fragment() {

    private var _binding: FragmentEditCreditCardBinding? = null
    private val binding get() = _binding!!
    private lateinit var passwordRepository: PasswordRepository
    private lateinit var creditCardEntry: CreditCardEntry

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditCreditCardBinding.inflate(inflater, container, false)
        passwordRepository = PasswordRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get credit card entry from arguments
        arguments?.let { args ->
            creditCardEntry = args.getParcelable("creditCardEntry") ?: return@let
        }

        setupExpiryDateSpinners()
        // Load existing credit card data
        loadCreditCardData()

        binding.btnUpdateCard.setOnClickListener {
            updateCreditCard()
        }

        binding.btnDeleteCard.setOnClickListener {
            deleteCreditCard()
        }
    }

    private fun setupExpiryDateSpinners() {
        // Setup month spinner
        val months = arrayOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMonth.adapter = monthAdapter

        // Setup year spinner (current year to 20 years from now)
        val currentYear = LocalDate.now().year
        val years = mutableListOf<String>()
        for (i in 0..20) {
            years.add((currentYear + i).toString())
        }
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerYear.adapter = yearAdapter
    }

    private fun loadCreditCardData() {
        binding.etCardNumber.setText(creditCardEntry.cardNumber)
        binding.etCardHolder.setText(creditCardEntry.cardHolder)
        setExpiryDateInSpinners(creditCardEntry.expiryDate)
        binding.etCvv.setText(creditCardEntry.cvv)
    }

    private fun setExpiryDateInSpinners(expiryDate: String) {
        if (expiryDate.isEmpty()) {
            return
        }

        try {
            val date = LocalDate.parse(expiryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val monthName = date.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
            val year = date.year.toString()

            val months = arrayOf("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December")
            val monthIndex = months.indexOf(monthName)
            if (monthIndex != -1) {
                binding.spinnerMonth.setSelection(monthIndex)
            }

            val currentYear = LocalDate.now().year
            val yearIndex = year.toInt() - currentYear
            if (yearIndex >= 0 && yearIndex <= 20) {
                binding.spinnerYear.setSelection(yearIndex)
            }
        } catch (e: Exception) {
            // If parsing fails, set to current month/year
            val currentDate = LocalDate.now()
            binding.spinnerMonth.setSelection(currentDate.monthValue - 1)
            binding.spinnerYear.setSelection(0)
        }
    }

    private fun updateCreditCard() {
        val cardNumber = binding.etCardNumber.text.toString()
        val cardHolder = binding.etCardHolder.text.toString()
        val expiryDate = getExpiryDateFromSpinners()
        val cvv = binding.etCvv.text.toString()

        if (cardNumber.isNotEmpty() && cardHolder.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()) {
            val updatedCreditCard = creditCardEntry.copy(
                cardNumber = cardNumber,
                cardHolder = cardHolder,
                expiryDate = expiryDate,
                cvv = cvv
            )
            passwordRepository.updateCreditCard(updatedCreditCard)
            Toast.makeText(requireContext(), "Credit card updated successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getExpiryDateFromSpinners(): String {
        val selectedMonth = binding.spinnerMonth.selectedItem.toString()
        val selectedYear = binding.spinnerYear.selectedItem.toString()
        
        val monthMap = mapOf(
            "January" to "01", "February" to "02", "March" to "03", "April" to "04",
            "May" to "05", "June" to "06", "July" to "07", "August" to "08",
            "September" to "09", "October" to "10", "November" to "11", "December" to "12"
        )
        
        val monthNumber = monthMap[selectedMonth] ?: "01"
        return "$selectedYear-$monthNumber-01"
    }

    private fun deleteCreditCard() {
        passwordRepository.deleteCreditCard(creditCardEntry.id)
        Toast.makeText(requireContext(), "Credit card deleted successfully", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 