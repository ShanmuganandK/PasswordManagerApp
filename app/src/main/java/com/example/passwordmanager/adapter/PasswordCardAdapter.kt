package com.example.passwordmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.R
import com.example.passwordmanager.model.PasswordCard
import com.example.passwordmanager.model.CardBrand

class PasswordCardAdapter(
    private var cards: List<PasswordCard>,
    private val onCardClick: (PasswordCard, Int) -> Unit
) : RecyclerView.Adapter<PasswordCardAdapter.CardViewHolder>() {

    private var selectedPosition = -1

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardContainer: CardView = itemView.findViewById(R.id.card_container)
        val bankName: TextView = itemView.findViewById(R.id.bank_name)
        val cardType: TextView = itemView.findViewById(R.id.card_type)
        val cardNumber: TextView = itemView.findViewById(R.id.card_number)
        val cardHolder: TextView = itemView.findViewById(R.id.card_holder)
        val expiryDate: TextView = itemView.findViewById(R.id.expiry_date)
        val cardBrandLogo: ImageView = itemView.findViewById(R.id.card_brand_logo)
        val selectionIndicator: View = itemView.findViewById(R.id.selection_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        try {
            val card = cards[position]
            
            holder.bankName.text = card.bankName
            holder.cardType.text = card.cardType
            holder.cardNumber.text = maskCardNumber(card.cardNumber)
            holder.cardHolder.text = card.cardHolder
            holder.expiryDate.text = card.expiryDate
            
            // Set card brand logo
            holder.cardBrandLogo.setImageResource(getCardBrandIcon(card.cardBrand))
            
            // Set card gradient background
            val backgroundView = holder.itemView.findViewById<View>(R.id.card_background)
            backgroundView?.setBackgroundResource(getCardGradientBackground(card.gradientType))
            
            // Handle selection state
            val isSelected = position == selectedPosition
            holder.cardContainer.isSelected = isSelected
            holder.selectionIndicator.visibility = if (isSelected) View.VISIBLE else View.GONE
            
            // Set click listener
            holder.cardContainer.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = if (selectedPosition == position) -1 else position
                
                // Notify changes for animation
                if (previousSelected != -1) {
                    notifyItemChanged(previousSelected)
                }
                if (selectedPosition != -1) {
                    notifyItemChanged(selectedPosition)
                }
                
                onCardClick(card, position)
            }
        } catch (e: Exception) {
            android.util.Log.e("PasswordCardAdapter", "Error binding view holder at position $position", e)
        }
    }

    override fun getItemCount(): Int = cards.size

    fun updateCards(newCards: List<PasswordCard>) {
        cards = newCards
        notifyDataSetChanged()
    }

    fun clearSelection() {
        val previousSelected = selectedPosition
        selectedPosition = -1
        if (previousSelected != -1) {
            notifyItemChanged(previousSelected)
        }
    }

    private fun maskCardNumber(cardNumber: String): String {
        return if (cardNumber.length >= 4) {
            "•••• ${cardNumber.takeLast(4)}"
        } else {
            "•••• ••••"
        }
    }

    private fun getCardBrandIcon(brand: CardBrand): Int {
        return when (brand) {
            CardBrand.VISA -> R.drawable.ic_visa
            CardBrand.MASTERCARD -> R.drawable.ic_mastercard
            CardBrand.AMEX -> R.drawable.ic_amex
            CardBrand.DISCOVER -> R.drawable.ic_discover
            CardBrand.DEFAULT -> R.drawable.ic_card_default
        }
    }

    private fun getCardGradientBackground(gradient: com.example.passwordmanager.model.CardGradient): Int {
        return when (gradient) {
            com.example.passwordmanager.model.CardGradient.GOLD -> R.drawable.card_gradient_gold
            com.example.passwordmanager.model.CardGradient.PLATINUM -> R.drawable.card_gradient_platinum
            com.example.passwordmanager.model.CardGradient.BLACK -> R.drawable.card_gradient_black
            com.example.passwordmanager.model.CardGradient.BLUE -> R.drawable.card_gradient_blue
            com.example.passwordmanager.model.CardGradient.GREEN -> R.drawable.card_gradient_green
            com.example.passwordmanager.model.CardGradient.DEFAULT -> R.drawable.card_gradient_background
        }
    }
}