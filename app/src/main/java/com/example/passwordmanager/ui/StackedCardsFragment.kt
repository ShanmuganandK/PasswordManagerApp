package com.example.passwordmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.passwordmanager.R
import com.example.passwordmanager.ui.widget.StackedCardView
import com.example.passwordmanager.model.PasswordCard
import com.example.passwordmanager.model.CardBrand
import com.example.passwordmanager.model.CardGradient

class StackedCardsFragment : Fragment() {

    private lateinit var stackedCardView: StackedCardView

    // Sample data for stacked cards using the same model as password cards
    private val sampleCards = listOf(
        PasswordCard(
            id = "1",
            bankName = "HDFC Bank",
            cardType = "Credit Card",
            cardNumber = "4532123456783768",
            cardHolder = "JOHN DOE",
            expiryDate = "12/25",
            cvv = "123",
            cardBrand = CardBrand.VISA,
            gradientType = CardGradient.BLUE
        ),
        PasswordCard(
            id = "2",
            bankName = "Emirates Islamic",
            cardType = "Credit Card", 
            cardNumber = "5555123456781234",
            cardHolder = "JANE SMITH",
            expiryDate = "08/26",
            cvv = "456",
            cardBrand = CardBrand.MASTERCARD,
            gradientType = CardGradient.GREEN
        ),
        PasswordCard(
            id = "3",
            bankName = "Dubai First",
            cardType = "Credit Card",
            cardNumber = "4111123456785678",
            cardHolder = "ALEX JOHNSON",
            expiryDate = "03/27",
            cvv = "789",
            cardBrand = CardBrand.VISA,
            gradientType = CardGradient.GOLD
        ),
        PasswordCard(
            id = "4",
            bankName = "ADCB",
            cardType = "Credit Card",
            cardNumber = "3782123456789012",
            cardHolder = "SARAH WILSON",
            expiryDate = "11/25",
            cvv = "012",
            cardBrand = CardBrand.AMEX,
            gradientType = CardGradient.PLATINUM
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stacked_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupStackedCards(view)
    }

    private fun setupStackedCards(view: View) {
        stackedCardView = view.findViewById(R.id.stacked_card_view)
        val indicatorsContainer = view.findViewById<android.widget.LinearLayout>(R.id.card_indicators)
        
        // Set the cards with click listener
        stackedCardView.setCards(sampleCards) { card ->
            Toast.makeText(context, "Selected: ${card.bankName}", Toast.LENGTH_SHORT).show()
        }
        
        // Setup indicators
        stackedCardView.setIndicatorContainer(indicatorsContainer)
    }
}