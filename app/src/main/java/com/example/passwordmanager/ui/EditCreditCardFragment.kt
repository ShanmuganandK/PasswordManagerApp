package com.example.passwordmanager.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.FragmentEditCreditCardBinding
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.model.CreditCardEntry
import com.example.passwordmanager.util.CardType
import com.example.passwordmanager.util.CardColorUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditCreditCardFragment : Fragment() {

    private var _binding: FragmentEditCreditCardBinding? = null
    private val binding get() = _binding!!
    private lateinit var passwordRepository: PasswordRepository
    private lateinit var creditCardEntry: CreditCardEntry
    
    // Card preview views
    private lateinit var previewCardType: TextView
    private lateinit var previewCardNumber: TextView
    private lateinit var previewCardHolder: TextView
    private lateinit var previewExpiryDate: TextView
    private lateinit var previewCvv: TextView
    private lateinit var previewBankName: TextView

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
        
        // Set metallic gradient background to match main page
        binding.root.setBackgroundResource(R.drawable.metallic_black_gradient)

        // Get credit card entry from arguments
        arguments?.let { args ->
            creditCardEntry = args.getParcelable("creditCardEntry") ?: return@let
        }

        setupCardPreview()
        setupExpiryDateSpinners()
        setupCardNumberValidation()
        setupTextWatchers()
        // Load existing credit card data
        loadCreditCardData()
        
        // Update preview after loading data
        updateCardPreview()

        binding.btnUpdateCard.setOnClickListener {
            updateCreditCard()
        }

        binding.btnDeleteCard.setOnClickListener {
            deleteCreditCard()
        }
    }

    private val currentYear = LocalDate.now().year
    private val currentMonth = LocalDate.now().monthValue - 1 // 0-based index
    private val allMonths = arrayOf("January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December")

    private fun setupCardPreview() {
        try {
            val cardPreviewRoot = binding.cardPreview.root
            previewCardType = cardPreviewRoot.findViewById(R.id.card_title)
            previewCardNumber = cardPreviewRoot.findViewById(R.id.card_number_text)
            previewCardHolder = cardPreviewRoot.findViewById(R.id.card_holder_text)
            previewExpiryDate = cardPreviewRoot.findViewById(R.id.expiry_date_text)
            previewBankName = cardPreviewRoot.findViewById(R.id.bank_name_text)
            
            // Create a hidden CVV view reference (not displayed on card)
            previewCvv = TextView(requireContext())
            
            // Apply gradient background using card's position in list
            val cardPosition = getCardPosition()
            cardPreviewRoot.background = CardColorUtil.getCardGradient(requireContext(), cardPosition)
        } catch (e: Exception) {
            // Handle case where card preview is not available
            e.printStackTrace()
        }
    }
    
    private fun getCardPosition(): Int {
        val allCards = passwordRepository.getAllCreditCards()
        return allCards.indexOfFirst { it.id == creditCardEntry.id }.takeIf { it >= 0 } ?: 0
    }

    private fun setupTextWatchers() {
        // Card number watcher
        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateCardPreview()
            }
        })
        
        // Card holder watcher
        binding.etCardHolder.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateCardPreview()
            }
        })
        
        // CVV watcher
        binding.etCvv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateCardPreview()
            }
        })
        
        // Bank name watcher
        binding.etBankName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateCardPreview()
            }
        })
    }

    private fun updateCardPreview() {
        try {
            val cardNumber = binding.etCardNumber.text.toString()
            val cardHolder = binding.etCardHolder.text.toString()
            val cvv = binding.etCvv.text.toString()
            val bankName = binding.etBankName.text.toString()
            
            // Update card type
            val cardType = CardType.detectCardType(cardNumber)
            if (::previewCardType.isInitialized) {
                previewCardType.text = if (cardType != CardType.UNKNOWN) cardType.displayName else "CREDIT CARD"
            }
            
            // Update bank name
            if (::previewBankName.isInitialized) {
                previewBankName.text = if (bankName.isNotEmpty()) bankName else "Bank Name"
            }
            
            // Update card number
            if (::previewCardNumber.isInitialized) {
                previewCardNumber.text = if (cardNumber.isNotEmpty()) {
                    maskCardNumberForPreview(cardNumber)
                } else {
                    "**** **** **** ****"
                }
            }
            
            // Update card holder
            if (::previewCardHolder.isInitialized) {
                previewCardHolder.text = if (cardHolder.isNotEmpty()) {
                    cardHolder.uppercase()
                } else {
                    "CARD HOLDER NAME"
                }
            }
            
            // Update CVV
            if (::previewCvv.isInitialized) {
                previewCvv.text = if (cvv.isNotEmpty()) {
                    "*".repeat(cvv.length)
                } else {
                    "***"
                }
            }
            
            // Update expiry date
            updateExpiryPreview()
        } catch (e: Exception) {
            // Handle preview update errors gracefully
            e.printStackTrace()
        }
    }
    
    private fun updateExpiryPreview() {
        try {
            if (::previewExpiryDate.isInitialized) {
                val selectedMonth = binding.spinnerMonth.selectedItem?.toString()
                val selectedYear = binding.spinnerYear.selectedItem?.toString()
                
                if (selectedMonth != null && selectedYear != null) {
                    val monthMap = mapOf(
                        "January" to "01", "February" to "02", "March" to "03", "April" to "04",
                        "May" to "05", "June" to "06", "July" to "07", "August" to "08",
                        "September" to "09", "October" to "10", "November" to "11", "December" to "12"
                    )
                    val monthNumber = monthMap[selectedMonth] ?: "01"
                    val yearShort = selectedYear.takeLast(2)
                    previewExpiryDate.text = "$monthNumber/$yearShort"
                } else {
                    previewExpiryDate.text = "MM/YY"
                }
            }
        } catch (e: Exception) {
            // Handle expiry preview update errors gracefully
            e.printStackTrace()
        }
    }
    
    private fun maskCardNumberForPreview(cardNumber: String): String {
        return when {
            cardNumber.length <= 4 -> cardNumber.padEnd(4, '*')
            cardNumber.length <= 8 -> "${cardNumber.substring(0, 4)} ${cardNumber.substring(4).padEnd(4, '*')}"
            cardNumber.length <= 12 -> "${cardNumber.substring(0, 4)} ${cardNumber.substring(4, 8)} ${cardNumber.substring(8).padEnd(4, '*')}"
            else -> "${cardNumber.substring(0, 4)} ${cardNumber.substring(4, 8)} ${cardNumber.substring(8, 12)} ${cardNumber.substring(12).padEnd(4, '*')}"
        }.let { formatted ->
            if (formatted.length < 19) formatted.padEnd(19, '*') else formatted
        }
    }

    private fun setupExpiryDateSpinners() {
        // Setup year spinner (current year to 20 years from now)
        val years = mutableListOf<String>()
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
        
        // Add listeners for expiry date preview updates
        binding.spinnerMonth.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                updateExpiryPreview()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupYearSpinnerListener() {
        val years = mutableListOf<String>()
        for (i in 0..20) {
            years.add((currentYear + i).toString())
        }
        
        binding.spinnerYear.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // Get the current month name before changing the adapter
                val currentMonthName = binding.spinnerMonth.selectedItem?.toString()
                
                val selectedYear = years[position].toInt()
                if (selectedYear == currentYear) {
                    // For current year, show only months from current month onwards
                    val validMonths = allMonths.slice(currentMonth until allMonths.size).toTypedArray()
                    val newMonthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, validMonths)
                    newMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerMonth.adapter = newMonthAdapter
                    
                    // Try to preserve the current selection if it's valid for current year
                    if (currentMonthName != null && validMonths.contains(currentMonthName)) {
                        val preservedIndex = validMonths.indexOf(currentMonthName)
                        binding.spinnerMonth.setSelection(preservedIndex)
                    } else {
                        // Set to current month if no valid selection
                        binding.spinnerMonth.setSelection(0)
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
                        // Set to January if no valid selection
                        binding.spinnerMonth.setSelection(0)
                    }
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupCardNumberValidation() {
        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val cardNumber = s.toString()
                if (cardNumber.isNotEmpty()) {
                    val cardType = CardType.detectCardType(cardNumber)
                    val isValid = CardType.isValidCardNumber(cardNumber)
                    
                    // Update card type display
                    binding.tvCardType.text = cardType.displayName
                    
                    // Update validation message
                    if (cardType != CardType.UNKNOWN) {
                        if (isValid) {
                            binding.tvValidationMessage.text = "✓ Valid ${cardType.displayName} card"
                            binding.tvValidationMessage.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                            binding.tvValidationMessage.visibility = View.VISIBLE
                        } else {
                            binding.tvValidationMessage.text = "⚠ Invalid ${cardType.displayName} card number"
                            binding.tvValidationMessage.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark))
                            binding.tvValidationMessage.visibility = View.VISIBLE
                        }
                    } else {
                        if (isValid) {
                            binding.tvValidationMessage.text = "✓ Valid card number (type not identified)"
                            binding.tvValidationMessage.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                            binding.tvValidationMessage.visibility = View.VISIBLE
                        } else {
                            binding.tvValidationMessage.text = "⚠ Invalid card number"
                            binding.tvValidationMessage.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                            binding.tvValidationMessage.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.tvCardType.text = ""
                    binding.tvValidationMessage.visibility = View.GONE
                }
            }
        })
    }

    private fun loadCreditCardData() {
        binding.etCardNumber.setText(creditCardEntry.cardNumber)
        binding.etCardHolder.setText(creditCardEntry.cardHolder)
        binding.etBankName.setText(creditCardEntry.bankName)
        
        // Temporarily disable the year spinner listener
        binding.spinnerYear.onItemSelectedListener = null
        
        setExpiryDateInSpinners(creditCardEntry.expiryDate)
        
        // Re-enable the year spinner listener
        setupYearSpinnerListener()
        
        binding.etCvv.setText(creditCardEntry.cvv)
        binding.etNotes.setText(creditCardEntry.notes)
    }

    private fun setExpiryDateInSpinners(expiryDate: String) {
        if (expiryDate.isEmpty()) {
            return
        }

        try {
            val date = LocalDate.parse(expiryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val monthName = date.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
            val year = date.year.toString()

            // Set year first
            val yearIndex = year.toInt() - currentYear
            if (yearIndex >= 0 && yearIndex <= 20) {
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
            // If parsing fails, set to current month and year
            binding.spinnerMonth.setSelection(0)
            binding.spinnerYear.setSelection(0)
        }
    }

    private fun updateCreditCard() {
        val cardNumber = binding.etCardNumber.text.toString()
        val cardHolder = binding.etCardHolder.text.toString()
        val bankName = binding.etBankName.text.toString()
        val expiryDate = getExpiryDateFromSpinners()
        val cvv = binding.etCvv.text.toString()
        val notes = binding.etNotes.text.toString()

        if (cardNumber.isNotEmpty() && cardHolder.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()) {
            val cardType = CardType.detectCardType(cardNumber)
            val isValid = CardType.isValidCardNumber(cardNumber)
            
            val updatedCreditCard = creditCardEntry.copy(
                cardNumber = cardNumber,
                cardHolder = cardHolder,
                bankName = bankName,
                cardType = cardType.displayName,
                expiryDate = expiryDate,
                cvv = cvv,
                notes = notes
            )
            passwordRepository.updateCreditCard(updatedCreditCard)
            
            val message = if (cardType != CardType.UNKNOWN) {
                if (isValid) {
                    "Credit card updated successfully"
                } else {
                    "Credit card updated (${cardType.displayName} validation failed)"
                }
            } else {
                if (isValid) {
                    "Credit card updated (type not identified)"
                } else {
                    "Credit card updated (validation failed)"
                }
            }
            
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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