package com.example.passwordmanager.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.passwordmanager.R
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.databinding.FragmentEditPasswordBinding
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
    private val currentMonth = LocalDate.now().monthValue

    private lateinit var previewContext: TextView
    private lateinit var previewAppName: TextView
    private lateinit var previewUsername: TextView
    private lateinit var previewPassword: TextView
    private lateinit var previewExpiry: TextView

    private val allMonths = arrayOf(
        "No Expiry", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

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
        binding.root.setBackgroundResource(R.drawable.blurred_background)

        arguments?.let { args ->
            passwordEntry = args.getParcelable("passwordEntry") ?: return@let
        }

        setupPasswordPreview()
        setupTextWatchers()
        setupExpiryDateSpinners()
        loadPasswordData()

        binding.passwordPreview.root.post {
            updatePasswordPreview()
        }

        binding.btnUpdatePassword.setOnClickListener { updatePassword() }
        binding.btnDeletePassword.setOnClickListener { deletePassword() }
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.passwordVisibilityToggle.setOnClickListener { togglePasswordVisibility() }
    }

    private fun setupExpiryDateSpinners() {
        val years = (listOf("No Expiry") + (0..20).map { (currentYear + it).toString() }).toTypedArray()
        val yearAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item_dark, years)
        yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_dark)
        binding.spinnerPasswordYear.adapter = yearAdapter

        val monthAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item_dark, allMonths)
        monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_dark)
        binding.spinnerPasswordMonth.adapter = monthAdapter

        binding.spinnerPasswordYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateMonthSpinner(years[position])
                updateExpiryPreview()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerPasswordMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateExpiryPreview()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateMonthSpinner(selectedYear: String) {
        val currentMonthName = binding.spinnerPasswordMonth.selectedItem?.toString()
        if (selectedYear == "No Expiry") {
            binding.spinnerPasswordMonth.setSelection(0)
            binding.spinnerPasswordMonth.isEnabled = false
        } else {
            binding.spinnerPasswordMonth.isEnabled = true
            val year = selectedYear.toInt()
            val months = if (year == currentYear) {
                (listOf("No Expiry") + allMonths.slice(currentMonth..12)).toTypedArray()
            } else {
                allMonths
            }
            val newAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item_dark, months)
            newAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_dark)
            binding.spinnerPasswordMonth.adapter = newAdapter

            val newIndex = months.indexOf(currentMonthName).takeIf { it >= 0 } ?: 0
            binding.spinnerPasswordMonth.setSelection(newIndex)
        }
    }

    private fun loadPasswordData() {
        binding.etContext.setText(passwordEntry.context)
        binding.etUsername.setText(passwordEntry.username)
        binding.etPassword.setText(passwordEntry.password)
        binding.etNotes.setText(passwordEntry.notes)
        setExpiryDateInSpinners(passwordEntry.expiryDate)
    }

    private fun setExpiryDateInSpinners(expiryDate: String) {
        if (expiryDate.isEmpty()) {
            binding.spinnerPasswordYear.setSelection(0)
            return
        }
        try {
            val date = LocalDate.parse(expiryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val year = date.year.toString()
            val monthName = date.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)

            val yearAdapter = binding.spinnerPasswordYear.adapter as ArrayAdapter<String>
            val yearPosition = yearAdapter.getPosition(year)
            if (yearPosition >= 0) {
                binding.spinnerPasswordYear.setSelection(yearPosition)
                // The year's onItemSelected listener will update the month spinner.
                // We need to set the month selection after the new month adapter is set.
                binding.spinnerPasswordYear.post {
                    val monthAdapter = binding.spinnerPasswordMonth.adapter as ArrayAdapter<String>
                    val monthPosition = monthAdapter.getPosition(monthName)
                    if (monthPosition >= 0) {
                        binding.spinnerPasswordMonth.setSelection(monthPosition)
                    }
                }
            }
        } catch (e: Exception) {
            binding.spinnerPasswordYear.setSelection(0)
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
            Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Context and password are required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getExpiryDateFromSpinners(): String {
        val selectedYear = binding.spinnerPasswordYear.selectedItem.toString()
        val selectedMonth = binding.spinnerPasswordMonth.selectedItem.toString()

        if (selectedYear == "No Expiry" || selectedMonth == "No Expiry") {
            return ""
        }

        val monthNumber = allMonths.indexOf(selectedMonth).let {
            if (it > 0) String.format("%02d", it) else "01"
        }
        return "$selectedYear-$monthNumber-01"
    }

    private fun deletePassword() {
        passwordRepository.deletePassword(passwordEntry.id)
        Toast.makeText(requireContext(), "Password deleted", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun setupPasswordPreview() {
        val passwordPreviewRoot = binding.passwordPreview.root
        previewContext = passwordPreviewRoot.findViewById(R.id.context_text)
        previewAppName = passwordPreviewRoot.findViewById(R.id.app_name_text)
        previewUsername = passwordPreviewRoot.findViewById(R.id.username_text)
        previewPassword = passwordPreviewRoot.findViewById(R.id.password_text)
        previewExpiry = passwordPreviewRoot.findViewById(R.id.expiry_text)

        val passwordPosition = passwordRepository.getAllPasswords().indexOfFirst { it.id == passwordEntry.id }.takeIf { it >= 0 } ?: 0
        val backgroundRes = CardColorUtil.getGlassmorphismCardBackground(requireContext(), passwordPosition)
        passwordPreviewRoot.setBackgroundResource(backgroundRes)
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updatePasswordPreview()
            }
        }
        binding.etContext.addTextChangedListener(textWatcher)
        binding.etUsername.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)
    }

    private fun updatePasswordPreview() {
        val context = binding.etContext.text.toString()
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        previewContext.text = context.ifEmpty { "Context" }
        previewAppName.text = if (context.isNotEmpty()) "$context App" else "App Name"
        previewUsername.text = username.ifEmpty { "Username" }
        previewPassword.text = if (password.isNotEmpty()) "•".repeat(password.length.coerceAtMost(12)) else "••••••••"
        updateExpiryPreview()
    }

    private fun updateExpiryPreview() {
        val selectedYear = binding.spinnerPasswordYear.selectedItem?.toString()
        val selectedMonth = binding.spinnerPasswordMonth.selectedItem?.toString()

        if (selectedYear == "No Expiry" || selectedMonth == "No Expiry" || selectedYear == null || selectedMonth == null) {
            previewExpiry.text = "Never"
        } else {
            val monthNumber = allMonths.indexOf(selectedMonth).let {
                if (it > 0) String.format("%02d", it) else "01"
            }
            val yearShort = selectedYear.takeLast(2)
            previewExpiry.text = "$monthNumber/$yearShort"
        }
    }

    private fun togglePasswordVisibility() {
        val passwordField = binding.etPassword
        val isPasswordVisible = passwordField.inputType != (android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD or android.text.InputType.TYPE_CLASS_TEXT)
        if (isPasswordVisible) {
            passwordField.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD or android.text.InputType.TYPE_CLASS_TEXT
            binding.passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_off)
        } else {
            passwordField.inputType = android.text.InputType.TYPE_CLASS_TEXT
            binding.passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility)
        }
        passwordField.setSelection(passwordField.text?.length ?: 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
