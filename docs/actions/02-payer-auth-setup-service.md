# 02 Payer Auth Setup Service

Status: implemented for direct setup; tracker preflight/reset not yet implemented.

## What changed
Moved the `PAYER_AUTH_SETUP` request into `SafepayCardCaptureController.submit()`.

`submit()` now:
1. reads full card details from `SafepayCardFieldView` inside the SDK
2. validates completeness before building the raw-card payload
3. calls the backend `PAYER_AUTH_SETUP` action with `entry_mode = raw`
4. maps the backend response into `SafepayCardCaptureResult`

The Android implementation uses `RetrofitInstance.createRetrofit(...)` directly with the provided JWT auth token instead of relying on the older repository path that stored its own generated access token.

## Result shape
The controller now returns:
- `accessToken`
- `deviceDataCollectionURL`
- `paymentMethod`
- `cardinalJWT`

## Security outcome
Raw PAN/CVV stay inside native Android code. They are read from the card field only long enough to create the request payload and are not emitted through normal card-change events.

## Remaining gap
Tracker usability/reset was not implemented in this pass. The Android SDK did not yet expose a standalone reset flow, so this controller currently relies on the backend `PAYER_AUTH_SETUP` response to reject invalid tracker state.
