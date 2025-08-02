package com.example.passwordmanager.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.FragmentCreditCardBinding
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.model.CreditCardEntry

class CreditCardFragment : Fragment() {

    private var _binding: FragmentCreditCardBinding? = null
    private val binding get() = _binding!!
    private lateinit var passwordRepository: PasswordRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditCardBinding.inflate(inflater, container, false)
        passwordRepository = PasswordRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveCard.setOnClickListener {
            saveCreditCard()
        }

        binding.btnUploadFront.setOnClickListener {
            // Logic to upload front image
            Toast.makeText(context, "Upload front image functionality", Toast.LENGTH_SHORT).show()
        }

        binding.btnUploadBack.setOnClickListener {
            // Logic to upload back image
            Toast.makeText(context, "Upload back image functionality", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCreditCard() {
        val cardNumber = binding.etCardNumber.text.toString()
        val cardHolder = binding.etCardHolder.text.toString()
        val expiryDate = binding.etExpiryDate.text.toString()
        val cvv = binding.etCvv.text.toString()

        if (cardNumber.isNotEmpty() && cardHolder.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()) {
            val creditCardEntry = CreditCardEntry(
                cardNumber = cardNumber,
                cardHolder = cardHolder,
                expiryDate = expiryDate,
                cvv = cvv
            )
            passwordRepository.addCreditCard(creditCardEntry)
            Toast.makeText(requireContext(), "Credit card saved successfully", Toast.LENGTH_SHORT).show()
            
            // Navigate back to password list
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}