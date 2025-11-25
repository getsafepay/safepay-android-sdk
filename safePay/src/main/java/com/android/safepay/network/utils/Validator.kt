package com.android.safepay.network.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.android.safepay.R
import com.fredporciuncula.phonemoji.PhonemojiTextInputEditText
import com.hbb20.CountryCodePicker
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

class Validator(private val context: Context) {

    private val nameRegex = "^[A-Za-z\\s]{2,}$".toRegex()
    private val addressRegex = "^[A-Za-z0-9\\s,.#-]{5,}$".toRegex()

    private val errorBorder: Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.bg_error)!!
    }

    private val defaultBorder: Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.bg_edittext)!!
    }

    private val focusedBorder: Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.bg_focused)!!
    }

    fun validatePhone(phone: EditText,phoneLayout:LinearLayout,countryCode: String): Boolean {
        return if (!isPhoneValid(phone,countryCode)) {
            phoneLayout.background = errorBorder
            false
        } else {
            phoneLayout.background = focusedBorder
            true
        }
    }


    fun validatePassword(password: EditText,passwordFieldLayout:FrameLayout): Boolean {
        return if (isEmpty(password) || !validatePassword(password.text.toString())) {
            passwordFieldLayout.background = errorBorder
            false
        } else {
            passwordFieldLayout.background = focusedBorder
            true
        }
    }



    fun validatePassword(password: String): Boolean {
        return password.length in 8..44
    }

    fun validationCVV(cvv: EditText):Boolean
    {
        if (isEmpty(cvv) || !isCvcValid(cvv)) {
            cvv.background = errorBorder
            return false
        } else {
            cvv.background = focusedBorder
            return true
        }
    }

    fun validationCard(cardNumber: EditText):Boolean
    {
        if (isEmpty(cardNumber) || !isCardNumberValid(cardNumber)) {
            cardNumber.background = errorBorder
            return false
        } else {
            cardNumber.background = focusedBorder
            return true
        }
    }

    fun validationName(name: EditText):Boolean
    {
        if (isEmpty(name) || !validateName(name.text.toString())) {
            name.background = errorBorder
            return false
        } else {
            name.background = focusedBorder
            return true
        }
    }

    fun validateName(name: String): Boolean {
        return nameRegex.matches(name)
    }

    fun validationPostalCode(postalCode: EditText):Boolean
    {
        if (isEmpty(postalCode) || !validateAddress(postalCode.text.toString())) {
            postalCode.background = errorBorder
            return false
        } else {
            postalCode.background = focusedBorder
            return true
        }
    }

    fun validateAddress(address: String): Boolean {
        return addressRegex.matches(address)
    }

    fun validationStreetAddress(address: EditText):Boolean
    {
        if (isEmpty(address) || !validateAddress(address.text.toString())) {
            address.background = errorBorder
            return false
        } else {
            address.background = focusedBorder
            return true
        }
    }

    fun validate(
        phone: EditText,
        country: String,
        street: EditText,
        city: EditText,
        cardNumber: EditText,
        expiry: EditText,
        cvc: EditText
    ): Boolean {
        var isValid = true

        // Phone
        if (isEmpty(phone) || !isPhoneValid(phone)) {
            //phone.background = errorBorder
            isValid = false
        } else {
            //phone.background = defaultBorder
        }

        // Country
       if (country.isEmpty()) {
           // country.background = errorBorder
            isValid = false
        } else {
           // country.background = defaultBorder
        }

        // Street
        if (isEmpty(street)) {
            //street.background = errorBorder
            isValid = false
        } else {
            //street.background = defaultBorder
        }

        // City
        if (isEmpty(city)) {
            //city.background = errorBorder
            isValid = false
        } else {
           // city.background = defaultBorder
        }

        // State
        /*if (isEmpty(state)) {
            state.background = errorBorder
            isValid = false
        } else {
            state.background = defaultBorder
        }*/

        // Card Number
        if (isEmpty(cardNumber) || !isCardNumberValid(cardNumber)) {
            //cardNumber.background = errorBorder
            isValid = false
        } else {
            //cardNumber.background = defaultBorder
        }

        // Expiry
        if (isEmpty(expiry) || !isExpiryValid(expiry)) {
            //expiry.background = errorBorder
            isValid = false
        } else {
            //expiry.background = defaultBorder
        }

        // CVC
        if (isEmpty(cvc) || !isCvcValid(cvc)) {
            //cvc.background = errorBorder
            isValid = false
        } else {
            //cvc.background = defaultBorder
        }

        return isValid
    }

    private fun isEmpty(editText: EditText): Boolean {
        return TextUtils.isEmpty(editText.text.toString().trim())
    }

    private fun isPhoneValid(editText: EditText): Boolean {
        val phone = editText.text.toString().trim()
        // Add phone number validation logic
        return phone.length in 10..15 // Example: valid if length is between 10 and 15
    }

    private fun isPhoneValid(editText: EditText, countryCode: String): Boolean {
        val phone = editText.text.toString().trim()
        val phoneUtil = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance()
        return try {
            val number = phoneUtil.parse(phone, countryCode)
            phoneUtil.isValidNumber(number)
        } catch (e: com.google.i18n.phonenumbers.NumberParseException) {
            false
        }
    }

    private fun isCardNumberValid(editText: EditText): Boolean {
        val cardNumber = editText.text.toString().trim()
        // Add card number validation logic
        return cardNumber.length in 13..19 // Example: valid if length is between 13 and 19
    }

    private fun isExpiryValid(editText: EditText): Boolean {
        val expiry = editText.text.toString().trim()
        // Example validation: MM/YYYY
        return expiry.matches(Regex("^(0[1-9]|1[0-2])/([0-9]{4})$"))
    }

    private fun isCvcValid(editText: EditText): Boolean {
        val cvc = editText.text.toString().trim()
        // Add CVC validation logic
        return cvc.length in 3..4 // Example: valid if length is between 3 and 4
    }
}
