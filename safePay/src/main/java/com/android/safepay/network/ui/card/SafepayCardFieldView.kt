package com.android.safepay.network.ui.card

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import com.android.safepay.R
import com.android.safepay.network.utils.CardType
import java.util.Calendar

class SafepayCardFieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    data class CardFieldDetails(
        val brand: String?,
        val last4: String?,
        val expiryMonth: Int?,
        val expiryYear: Int?,
        val complete: Boolean,
        val validNumber: String,
        val validExpiryDate: String,
        val validCvc: String,
        val number: String?,
        val cvc: String?,
    )

    enum class FocusField {
        CardNumber,
        ExpiryDate,
        Cvc,
    }

    interface OnCardChangeListener {
        fun onCardChange(details: CardFieldDetails)
    }

    interface OnFocusChangeListener {
        fun onFocusChange(focusedField: FocusField?)
    }

    private val cardNumberEditText: AppCompatEditText
    private val expiryEditText: AppCompatEditText
    private val cvcEditText: AppCompatEditText
    private val cardBrandView: ImageView

    private var onCardChangeListener: OnCardChangeListener? = null
    private var onFocusChangeListener: OnFocusChangeListener? = null

    var dangerouslyGetFullCardDetails: Boolean = false

    private var isFormattingCardNumber = false
    private var isFormattingExpiry = false
    private var cachedExpiryMonth: Int? = null
    private var cachedExpiryYear: Int? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_safepay_card_field, this, true)
        cardNumberEditText = findViewById(R.id.sfpyCardNumber)
        expiryEditText = findViewById(R.id.sfpyExpiry)
        cvcEditText = findViewById(R.id.sfpyCvc)
        cardBrandView = findViewById(R.id.sfpyCardBrand)

        setupListeners()
    }

    fun setOnCardChangeListener(listener: OnCardChangeListener?) {
        onCardChangeListener = listener
    }

    fun setOnFocusChangeListener(listener: OnFocusChangeListener?) {
        onFocusChangeListener = listener
    }

    fun clear() {
        cardNumberEditText.text?.clear()
        expiryEditText.text?.clear()
        cvcEditText.text?.clear()
        cachedExpiryMonth = null
        cachedExpiryYear = null
        emitChange()
    }

    fun focus() {
        cardNumberEditText.requestFocus()
    }

    fun blur() {
        cardNumberEditText.clearFocus()
        expiryEditText.clearFocus()
        cvcEditText.clearFocus()
        onFocusChangeListener?.onFocusChange(null)
    }

    fun setDisabled(disabled: Boolean) {
        cardNumberEditText.isEnabled = !disabled
        expiryEditText.isEnabled = !disabled
        cvcEditText.isEnabled = !disabled
    }

    private fun setupListeners() {
        cardNumberEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onFocusChangeListener?.onFocusChange(FocusField.CardNumber)
            } else {
                emitBlurIfNeeded()
            }
        }

        expiryEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onFocusChangeListener?.onFocusChange(FocusField.ExpiryDate)
            } else {
                emitBlurIfNeeded()
            }
        }

        cvcEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onFocusChangeListener?.onFocusChange(FocusField.Cvc)
            } else {
                emitBlurIfNeeded()
            }
        }

        cardNumberEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isFormattingCardNumber) return
                    isFormattingCardNumber = true

                    val raw = s?.toString()?.replace(" ", "") ?: ""
                    val formatted = raw.chunked(4).joinToString(" ")
                    cardNumberEditText.setText(formatted)
                    cardNumberEditText.setSelection(formatted.length)

                    val cardType = CardType.fromCardNumber(raw)
                    cardBrandView.setImageResource(cardType.drawableResId)

                    emitChange()
                    isFormattingCardNumber = false
                }
            },
        )

        expiryEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isFormattingExpiry) return
                    isFormattingExpiry = true

                    val raw = s?.toString()?.replace("/", "") ?: ""
                    val trimmed = if (raw.length > 4) raw.substring(0, 4) else raw
                    val formatted =
                        if (trimmed.length >= 3) {
                            trimmed.substring(0, 2) + "/" + trimmed.substring(2)
                        } else {
                            trimmed
                        }

                    expiryEditText.setText(formatted)
                    expiryEditText.setSelection(formatted.length)

                    parseExpiry(formatted)
                    emitChange()
                    isFormattingExpiry = false
                }
            },
        )

        cvcEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    emitChange()
                }
            },
        )
    }

    private fun emitBlurIfNeeded() {
        if (!(cardNumberEditText.hasFocus() || expiryEditText.hasFocus() || cvcEditText.hasFocus())) {
            onFocusChangeListener?.onFocusChange(null)
        }
    }

    private fun emitChange() {
        onCardChangeListener?.onCardChange(buildDetails())
    }

    fun currentCardDetails(includeSensitiveData: Boolean = false): CardFieldDetails {
        return buildDetails(includeSensitiveData)
    }

    private fun buildDetails(includeSensitiveData: Boolean = dangerouslyGetFullCardDetails): CardFieldDetails {
        val numberText = cardNumberEditText.text?.toString().orEmpty()
        val cleanedNumber = numberText.replace(" ", "")
        val last4 = if (cleanedNumber.length >= 4) cleanedNumber.takeLast(4) else null

        val cardType = CardType.fromCardNumber(cleanedNumber)
        val brand = when (cardType) {
            CardType.VISA -> "Visa"
            CardType.MASTERCARD -> "Mastercard"
            else -> "Unknown"
        }

        val validNumber = validationState(cleanedNumber, isCardNumberValid(cleanedNumber))
        val validExpiry = validationState(expiryEditText.text?.toString().orEmpty(), isExpiryValid())
        val validCvc = validationState(cvcEditText.text?.toString().orEmpty(), isCvcValid())

        val complete = validNumber == "Valid" && validExpiry == "Valid" && validCvc == "Valid"

        val number = if (includeSensitiveData) cleanedNumber else null
        val cvc = if (includeSensitiveData) cvcEditText.text?.toString() else null

        return CardFieldDetails(
            brand = brand,
            last4 = last4,
            expiryMonth = cachedExpiryMonth,
            expiryYear = cachedExpiryYear,
            complete = complete,
            validNumber = validNumber,
            validExpiryDate = validExpiry,
            validCvc = validCvc,
            number = number,
            cvc = cvc,
        )
    }

    private fun validationState(text: String, isValid: Boolean): String {
        if (text.isEmpty()) {
            return "Incomplete"
        }
        return if (isValid) "Valid" else "Invalid"
    }

    private fun isCardNumberValid(number: String): Boolean {
        if (number.length < 13 || number.length > 19) return false
        return isValidLuhn(number)
    }

    private fun isCvcValid(): Boolean {
        val cvc = cvcEditText.text?.toString().orEmpty()
        return cvc.length in 3..4
    }

    private fun isExpiryValid(): Boolean {
        val month = cachedExpiryMonth ?: return false
        val year = cachedExpiryYear ?: return false

        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)
        val currentMonth = now.get(Calendar.MONTH) + 1

        return year > currentYear || (year == currentYear && month >= currentMonth)
    }

    private fun parseExpiry(formatted: String) {
        val parts = formatted.split("/")
        if (parts.size != 2) {
            cachedExpiryMonth = null
            cachedExpiryYear = null
            return
        }

        val month = parts[0].toIntOrNull()
        val yearPart = parts[1].toIntOrNull()
        if (month == null || month !in 1..12 || yearPart == null) {
            cachedExpiryMonth = null
            cachedExpiryYear = null
            return
        }

        val fullYear = convertToFourDigitYear(yearPart)
        cachedExpiryMonth = month
        cachedExpiryYear = fullYear
    }

    private fun convertToFourDigitYear(twoDigitYear: Int): Int {
        if (twoDigitYear >= 100) return twoDigitYear
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentCentury = (currentYear / 100) * 100
        val currentTwoDigit = currentYear % 100
        return if (twoDigitYear < currentTwoDigit) {
            currentCentury + 100 + twoDigitYear
        } else {
            currentCentury + twoDigitYear
        }
    }

    private fun isValidLuhn(number: String): Boolean {
        var sum = 0
        var alternate = false
        for (i in number.length - 1 downTo 0) {
            var n = number[i].digitToIntOrNull() ?: return false
            if (alternate) {
                n *= 2
                if (n > 9) n -= 9
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }
}
