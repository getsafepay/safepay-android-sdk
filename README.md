# safepay-android-sdk

## Building

The SDK depends on the Cardinal Mobile SDK, which is served from Cardinal Commerce's private Maven repository. Add your Cardinal credentials to `~/.gradle/gradle.properties` (outside this repository):

```properties
CARDINAL_USERNAME=your-username
CARDINAL_PASSWORD=your-token
```

In CI, provide them as the `CARDINAL_USERNAME` and `CARDINAL_PASSWORD` environment variables instead. Never commit credentials to this repository: it is public.
