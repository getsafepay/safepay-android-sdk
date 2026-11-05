package com.android.safepay.network.utils

import com.android.safepay.card.R

enum class CardType(val pattern: Regex, val drawableResId: Int) {
    VISA(Regex("^4[0-9]{6,}$"), R.drawable.sfpy_card_brand_visa),  // Visa card starts with 4
    MASTERCARD(Regex("^5[1-5][0-9]{5,}$"), R.drawable.sfpy_card_brand_mastercard),  // MasterCard starts with 51-55
   // AMEX(Regex("^3[47][0-9]{5,}$"), R.drawable.flag),  // Amex starts with 34 or 37
   // DISCOVER(Regex("^6(?:011|5[0-9]{2})[0-9]{3,}$"), R.drawable.flag),  // Discover starts with 6011 or 65
    UNKNOWN(Regex(""), R.drawable.sfpy_card_brand_unknown);  // Unknown card type

    companion object {
        fun fromCardNumber(cardNumber: String): CardType {
            return entries.firstOrNull { cardType ->
                cardNumber.matches(cardType.pattern)
            } ?: UNKNOWN
        }
    }
}