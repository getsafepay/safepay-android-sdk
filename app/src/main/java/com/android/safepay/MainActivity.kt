package com.android.safepay

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.safepay.network.error.PaymentError
import com.android.safepay.network.model.payment.PaymentResult
import com.android.safepay.network.ui.SafePayConfiguration
import com.android.safepay.network.ui.SafePayPaymentSheet

class MainActivity : AppCompatActivity() {

    lateinit var payButton:AppCompatButton
    lateinit var etTimeBasetoken:AppCompatEditText
    lateinit var etTracker:AppCompatEditText
    lateinit var etAddress:AppCompatEditText
    lateinit var paymentCallback:AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        payButton = findViewById(R.id.PayButton)
        etTimeBasetoken=findViewById(R.id.etTimeBase)
        etTracker=findViewById(R.id.etTracker)
        etAddress=findViewById(R.id.etAddress)
        paymentCallback=findViewById(R.id.paymentCallback)

        etAddress.setText("")//address_5ce54f87-a823-4da6-8990-7b26d048ce00
        etTracker.setText("track_9022cb13-a1a0-4ecb-8d7e-687213a9fd06")
        etTimeBasetoken.setText("mFvhRgITVcHoxrWXjJGq9my6wlEjwuxjIBqMpcoCnJckj7WHTboviV3c_tDn2UhOLqIdPwb9gg==")

        payButton.setOnClickListener {
            val config = SafePayConfiguration(trackerToken = etTracker.text.toString(), timeBaseToken = etTimeBasetoken.text.toString(),
                etAddress.text.toString())
            val paymentSheet = SafePayPaymentSheet(configuration = config)

            paymentSheet.present(activity = this) { paymentResult ->
                when (paymentResult) {
                    is PaymentResult.Completed -> paymentCallback.text = "Payment Completed!"
                    is PaymentResult.Canceled -> paymentCallback.text = "Payment Cancelled!"
                    is PaymentResult.Failed -> paymentCallback.text = paymentResult.error.msg
                }
            }
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
    fun Activity.displayAlert(title: String, message: String? = null) {
        // Replace this with actual alert logic
        println("$title: ${message ?: ""}")
    }
}