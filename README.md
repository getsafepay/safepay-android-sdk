# safepay-android-sdk

## Building

The SDK depends on the Cardinal Mobile SDK, which is served from Cardinal Commerce's private Maven repository. Add your Cardinal credentials to `~/.gradle/gradle.properties` (outside this repository):

```properties
CARDINAL_USERNAME=your-username
CARDINAL_PASSWORD=your-token
```

In CI, provide them as the `CARDINAL_USERNAME` and `CARDINAL_PASSWORD` environment variables instead. Never commit credentials to this repository: it is public.

## Modules

| Module | Contents | Needs Cardinal credentials |
|---|---|---|
| `safePay` | The full payment sheet, including 3-D Secure through the Cardinal Mobile SDK. Includes `safePayCard`. | Yes |
| `safePayCard` | Card capture only: the native card input and `SafepayCardCaptureController`. | No |

## Card capture

`SafepayCardCaptureController` provides a native card number, expiry and CVC input (`cardFieldView`) and sends the card to Safepay when you call `submit()`. Card data never leaves the SDK except in that request.

### Setup

Requires `minSdk` 24 or later and Java 17. Include the `safePayCard` module from a checkout of this repository (for example a git submodule) in your `settings.gradle.kts`, adjusting the path:

```kotlin
include(":safePayCard")
project(":safePayCard").projectDir = file("../safepay-android-sdk/safePayCard")
```

Then depend on it from your app module:

```kotlin
dependencies {
    implementation(project(":safePayCard"))
}
```

### Usage

You need a payment tracker and a time-based token (TBT) for it, both created on your server. Use the environment the tracker was created in.

```kotlin
import com.android.safepay.network.error.PaymentError
import com.android.safepay.network.ui.card.SafepayCardCaptureConfiguration
import com.android.safepay.network.ui.card.SafepayCardCaptureController
import com.android.safepay.network.ui.card.SafepayCardCaptureEnvironment
import com.android.safepay.network.ui.card.SafepayCardFieldView

val cardCapture = SafepayCardCaptureController(
    context = this,
    configuration = SafepayCardCaptureConfiguration(
        environment = SafepayCardCaptureEnvironment.SANDBOX, // DEVELOPMENT, SANDBOX or PRODUCTION
        authToken = tbt,
        tracker = tracker,
    ),
)
container.addView(cardCapture.cardFieldView)

cardCapture.cardFieldView.setOnCardChangeListener(object : SafepayCardFieldView.OnCardChangeListener {
    override fun onCardChange(details: SafepayCardFieldView.CardFieldDetails) {
        payButton.isEnabled = details.complete
    }
})

payButton.setOnClickListener {
    lifecycleScope.launch {
        try {
            val result = cardCapture.submit()
            // result.paymentMethod: the saved card (token, brand, last four, ...)
            // result.accessToken and result.deviceDataCollectionURL: inputs to 3-D Secure
        } catch (error: PaymentError) {
            Log.w("Checkout", error.formattedError())
        }
    }
}
```

To place the field in an XML layout instead, add a `com.android.safepay.network.ui.card.SafepayCardFieldView` and pass it to the controller: `SafepayCardCaptureController(configuration, cardFieldView)`.

The controller also has `validate()`, `fetchValidity()`, `focus()`, `blur()` and `clear()`.

`CardFieldDetails` gives you the brand, last four digits, expiry and validation state. The card number and CVC are never exposed to your app, which keeps raw card data out of it.

For React Native, use [`@sfpy/react-native`](https://github.com/getsafepay/sfpy-react-native), which wraps this controller.
