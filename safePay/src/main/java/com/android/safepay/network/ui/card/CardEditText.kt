package com.android.safepay.network.ui.card

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.android.safepay.network.utils.CardType

class CardEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatEditText(context, attrs, defStyleAttr) {

    private var cardTypeImageView: ImageView? = null

    // Link an ImageView that will display the card type image
    fun setCardTypeImageView(imageView: ImageView) {
        this.cardTypeImageView = imageView
    }

    /*init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    detectCardType(it.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }*/

    // Detect card type and update ImageView
    private fun detectCardType(cardNumber: String) {
        val cardType = CardType.fromCardNumber(cardNumber)
        cardTypeImageView?.setImageDrawable(getDrawable(cardType.drawableResId))
    }

    // Helper function to get drawable
    private fun getDrawable(drawableResId: Int): Drawable? {
        return ContextCompat.getDrawable(context, drawableResId)
    }
}