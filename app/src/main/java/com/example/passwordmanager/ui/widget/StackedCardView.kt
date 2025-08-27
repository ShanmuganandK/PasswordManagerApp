package com.example.passwordmanager.ui.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.GestureDetectorCompat
import com.example.passwordmanager.R
import com.example.passwordmanager.model.PasswordCard
import com.example.passwordmanager.model.CardBrand
import com.example.passwordmanager.model.CardGradient
import kotlin.math.abs

class StackedCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var cards: List<PasswordCard> = emptyList()
    private var onCardClickListener: ((PasswordCard) -> Unit)? = null
    private var selectedCardIndex = 0
    private val cardViews = mutableListOf<View>()
    private lateinit var gestureDetector: GestureDetectorCompat
    private var indicatorContainer: android.widget.LinearLayout? = null

    init {
        setupGestureDetector()
    }

    fun setCards(
        cardList: List<PasswordCard>,
        onCardClick: (PasswordCard) -> Unit
    ) {
        this.cards = cardList
        this.onCardClickListener = onCardClick
        createStackedCards()
        createIndicators()
    }

    fun setIndicatorContainer(container: android.widget.LinearLayout) {
        this.indicatorContainer = container
        createIndicators()
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                
                if (abs(diffY) > abs(diffX)) {
                    if (abs(diffY) > 100 && abs(velocityY) > 100) {
                        if (diffY > 0) {
                            // Swipe down - go to previous card
                            selectPreviousCard()
                        } else {
                            // Swipe up - go to next card
                            selectNextCard()
                        }
                        return true
                    }
                }
                return false
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                // Handle tap to select card
                val tappedCardIndex = findTappedCard(e.x, e.y)
                if (tappedCardIndex != -1) {
                    selectCardWithAnimation(tappedCardIndex)
                    onCardClickListener?.invoke(cards[tappedCardIndex])
                    return true
                }
                return false
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun createStackedCards() {
        removeAllViews()
        cardViews.clear()
        
        if (cards.isEmpty()) return

        // Create cards in reverse order (bottom to top)
        for (i in cards.indices.reversed()) {
            val card = cards[i]
            val cardView = createCardView(card, i)
            cardViews.add(cardView)
            
            // Position cards with stacked effect
            val layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(200) // Fixed card height
            )
            
            // Simple stack positioning
            val offsetY = i * dpToPx(20) // Simple 20dp offset between cards
            layoutParams.topMargin = offsetY
            
            cardView.layoutParams = layoutParams
            addView(cardView)
        }
    }

    private fun createCardView(card: PasswordCard, index: Int): View {
        val inflater = LayoutInflater.from(context)
        val cardView = inflater.inflate(R.layout.item_password_card, this, false) as CardView
        
        // Set card data
        val bankName = cardView.findViewById<TextView>(R.id.bank_name)
        val cardNumber = cardView.findViewById<TextView>(R.id.card_number)
        val cardType = cardView.findViewById<TextView>(R.id.card_type)
        val cardHolder = cardView.findViewById<TextView>(R.id.card_holder)
        val expiryDate = cardView.findViewById<TextView>(R.id.expiry_date)
        val cardBrandLogo = cardView.findViewById<ImageView>(R.id.card_brand_logo)
        val cardBackground = cardView.findViewById<View>(R.id.card_background)
        
        // Set card data using full card number format (like in your original design)
        bankName.text = card.bankName
        cardType.text = card.cardType
        cardNumber.text = formatCardNumber(card.cardNumber)
        cardHolder.text = card.cardHolder
        expiryDate.text = card.expiryDate
        
        // Set card brand logo
        cardBrandLogo.setImageResource(getCardBrandIcon(card.cardBrand))
        
        // Set card gradient background
        cardBackground.setBackgroundResource(getCardGradientBackground(card.gradientType))
        
        // Set elevation and scale based on position
        val isSelected = index == selectedCardIndex
        cardView.cardElevation = if (isSelected) dpToPx(16).toFloat() else dpToPx(8).toFloat()
        
        // Scale effect for depth
        val scale = if (isSelected) 1.0f else 0.95f - (cards.size - index - 1) * 0.02f
        cardView.scaleX = scale
        cardView.scaleY = scale
        
        // Click listener with animation
        cardView.setOnClickListener {
            selectCardWithAnimation(index)
            onCardClickListener?.invoke(card)
        }
        
        return cardView
    }

    private fun selectCardWithAnimation(newIndex: Int) {
        if (selectedCardIndex == newIndex) return
        
        selectedCardIndex = newIndex
        
        // Simple animation - just update positions without recreating cards
        animateToNewPositions()
        
        // Update indicators
        updateIndicators()
    }

    private fun animateToNewPositions() {
        // Simple, efficient animation - just update the selected card
        for (i in cards.indices) {
            val cardView = cardViews.getOrNull(cards.size - 1 - i) ?: continue
            val isSelected = i == selectedCardIndex
            
            // Simple scale and elevation animation
            cardView.animate()
                .scaleX(if (isSelected) 1.0f else 0.95f)
                .scaleY(if (isSelected) 1.0f else 0.95f)
                .setDuration(200)
                .start()
                
            // Update elevation for CardView
            if (cardView is CardView) {
                cardView.animate()
                    .setDuration(200)
                    .withStartAction {
                        cardView.cardElevation = if (isSelected) dpToPx(16).toFloat() else dpToPx(8).toFloat()
                    }
                    .start()
            }
        }
    }

    private fun calculateStackOffset(index: Int): StackOffset {
        val isSelected = index == selectedCardIndex
        val positionFromTop = if (isSelected) 0 else index - selectedCardIndex
        
        return if (isSelected) {
            // Selected card at the front
            StackOffset(0, 0, 0, 0f, 0f)
        } else {
            // Cards behind the selected one
            val offset = Math.abs(positionFromTop) * dpToPx(20)
            val sideOffset = Math.abs(positionFromTop) * dpToPx(8)
            StackOffset(offset, sideOffset, sideOffset, 0f, 0f)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    private fun selectNextCard() {
        if (cards.isNotEmpty()) {
            val nextIndex = (selectedCardIndex + 1) % cards.size
            selectCardWithAnimation(nextIndex)
            onCardClickListener?.invoke(cards[nextIndex])
        }
    }

    private fun selectPreviousCard() {
        if (cards.isNotEmpty()) {
            val prevIndex = if (selectedCardIndex == 0) cards.size - 1 else selectedCardIndex - 1
            selectCardWithAnimation(prevIndex)
            onCardClickListener?.invoke(cards[prevIndex])
        }
    }

    private fun findTappedCard(x: Float, y: Float): Int {
        // Simplified - just return the top visible card for now
        return if (cards.isNotEmpty()) 0 else -1
    }

    private fun createIndicators() {
        indicatorContainer?.let { container ->
            container.removeAllViews()
            
            for (i in cards.indices) {
                val indicator = View(context)
                val size = dpToPx(8)
                val layoutParams = android.widget.LinearLayout.LayoutParams(size, size)
                layoutParams.setMargins(dpToPx(4), 0, dpToPx(4), 0)
                indicator.layoutParams = layoutParams
                
                // Set indicator appearance
                indicator.setBackgroundResource(R.drawable.indicator_dot)
                indicator.isSelected = i == selectedCardIndex
                
                container.addView(indicator)
            }
        }
    }

    private fun updateIndicators() {
        indicatorContainer?.let { container ->
            for (i in 0 until container.childCount) {
                val indicator = container.getChildAt(i)
                indicator.isSelected = i == selectedCardIndex
            }
        }
    }

    private fun formatCardNumber(cardNumber: String): String {
        // Format card number with spaces like "4572 6950 0036 6077"
        return cardNumber.chunked(4).joinToString(" ")
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

    private fun getCardGradientBackground(gradient: CardGradient): Int {
        return when (gradient) {
            CardGradient.GOLD -> R.drawable.card_gradient_gold
            CardGradient.PLATINUM -> R.drawable.card_gradient_platinum
            CardGradient.BLACK -> R.drawable.card_gradient_black
            CardGradient.BLUE -> R.drawable.card_gradient_blue
            CardGradient.GREEN -> R.drawable.card_gradient_green
            CardGradient.DEFAULT -> R.drawable.card_gradient_background
        }
    }

    private data class StackOffset(
        val topMargin: Int,
        val leftMargin: Int,
        val rightMargin: Int,
        val translationX: Float,
        val translationY: Float
    )
}