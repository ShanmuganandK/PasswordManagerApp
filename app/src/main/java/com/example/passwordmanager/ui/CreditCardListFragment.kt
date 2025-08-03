package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.FragmentCreditCardListBinding
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.model.CreditCardEntry

class CreditCardListFragment : Fragment() {

    private var _binding: FragmentCreditCardListBinding? = null
    private val binding get() = _binding!!
    private lateinit var passwordRepository: PasswordRepository
    private lateinit var creditCardAdapter: CreditCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditCardListBinding.inflate(inflater, container, false)
        passwordRepository = PasswordRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.creditCardRecyclerView.layoutManager = LinearLayoutManager(context)
        loadCreditCards()
        
        // Set up navigation buttons
        binding.addCreditCardButton.setOnClickListener {
            findNavController().navigate(R.id.action_creditCardListFragment_to_creditCardFragment)
        }
        
        binding.passwordsButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadCreditCards() {
        val creditCards: List<CreditCardEntry> = passwordRepository.getAllCreditCards()
        creditCardAdapter = CreditCardAdapter(creditCards)
        binding.creditCardRecyclerView.adapter = creditCardAdapter
    }

    inner class CreditCardAdapter(private val creditCards: List<CreditCardEntry>) :
        RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_credit_card, parent, false)
            return CreditCardViewHolder(view)
        }

        override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
            val creditCardEntry = creditCards[position]
            holder.bind(creditCardEntry)
        }

        override fun getItemCount(): Int = creditCards.size

        inner class CreditCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val cardHolderText: TextView = itemView.findViewById(R.id.card_holder_text)
            private val cardNumberText: TextView = itemView.findViewById(R.id.card_number_text)
            private val cardTypeText: TextView = itemView.findViewById(R.id.card_type_text)
            private val bankNameText: TextView = itemView.findViewById(R.id.bank_name_text)
            private val expiryDateText: TextView = itemView.findViewById(R.id.expiry_date_text)
            private val notesText: TextView = itemView.findViewById(R.id.notes_text)

            fun bind(creditCardEntry: CreditCardEntry) {
                cardHolderText.text = "Card Holder: ${creditCardEntry.cardHolder}"
                cardNumberText.text = "Card Number: ${maskCardNumber(creditCardEntry.cardNumber)}"
                cardTypeText.text = if (creditCardEntry.cardType.isNotEmpty()) "Type: ${creditCardEntry.cardType}" else ""
                cardTypeText.visibility = if (creditCardEntry.cardType.isNotEmpty()) View.VISIBLE else View.GONE
                bankNameText.text = if (creditCardEntry.bankName.isNotEmpty()) "Bank: ${creditCardEntry.bankName}" else ""
                bankNameText.visibility = if (creditCardEntry.bankName.isNotEmpty()) View.VISIBLE else View.GONE
                expiryDateText.text = "Expiry: ${formatExpiryDate(creditCardEntry.expiryDate)}"
                notesText.text = if (creditCardEntry.notes.isNotEmpty()) "Notes: ${creditCardEntry.notes}" else ""
                notesText.visibility = if (creditCardEntry.notes.isNotEmpty()) View.VISIBLE else View.GONE
                
                // Add click listener for editing
                itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("creditCardEntry", creditCardEntry)
                    findNavController().navigate(R.id.action_creditCardListFragment_to_editCreditCardFragment, bundle)
                }
            }

            private fun formatExpiryDate(expiryDate: String): String {
                return try {
                    val date = java.time.LocalDate.parse(expiryDate, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val monthName = date.month.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH)
                    "${monthName} ${date.year}"
                } catch (e: Exception) {
                    expiryDate
                }
            }

            private fun maskCardNumber(cardNumber: String): String {
                return if (cardNumber.length >= 4) {
                    "**** **** **** ${cardNumber.takeLast(4)}"
                } else {
                    cardNumber
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadCreditCards()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 