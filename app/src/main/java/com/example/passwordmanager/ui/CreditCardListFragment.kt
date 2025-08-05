package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.FragmentCreditCardListBinding
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.model.CreditCardEntry
import com.example.passwordmanager.util.CardColorUtil
import com.example.passwordmanager.util.StackedCardDecoration

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
        
        // Detect dark theme
        val isDarkTheme = (resources.configuration.uiMode and 
            android.content.res.Configuration.UI_MODE_NIGHT_MASK) == 
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        
        val stackedDecoration = StackedCardDecoration()
        stackedDecoration.setDarkTheme(isDarkTheme)
        
        binding.creditCardRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.creditCardRecyclerView.addItemDecoration(stackedDecoration)
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
            holder.bind(creditCardEntry, position)
            
            // Add stacked card animation with progressive offset
            holder.itemView.alpha = 0f
            holder.itemView.translationY = 50f
            holder.itemView.translationX = (position * 4).toFloat() // Slight horizontal offset for stack effect
            
            holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(position * 80L)
                .start()
        }

        override fun getItemCount(): Int = creditCards.size

        inner class CreditCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val cardHolderText: TextView = itemView.findViewById(R.id.card_holder_text)
            private val cardNumberText: TextView = itemView.findViewById(R.id.card_number_text)
            private val expiryDateText: TextView = itemView.findViewById(R.id.expiry_date_text)
            private val cardTitleText: TextView = itemView.findViewById(R.id.card_title)
            private val bankNameText: TextView = itemView.findViewById(R.id.bank_name_text)

            fun bind(creditCardEntry: CreditCardEntry, position: Int) {
                val frameLayout = itemView as FrameLayout
                frameLayout.background = CardColorUtil.getCardGradient(itemView.context, position)
                frameLayout.foreground = ContextCompat.getDrawable(itemView.context, R.drawable.card_clipper)
                cardHolderText.text = creditCardEntry.cardHolder.uppercase()
                cardNumberText.text = maskCardNumber(creditCardEntry.cardNumber)
                expiryDateText.text = formatExpiryDate(creditCardEntry.expiryDate)
                if (creditCardEntry.cardType.isNotEmpty()) {
                    cardTitleText.text = creditCardEntry.cardType
                } else {
                    cardTitleText.text = "CREDIT CARD"
                }
                if (creditCardEntry.bankName.isNotEmpty()) {
                    bankNameText.text = creditCardEntry.bankName
                    bankNameText.visibility = View.VISIBLE
                } else {
                    bankNameText.visibility = View.GONE
                }
                
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
                    date.format(java.time.format.DateTimeFormatter.ofPattern("MM/yy"))
                } catch (e: Exception) {
                    expiryDate
                }
            }

            private fun maskCardNumber(cardNumber: String): String {
                if (cardNumber.length != 16) return "**** **** **** ****"
                return "${cardNumber.substring(0, 4)} ${cardNumber.substring(4, 8)} ${cardNumber.substring(8, 12)} ${cardNumber.substring(12, 16)}"
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
