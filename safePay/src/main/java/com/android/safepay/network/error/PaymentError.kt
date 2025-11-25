package com.android.safepay.network.error

class PaymentError(
    var code: String? = null,
    var msg: String
) : Exception(msg) {

    // Function to get a formatted error description
    fun formattedError(): String {
        return "Error: ${code ?: ""} $message"
    }

    companion object {
        fun generalPaymentError(): PaymentError {
            return PaymentError(
                code = null,
                msg = "An unexpected error occurred while processing your payment. Please try again or contact support for assistance."
            )
        }

        fun invalidConfiguration(): PaymentError {
            return PaymentError(
                code = null,
                msg = "Looks like required parameters are missing, please check tracker configurations and try again."
            )
        }

        fun timeBasedTokenExpired(): PaymentError {
            return PaymentError(
                code = null,
                msg = "Looks like a required parameter \"Timebased token\" is expired or invalid, please check tracker configurations and try again."
            )
        }

        fun cardError(): PaymentError {
            return PaymentError(
                code = null,
                msg = "There was a problem authenticating your payment. Please use a different card."
            )
        }

        fun invalidTrackerState(): PaymentError {
            return PaymentError(
                code = "PT-1001",
                msg = "It appears that your payment tracker has already been started or completed. Please contact support."
            )
        }
    }
}