# SensorLab

A Kotlin + Jetpack Compose Android app for the COMP3074 sensor lab. The implementation follows the supplied lab requirements; device testing and submission remain your own steps.

## Open and run

1. In Android Studio, choose **Open** and select this `SensorLab` folder (the folder containing `settings.gradle.kts`).
2. Let Gradle sync finish. Use Android Studio's bundled JDK as the Gradle JDK (JDK 17 or newer compatible with Gradle 9.6). This project uses Android Gradle Plugin 9.4.1, Gradle 9.6.0, and its built-in Kotlin 2.2.10 with the matching Compose compiler plugin.
3. Install **Android SDK Platform 35** through SDK Manager if prompted. Minimum device version: Android 7.0 / API 24.
4. Connect an Android phone with USB debugging enabled and authorize this computer. Select it in the device dropdown and click **Run**.
5. Keep the phone still, then rotate it around each axis and watch the readings.

No runtime permissions or internet connection are needed by the app. Gradle may need internet access for the initial dependency download. `local.properties` is a machine-specific SDK path and is excluded from source control; Android Studio can recreate it.

Command line (PowerShell, with a compatible Java installation available):

```powershell
.\gradlew.bat assembleDebug lintDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Verification performed

`assembleDebug` and `lintDebug` passed on November 8, 2026. Lint reports 0 errors and 6 warnings about newer dependency/SDK versions and backup configuration compatibility. The app has not been launched on a phone or emulator: no device was connected during this build. Complete the device checklist below before submission; compilation does not establish hardware behavior or provide a device screenshot.

## How the code works

All app behavior is in `app/src/main/java/com/example/sensorlab/MainActivity.kt`:

- `onCreate` obtains `SensorManager` and looks up the ambient temperature sensor and gyroscope. A null result means that hardware is absent.
- `onResume` registers the available sensors at `SENSOR_DELAY_NORMAL`. A failed registration gets its own message.
- `onPause` unregisters the listener so the app stops receiving events in the background.
- `onSensorChanged` copies temperature or X/Y/Z angular velocity into Compose-observable state. The UI updates when the state changes.
- `SensorScreen` displays degrees Celsius and radians per second, with explicit waiting and unavailable states. It supports scrolling, system insets, and light/dark themes.
- The independent-practice extensions are a **Reset gyroscope** button and a reusable `SensorValueCard`. Reset sets the displayed axes to zero until the next event, which may arrive immediately.

The UI preview functions show live sample values, missing sensors, and waiting states. Sample values appear only in Android Studio previews; the installed app uses real sensor callbacks.

## Device test checklist

- [ ] App installs and launches without crashing.
- [ ] Both sensor cards are visible (scroll on a small screen).
- [ ] Ambient temperature shows a value in degrees Celsius, or the unavailable message.
- [ ] Gyroscope shows all three axes in rad/s, or the unavailable message.
- [ ] Keeping the device still produces near-zero angular velocities; rotating it changes the readings.
- [ ] Reset displays zero until the next event.
- [ ] Press Home, then return: sensor listening resumes and fresh readings arrive.
- [ ] Rotate the screen: app recreates safely and resumes receiving events.
- [ ] If possible, use an emulator or second device without the sensors to confirm the missing-hardware messages.
- [ ] Take a screenshot on your actual test device and fill in `SUBMISSION.md`.

A missing ambient temperature sensor is an expected valid outcome. Battery temperature is a different measurement and is never substituted. Preview screenshots do not establish physical sensor availability.

## Build references

- [Android Gradle Plugin / Gradle compatibility](https://developer.android.com/build/releases/about-agp)
- [Compose compiler Gradle plugin](https://developer.android.com/develop/ui/compose/compiler)

See `REFLECTIONS.md` for explanations to study and adapt if your instructor requests reflection answers.
