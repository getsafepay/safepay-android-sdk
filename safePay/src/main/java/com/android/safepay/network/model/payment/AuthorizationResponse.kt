package com.android.safepay.network.model.payment

import android.health.connect.datatypes.Metadata
import com.google.gson.annotations.SerializedName

class AuthorizationResponse(

    @SerializedName("data")
    val data: Data,

    @SerializedName("status")
    val status: Status)
{
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
        @SerializedName("customer")
        val customer: String,
        @SerializedName("next_actions")
        val next_actions: NextActions,
        @SerializedName("purchase_totals")
        val purchase_totals: PurchaseTotals
    )

    data class NextActions(
        @SerializedName("CYBERSOURCE")
        val CYBERSOURCE: Cybersource
    )

    data class Cybersource(
        @SerializedName("kind")
        val kind: String,
        @SerializedName("request_id")
        val request_id: String
    )

    data class PurchaseTotals(
        @SerializedName("quote_amount")
        val quote_amount: Amount,
        @SerializedName("base_amount")
        val base_amount: Amount,
        @SerializedName("conversion_rate")
        val conversion_rate: ConversionRate
    )

    data class Amount(
        @SerializedName("currency")
        val currency: String,
        @SerializedName("amount")
        val amount: Int
    )

    data class ConversionRate(
        @SerializedName("base_currency")
        val base_currency: String,
        @SerializedName("quote_currency")
        val quote_currency: String,
        @SerializedName("rate")
        val rate: Int
    )

    data class Action(
        @SerializedName("token")
        val token: String,
        @SerializedName("payment_method")
        val payment_method: PaymentMethod,
        @SerializedName("payer_authentication_setup")
        val payer_authentication_setup: PayerAuthenticationSetup
    )

    data class PaymentMethod(
        @SerializedName("token")
        val token: String,
        @SerializedName("expiration_month")
        val expiration_month: String,
        @SerializedName("expiration_year")
        val expiration_year: String,
        @SerializedName("card_type_code")
        val card_type_code: String,
        @SerializedName("card_type")
        val card_type: String,
        @SerializedName("bin_number")
        val bin_number: String,
        @SerializedName("last_four")
        val last_four: String
    )

    data class PayerAuthenticationSetup(
        @SerializedName("access_token")
        val access_token: String,
        @SerializedName("device_data_collection_url")
        val device_data_collection_url: String,

        @SerializedName("cardinal_jwt")
        val cardinalJWT:String
    )

    data class Status(
        @SerializedName("errors")
        val errors: List<String>,
        @SerializedName("message")
        val message: String
    )
}