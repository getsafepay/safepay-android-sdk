package com.android.safepay.network.utils

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.android.safepay.R
import com.fredporciuncula.phonemoji.PhonemojiHelper
import com.fredporciuncula.phonemoji.PhonemojiTextInputEditText
import com.fredporciuncula.phonemoji.internal.TextDrawable
import com.google.android.material.textfield.TextInputLayout

class PhonemojiTextInputLayout : TextInputLayout {

    private var showFlag = true
    private var flagSize = 0f

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            with(context.theme.obtainStyledAttributes(attrs, R.styleable.PhonemojiTextInputLayout, 0, 0)) {
                try {
                    showFlag = getBoolean(R.styleable.PhonemojiTextInputLayout_phonemoji_showFlag, true)
                    val flagSizeFromAttr = getDimension(R.styleable.PhonemojiTextInputLayout_phonemoji_flagSize, 0f)
                    flagSize = flagSizeFromAttr.takeIf { it > 0 } ?: resources.getDimension(R.dimen.phonemoji_default_flag_size)
                } finally {
                    recycle()
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (showFlag && !isInEditMode) watchPhoneNumber()
    }

    private fun watchPhoneNumber() {
        val phonemojiEditText = editText as? PhonemojiTextInputEditText
        checkNotNull(phonemojiEditText) { "PhonemojiTextInputLayout requires a PhonemojiTextInputEditText child" }
        PhonemojiHelper.watchPhoneNumber(phonemojiEditText) { startIconDrawable = TextDrawable(it, flagSize) }
    }
}