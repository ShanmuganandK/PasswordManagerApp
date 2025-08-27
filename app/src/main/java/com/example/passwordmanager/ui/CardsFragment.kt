package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.passwordmanager.R
import com.example.passwordmanager.model.PasswordCard
import com.example.passwordmanager.model.CardBrand
import com.example.passwordmanager.model.CardGradient
import com.example.passwordmanager.ui.widget.StackedCardView

class CardsFragment : Fragment() {

    private lateinit var stackedCardView: StackedCardView
    private val sampleCards = listOf(
        PasswordCard(
            id = "1",
            bankName = "ENBD",
            cardType = "Credit Card",
            cardNumber = "4572695000366077",
            cardHolder = "SHANMUGANAND K",
            expiryDate = "10/26",
            cvv = "123",
            cardBrand = CardBrand.VISA,
            gradientType = CardGradient.DEFAULT
        ),
        PasswordCard(
            id = "2",
            bankName = "Dubai First",
            cardType = "Credit Card",
            cardNumber = "5242041365303768",
            cardHolder = "SHANMUGANAND K",
            expiryDate = "03/30",
            cvv = "456",
            cardBrand = CardBrand.MASTERCARD,
            gradientType = CardGradient.BLUE
        ),
        PasswordCard(
            id = "3",
            bankName = "Citi Bank",
            cardType = "Credit Card",
            cardNumber = "5291200001927741",
            cardHolder = "SHANMUGANAND K",
            expiryDate = "08/28",
            cvv = "789",
            cardBrand = CardBrand.MASTERCARD,
            gradientType = CardGradient.BLACK
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupStackedCards(view)
        setupClickListeners(view)
    }

    private fun setupStackedCards(view: View) {
        try {
            stackedCardView = view.findViewById(R.id.stacked_card_view)
            
            // Set the cards with click listener
            stackedCardView.setCards(sampleCards) { card ->
                onCardSelected(card)
            }
            
            // Setup indicators if they exist
            val indicatorsContainer = view.findViewById<android.widget.LinearLayout>(R.id.card_indicators)
            if (indicatorsContainer != null) {
                stackedCardView.setIndicatorContainer(indicatorsContainer)
            }
        } catch (e: Exception) {
            android.util.Log.e("CardsFragment", "Error setting up stacked cards", e)
            // Show error message to user
            android.widget.Toast.makeText(context, "Error loading cards", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners(view: View) {
        try {
            view.findViewById<View>(R.id.add_card_button)?.setOnClickListener {
                Toast.makeText(context, "Add new card", Toast.LENGTH_SHORT).show()
                // TODO: Navigate to add card screen
            }
        } catch (e: Exception) {
            android.util.Log.e("CardsFragment", "Error setting up click listeners", e)
        }
    }

    private fun onCardSelected(card: PasswordCard) {
        Toast.makeText(
            context, 
            "Selected: ${card.bankName} - ${card.cardNumber.takeLast(4)}", 
            Toast.LENGTH_SHORT
        ).show()
        
        // TODO: Navigate to edit card screen
    }


}