package com.android.safepay.network.model.payment

import com.google.gson.annotations.SerializedName

class EnrollmentResponse(@SerializedName("data")
                          val data: Data,

                         @SerializedName("status")
                          val status: Status
)

data class Data(
    @SerializedName("tracker")
    val tracker: Tracker,

    @SerializedName("action")
    val action: Action
)

data class Tracker(
    @SerializedName("token")
    val token: String,

    @SerializedName("client")
    val client: String,

    @SerializedName("environment")
    val environment: String,

    @SerializedName("state")
    val state: String,

    @SerializedName("intent")
    val intent: String,

    @SerializedName("mode")
    val mode: String,

    @SerializedName("next_actions")
    val nextActions: NextActions,

    @SerializedName("purchase_totals")
    val purchaseTotals: PurchaseTotals
)

data class NextActions(
    @SerializedName("CYBERSOURCE")
    val cybersource: Cybersource
)

data class Cybersource(
    @SerializedName("kind")
    val kind: String,

    @SerializedName("request_id")
    val requestId: String
)

data class PurchaseTotals(
    @SerializedName("quote_amount")
    val quoteAmount: Amount,

    @SerializedName("base_amount")
    val baseAmount: Amount,

    @SerializedName("conversion_rate")
    val conversionRate: ConversionRate
)

data class Amount(
    @SerializedName("currency")
    val currency: String,

    @SerializedName("amount")
    val amount: Int
)

data class ConversionRate(
    @SerializedName("base_currency")
    val baseCurrency: String,

    @SerializedName("quote_currency")
    val quoteCurrency: String,

    @SerializedName("rate")
    val rate: Int
)

data class Action(
    @SerializedName("token")
    val token: String,

    @SerializedName("payment_method")
    val paymentMethod: PaymentMethod,

    @SerializedName("payer_authentication_enrollment")
    val payerAuthenticationEnrollment: PayerAuthenticationEnrollment
)

data class PaymentMethod(
    @SerializedName("token")
    val token: String,

    @SerializedName("expiration_month")
    val expirationMonth: String,

    @SerializedName("expiration_year")
    val expirationYear: String,

    @SerializedName("card_type_code")
    val cardTypeCode: String,

    @SerializedName("card_type")
    val cardType: String,

    @SerializedName("bin_number")
    val binNumber: String,

    @SerializedName("last_four")
    val lastFour: String
)

data class PayerAuthenticationEnrollment(
    @SerializedName("enrollment_status")
    val enrollmentStatus: String,

    @SerializedName("veres_enrolled")
    val veresEnrolled: String,

    @SerializedName("veres_enrolled_description")
    val veresEnrolledDescription: String,

    @SerializedName("authentication_status")
    val authenticationStatus:String,

    @SerializedName("authentication_transaction_id")
    val transactionId:String,

    @SerializedName("payload")
    val payload:String
)

data class Status(
    @SerializedName("errors")
    val errors: List<String>,

    @SerializedName("message")
    val message: String
)