# VIDORA AI v1

Android MVP for an AI video creation app.

## Included
- Home screen
- Video prompt editor
- Style selection
- 9:16 format
- Generation progress demo
- Video library
- Credits/subscription placeholder
- Backend endpoint placeholder via `BuildConfig.VIDORA_API_BASE_URL`
- GitHub Actions workflow that builds a debug APK

## Current status
The generation flow is still DEMO mode. No provider API key is embedded in the Android app. The next stage is a secure backend that receives a prompt and returns a generated video URL.

## Build
Open the project in Android Studio and sync Gradle. The project targets Android API 37 and uses Android Gradle Plugin 9.3.0.

For a GitHub build, push the repository and open Actions -> Android build. The resulting `vidora-debug-apk` artifact can be downloaded from the workflow run.
