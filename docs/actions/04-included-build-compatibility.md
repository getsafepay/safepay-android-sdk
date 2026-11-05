# 04 Included Build Compatibility

Status: implemented.

## What changed
Updated `safePay/build.gradle.kts` so the Android SDK can be consumed as an included build inside the React Native example app.

The module now:
- uses plain `id("com.android.library")` and `id("org.jetbrains.kotlin.android")` plugin declarations instead of version-catalog aliases
- targets Java 17
- targets Kotlin JVM 17

## Why this was necessary
When the SDK is pulled into the example app through `android/settings.gradle`, the host app already provides the Android and Kotlin plugins on the classpath. The version-catalog alias form caused Gradle to treat the SDK as if it were requesting its own plugin version, which then conflicted with the host build.

The older Java 8 / Kotlin 1.8 target also drifted away from the host React Native app, which is already building with Java 17.
