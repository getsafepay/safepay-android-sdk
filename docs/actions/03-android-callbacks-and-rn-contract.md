# 03 Android Callbacks And RN Contract

Status: partially implemented in the Android SDK.

## What the SDK exposes now
The Android SDK now exposes a stable imperative surface through `SafepayCardCaptureController`:
- `validate`
- `fetchValidity`
- `clear`
- `focus`
- `blur`
- `submit`

`submit()` returns a native `SafepayCardCaptureResult` that is shaped for the RN bridge to convert into:
- `onValidated`
- `onError`
- `onProceedToAuthentication`

## What is still left for the RN bridge
The RN bridge still needs to:
- hold a controller instance per mounted native card-capture view
- translate native `submit()` results into React Native events
- expose the same command surface on the JS component

## Scope boundary that stayed the same
This Android work still stops at payer-auth setup. It does not run DDC, present challenge UI, or finalize authorization.

## Verification
The standalone Android implementation compiled successfully with:
- `./gradlew :safePay:compileDebugKotlin`
