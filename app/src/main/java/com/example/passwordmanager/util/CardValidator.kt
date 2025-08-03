package com.example.passwordmanager.util

enum class CardType(val displayName: String, val patterns: List<String>, val lengths: List<Int>) {
    VISA("Visa", listOf("^4[0-9]{12}(?:[0-9]{3})?$"), listOf(13, 16)),
    MASTERCARD("Mastercard", listOf("^5[1-5][0-9]{14}$", "^2[2-7][0-9]{14}$"), listOf(16)),
    AMEX("American Express", listOf("^3[47][0-9]{13}$"), listOf(15)),
    DISCOVER("Discover", listOf("^6(?:011|5[0-9]{2})[0-9]{12}$"), listOf(16)),
    JCB("JCB", listOf("^(?:2131|1800|35\\d{3})\\d{11}$"), listOf(16)),
    DINERS_CLUB("Diners Club", listOf("^3(?:0[0-5]|[68][0-9])[0-9]{11}$"), listOf(14)),
    UNKNOWN("Unknown", emptyList(), emptyList());

    companion object {
        fun detectCardType(cardNumber: String): CardType {
            val cleanNumber = cardNumber.replace("\\s".toRegex(), "")
            
            for (cardType in values()) {
                if (cardType == UNKNOWN) continue
                
                if (cardType.lengths.contains(cleanNumber.length)) {
                    for (pattern in cardType.patterns) {
                        if (cleanNumber.matches(pattern.toRegex())) {
                            return cardType
                        }
                    }
                }
            }
            return UNKNOWN
        }

        fun isValidCardNumber(cardNumber: String): Boolean {
            val cleanNumber = cardNumber.replace("\\s".toRegex(), "")
            
            // Check if it's all digits
            if (!cleanNumber.matches("^\\d+$".toRegex())) {
                return false
            }
            
            // Check length
            if (cleanNumber.length < 13 || cleanNumber.length > 19) {
                return false
            }
            
            // Luhn algorithm validation
            return isValidLuhn(cleanNumber)
        }

        private fun isValidLuhn(cardNumber: String): Boolean {
            var sum = 0
            var alternate = false
            
            for (i in cardNumber.length - 1 downTo 0) {
                var n = cardNumber[i].toString().toInt()
                if (alternate) {
                    n *= 2
                    if (n > 9) {
                        n = (n % 10) + 1
                    }
                }
                sum += n
                alternate = !alternate
            }
            
            return sum % 10 == 0
        }

    }
} 