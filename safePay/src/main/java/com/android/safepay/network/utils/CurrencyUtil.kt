object CurrencyUtil {
    private val currencySymbols: Map<String, String> = mapOf(
        "USD" to "$",    // Dollar
        "EUR" to "€",    // Euro
        "GBP" to "£",    // Pound
        "JPY" to "¥",    // Yen
        "INR" to "₹",    // Rupee
        "CAD" to "$",    // Canadian Dollar
        "AUD" to "$",    // Australian Dollar
        "CHF" to "CHF",  // Swiss Franc
        "CNY" to "¥",    // Yuan
        "KRW" to "₩",    // Korean Won
        "PKR" to "Rs",   // Pakistani Rupee
        "AED" to "AED",  // UAE Dirham
        "SAR" to "﷼",    // Saudi Riyal
        "KWD" to "KD"    // Kuwaiti Dinar
    )
    
    // Minor to major unit conversion factor
    private val minorToMajorFactors: Map<String, Int> = mapOf(
        "USD" to 100,   // Dollar
        "EUR" to 100,   // Euro
        "GBP" to 100,   // Pound
        "JPY" to 1,     // Yen (No minor units)
        "INR" to 100,   // Rupee
        "CAD" to 100,   // Canadian Dollar
        "AUD" to 100,   // Australian Dollar
        "CHF" to 100,   // Swiss Franc
        "CNY" to 100,   // Yuan
        "KRW" to 1,     // Korean Won (No minor units)
        "PKR" to 100,   // Pakistani Rupee
        "AED" to 100,   // UAE Dirham
        "SAR" to 100,   // Saudi Riyal
        "KWD" to 1000   // Kuwaiti Dinar (divided into 1000 fils)
    )
    
    // Function to get the currency symbol
    fun symbolFor(currencyName: String): String? {
        return currencySymbols[currencyName]
    }

    // Utility to convert minor to major denomination
    fun convertMinorToMajor(currencyName: String, minorAmount: Int): String? {
        val factor = minorToMajorFactors[currencyName]
        return if (factor != null) {
            String.format("%.2f", minorAmount.toDouble() / factor)
        } else {
            null
        }
    }
}

// Usage
/*fun main() {
    // Converting 150,000 minor units (cents) of Dollars to major
    val majorDollarAmount = CurrencyUtil.convertMinorToMajor("Dollar", 150000)
    println("150,000 cents in Dollar is $majorDollarAmount")  // Output: 1500.0

    // Converting 150,000 minor units (paise) of Pakistani Rupee to major
    val majorRupeeAmount = CurrencyUtil.convertMinorToMajor("Pakistani Rupee", 150000)
    println("150,000 paise in Pakistani Rupee is $majorRupeeAmount")  // Output: 1500.0

    // Converting 150,000 minor units (fils) of Kuwaiti Dinar to major
    val majorDinarAmount = CurrencyUtil.convertMinorToMajor("Kuwaiti Dinar", 150000)
    println("150,000 fils in Kuwaiti Dinar is $majorDinarAmount")  // Output: 150.0
}*/
