# 01 Reusable Card Field Controller

Status: implemented.

## What changed
Added a standalone `SafepayCardCaptureController` in `safePay/src/main/java/com/android/safepay/network/ui/card/SafepayCardCaptureController.kt`.

The controller:
- accepts `environment`, `authToken`, and `tracker`
- works with an embeddable `SafepayCardFieldView`
- exposes `validate()`, `fetchValidity()`, `submit()`, `clear()`, `focus()`, and `blur()`
- returns a native `SafepayCardCaptureResult` instead of requiring host code to assemble the payload itself

## Supporting changes
Updated `safePay/src/main/java/com/android/safepay/network/ui/card/SafepayCardFieldView.kt` so the SDK can read full card details internally through `currentCardDetails(includeSensitiveData)` without sending PAN/CVV through the normal public change callback.

Added `PRODUCTION_BASE_URL` to `safePay/src/main/java/com/android/safepay/network/utils/Constants.kt` so the new controller can target all environments explicitly.

## Architectural outcome
The standalone card-capture flow no longer depends on `SafePayPaymentSheet`. Host code can place the card field directly in any layout and drive submission through the controller.
