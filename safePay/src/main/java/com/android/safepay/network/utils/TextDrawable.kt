package com.android.safepay.network.utils

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import com.fredporciuncula.phonemoji.PhonemojiHelper

class TextDrawable(private val text: String, private val size: Float, private val backgroundColor: Int) : Drawable() {

    // Paint for text
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = PhonemojiHelper.EMOJI_COLOR
        textSize = size
        textAlign = Paint.Align.CENTER
       // style = Paint.Style.FILL
    }

    // Paint for background
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = backgroundColor
       // style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        // Draw the background first
        canvas.drawRect(bounds, backgroundPaint)

        // Draw the text on top of the background
        canvas.drawText(
            text,
            0,
            text.length,
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat() - ((textPaint.descent() + textPaint.ascent()) / 2),
            textPaint
        )
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
        backgroundPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        textPaint.colorFilter = colorFilter
        backgroundPaint.colorFilter = colorFilter
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("This method is no longer used in graphics optimizations")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
