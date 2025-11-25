package com.android.safepay.network.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import com.android.safepay.R
import com.android.safepay.databinding.LayoutFooterBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.regex.Pattern

object UiUtils
{
    fun isValidEmail(email: String): Boolean {
        // Basic email pattern matching using Android's built-in patterns
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }

        // Custom pattern to ensure the domain and TLD are valid
        val domainPattern = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )

        // Check if email matches both patterns
        return domainPattern.matcher(email).matches()
    }

    fun slideUpView(view: View) {
        view.apply {
            // Set initial state: the view is below its visible position
            translationY = view.height.toFloat()
            visibility = View.VISIBLE
            animate()
                .translationY(0f) // Move it to its natural position
                .setDuration(500)
                .setListener(null)
        }
    }

    fun slideInViewFromBottom(view: View) {
        // Set the view to be just off the bottom of the screen (its height)
        view.translationY = view.height.toFloat()

        // Animate the view sliding in from the bottom
        val animator = ObjectAnimator.ofFloat(view, "translationY", view.height.toFloat(), 0f)
        animator.duration = 1000  // Duration of the animation in milliseconds
        animator.interpolator = AccelerateInterpolator()  // Animation speed

        // Start the animation
        animator.start()

        // Ensure the view is visible when animation starts
        view.visibility = View.VISIBLE
    }

    fun fadeInView(view: View) {
    view.apply {
        alpha = 0f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .setDuration(100)
            .setListener(null)
    }
}


    fun setSpannable(footerBinding: LayoutFooterBinding?)
    {
        val spannable = SpannableString(footerBinding?.tvPoweredBy?.text)

        // Set color for "Powerd By"
        val powerColor = ForegroundColorSpan(
            ContextCompat.getColor(footerBinding?.tvPoweredBy?.context!!,
                R.color.footer_desc_color))
        spannable.setSpan(powerColor, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set color for "SafePay"
        val safepayColor = ForegroundColorSpan(
            ContextCompat.getColor(footerBinding.tvPoweredBy.context!!,
                R.color.blue))
        spannable.setSpan(safepayColor, 12, footerBinding.tvPoweredBy?.text?.length!!, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Apply the SpannableString to the TextView
        footerBinding.tvPoweredBy.text = spannable
    }


    fun enablePayButton(isEnable:Boolean,btnPay:Button?,mActivity: Activity)
    {
        if(isEnable)
        {
            btnPay?.isEnabled=true
            btnPay?.background=ContextCompat.getDrawable(mActivity,R.drawable.bg_button)
            btnPay?.setTextColor(ContextCompat.getColor(mActivity,R.color.white))
        }
        else
        {
            btnPay?.isEnabled=false
            btnPay?.background=ContextCompat.getDrawable(mActivity,R.drawable.bg_disable_btn)
            btnPay?.setTextColor(ContextCompat.getColor(mActivity, com.cardinalcommerce.cardinalmobilesdk.R.color.grey))
        }

    }

     fun togglePasswordVisibility(isPasswordVisible:Boolean,passwordEditText:EditText) {
         // Toggle visibility state
         val newInputType = if (isPasswordVisible) {
             passwordEditText.transformationMethod=null
             //InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD // Hide password
         } else {
             passwordEditText.transformationMethod=(PasswordTransformationMethod())
             //InputType.TYPE_CLASS_TEXT // Show password
         }

         //passwordEditText.inputType = newInputType
         // Move cursor to the end of the text
         passwordEditText.setSelection(passwordEditText.text.length)
    }

    fun expand(view: View, duration: Long = 300) {

        view.post {
            val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec((view.parent as View).width,
                View.MeasureSpec.EXACTLY)
            val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
            val targetHeight: Int = view.measuredHeight

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            view.layoutParams.height = 1
            view.visibility = View.VISIBLE
            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    view.layoutParams.height = if (interpolatedTime == 1f) ConstraintLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.duration = duration.toLong()
            view.startAnimation(a)
        }

    }

   fun collapse(view: View, duration: Long = 200) {
       view.post {
           val initialHeight = view.measuredHeight

           val animator = ValueAnimator.ofInt(initialHeight, 0).apply {
               this.duration = duration
               interpolator = AccelerateDecelerateInterpolator()
               addUpdateListener { animation ->
                   view.layoutParams.height = animation.animatedValue as Int
                   view.requestLayout()
               }
               addListener(onEnd = {
                   view.visibility = View.GONE
               })
           }
           animator.start()
       }

    }

    fun hideKeyboard(view: View?) {
        val imm = view?.context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setEditTextEnabled(parent: ViewGroup, isEnabled: Boolean) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            when (child) {
                is EditText -> child.isEnabled = isEnabled
            }
        }
    }

    fun showBlockerView(view:View?)
    {
        view?.visibility=View.VISIBLE
    }

    fun hideBlockerView(view: View?)
    {
        view?.visibility=View.GONE
    }

    fun showPaymentSuccessSheet(context: Context) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.layout_payment_success, null)
        dialog.setContentView(view)

        // Handle dismiss or button clicks in the bottom sheet
        /*val closeButton = view.findViewById<AppCompatImageView>(R.id.cross)
        closeButton?.setOnClickListener {
            dialog.dismiss()
        }*/

        dialog.show()
    }


}