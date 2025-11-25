package com.android.safepay.network.model.tracker

import com.google.gson.annotations.SerializedName

data class TrackerFetchResponse(

    @SerializedName("ok") val ok: Boolean,
    @SerializedName("data") val data: Data

)

data class Data(
    @SerializedName("token") val token: String,
    @SerializedName("environment") val environment: String,
    @SerializedName("state") val state: String,
    @SerializedName("intent") val intent: String,
    @SerializedName("mode") val mode: String,
    @SerializedName("entry_mode") val entryMode: String,
    @SerializedName("client") val client: Client,
    @SerializedName("customer") val customer: Customer,
    @SerializedName("next_actions") val nextActions: NextActions,
    @SerializedName("purchase_totals") val purchaseTotals: PurchaseTotals,
    @SerializedName("events") val events: List<Event>,
    @SerializedName("attempts") val attempts: List<Attempt>,
    @SerializedName("location") val location: Location,
    @SerializedName("device") val device: Device,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("updated_at") val updatedAt: Timestamp
)

data class Client(
    @SerializedName("token") val token: String,
    @SerializedName("api_key") val apiKey: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
)

data class Customer(
    @SerializedName("token") val token: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String
)

data class NextActions(
    @SerializedName("CYBERSOURCE") val cybersource: ActionDetails,
    @SerializedName("MPGS") val mpgs: ActionDetails,
    @SerializedName("PAYFAST") val payfast: ActionDetails
)

data class ActionDetails(
    @SerializedName("kind") val kind: String,
    @SerializedName("request_id") val requestId: String? = null
)

data class PurchaseTotals(
    @SerializedName("quote_amount") val quoteAmount: Amount,
    @SerializedName("base_amount") val baseAmount: Amount,
    @SerializedName("conversion_rate") val conversionRate: ConversionRate
)

data class Amount(
    @SerializedName("currency") val currency: String,
    @SerializedName("amount") val amount: Int
)

data class ConversionRate(
    @SerializedName("base_currency") val baseCurrency: String,
    @SerializedName("quote_currency") val quoteCurrency: String,
    @SerializedName("rate") val rate: Int
)

data class Event(
    @SerializedName("intent") val intent: String,
    @SerializedName("type") val type: String,
    @SerializedName("intent_request_id") val intentRequestId: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("created_at") val createdAt: Timestamp
)
data class Attempt(
    @SerializedName("token") val token: String,
    @SerializedName("tracker") val tracker: String,
    @SerializedName("intent") val intent: String,
    @SerializedName("idempotency_key") val idempotencyKey: String,
    @SerializedName("kind") val kind: Int,
    @SerializedName("actions_performed") val actionsPerformed: List<ActionPerformed>,
    @SerializedName("payment_method") val paymentMethod: PaymentMethod,
    @SerializedName("billing") val billing: Billing,
    @SerializedName("enrollment") val enrollment: Enrollment,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("updated_at") val updatedAt: Timestamp,
    @SerializedName("mode") val mode: String,
    @SerializedName("entry_mode") val entryMode: String,
    @SerializedName("customer") val customer: Customer
)

data class ActionPerformed(
    @SerializedName("token") val token: String,
    @SerializedName("tracker") val tracker: String,
    @SerializedName("attempt") val attempt: String,
    @SerializedName("kind") val kind: String,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("updated_at") val updatedAt: Timestamp
)

data class PaymentMethod(
    @SerializedName("token") val token: String,
    @SerializedName("tracker") val tracker: String,
    @SerializedName("attempt") val attempt: String,
    @SerializedName("last_four") val lastFour: String,
    @SerializedName("kind") val kind: String,
    @SerializedName("scheme") val scheme: String,
    @SerializedName("bin") val bin: String,
    @SerializedName("expiration_month") val expirationMonth: String,
    @SerializedName("expiration_year") val expirationYear: String,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("updated_at") val updatedAt: Timestamp
)

data class Billing(
    @SerializedName("token") val token: String,
    @SerializedName("attempt") val attempt: String,
    @SerializedName("street_1") val street1: String,
    @SerializedName("city") val city: String,
    @SerializedName("postal_code") val postalCode: String,
    @SerializedName("country") val country: String,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("updated_at") val updatedAt: Timestamp
)

data class Enrollment(
    @SerializedName("token") val token: String,
    @SerializedName("attempt") val attempt: String,
    @SerializedName("cybersource_rid") val cybersourceRid: String,
    @SerializedName("specification_version") val specificationVersion: String,
    @SerializedName("veres_enrolled") val veresEnrolled: String,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("updated_at") val updatedAt: Timestamp,
    @SerializedName("authentication_status") val authenticationStatus: String
)

data class Location(
    @SerializedName("tracker") val tracker: String,
    @SerializedName("token") val token: String,
    @SerializedName("ip_address") val ipAddress: String,
    @SerializedName("city") val city: String,
    @SerializedName("country") val country: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("region") val region: String,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("updated_at") val updatedAt: Timestamp
)

data class Device(
    @SerializedName("tracker") val tracker: String,
    @SerializedName("token") val token: String,
    @SerializedName("user_agent") val userAgent: String,
    @SerializedName("entity") val entity: String,
    @SerializedName("browser") val browser: String,
    @SerializedName("browser_version") val browserVersion: String,
    @SerializedName("device_type") val deviceType: String,
    @SerializedName("platform_icon") val platformIcon: String,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("updated_at") val updatedAt: Timestamp
)

data class Timestamp(
    @SerializedName("seconds") val seconds: Long
)