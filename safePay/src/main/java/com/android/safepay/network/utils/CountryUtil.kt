object CurrencyToCountryUtil {
    // Mapping of currency names to countries and their ISO 3166-1 alpha-2 country codes
    private val currencyToCountry: Map<String, Pair<String, String>> = mapOf(
        "USD" to Pair("United States", "US"),
        "EUR" to Pair("European Union", "EU"),
        "GBP" to Pair("United Kingdom", "GB"),
        "JPY" to Pair("Japan", "JP"),
        "INR" to Pair("India", "IN"),
        "CAD" to Pair("Canada", "CA"),
        "AUD" to Pair("Australia", "AU"),
        "CHF" to Pair("Switzerland", "CH"),
        "CNY" to Pair("China", "CN"),
        "KRW" to Pair("South Korea", "KR"),
        "PKR" to Pair("Pakistan", "PK"),
        "AED" to Pair("United Arab Emirates", "AE"),
        "SAR" to Pair("Saudi Arabia", "SA"),
        "KWD" to Pair("Kuwait", "KW")
    )

    // Function to get the country name and code from the currency ISO code with a default fallback to USA
    fun countryAndCode(currencyCode: String): Pair<String, String> {
        return currencyToCountry[currencyCode] ?: Pair("United States", "US") // Default to USA
    }

}

// Usage Example
/*fun main() {
    val dollarCountryInfo = CurrencyToCountryUtil.countryAndCode("Dollar")
    println("Currency: Dollar -> Country: ${dollarCountryInfo.first}, Code: ${dollarCountryInfo.second}") 
    // Output: United States, US

    val pakistaniRupeeCountryInfo = CurrencyToCountryUtil.countryAndCode("Pakistani Rupee")
    println("Currency: Pakistani Rupee -> Country: ${pakistaniRupeeCountryInfo.first}, Code: ${pakistaniRupeeCountryInfo.second}") 
    // Output: Pakistan, PK

    val unknownCurrencyCountryInfo = CurrencyToCountryUtil.countryAndCode("Unknown Currency")
    println("Currency: Unknown Currency -> Country: ${unknownCurrencyCountryInfo.first}, Code: ${unknownCurrencyCountryInfo.second}") 
    // Output: United States, US (default)
}*/
