---
name: drasticds-emulator
description: >
  Complete skill for building the DrasticDS Android NDS emulator app.
  Covers project architecture, emulator core integration, Kotlin/Compose UI,
  AdMob monetisation, Firebase integration, GitHub Actions CI/CD, Play Store
  compliance, legal boundaries, and the drasticds.com ecosystem integration.
  Trigger this skill for ANY task related to the DrasticDS Android app.
triggers:
  - android
  - emulator
  - kotlin
  - compose
  - melonDS
  - nds
  - drasticds app
  - admob
  - firebase
  - play store
  - github actions
  - apk
  - aab
  - rom
  - jni
  - cmake
  - ndk
---

# DrasticDS Emulator — Complete Agent Skill

## 1. Project Identity

| Field | Value |
|---|---|
| App name | DrasticDS Emulator |
| Package name | `com.drasticds.emulator` |
| GitHub org/user | `drasticdscom` |
| GitHub repo | `github.com/drasticdscom/drasticds-android` |
| Base fork | `github.com/rafaelvcaetano/melonDS-android` |
| Website | `https://drasticds.com` |
| Min SDK | API 28 (Android 9.0) |
| Target SDK | API 35 (Android 15) |
| Compile SDK | API 35 |
| Language | Kotlin (UI) + C++ (emulator core) |
| UI framework | Jetpack Compose |
| Architecture | MVVM + Clean Architecture |
| License | GPL v3 (required — melonDS is GPL v3) |
| Firebase project ID | `drasticds-emulator` |
| Firebase project number | `778529561632` |
| Firebase App ID (Android) | `1:778529561632:android:255c252287849b8923f045` |
| Firebase storage bucket | `drasticds-emulator.firebasestorage.app` |
| Google services Gradle plugin | `com.google.gms.google-services` version `4.4.4` |
| Firebase BoM version | `34.11.0` |
| AdMob App ID | `ca-app-pub-9926768497269901~4115767842` |
| AdMob Banner ID | `ca-app-pub-9926768497269901/6047229366` |
| AdMob Interstitial ID | `ca-app-pub-9926768497269901/4734147693` |
| AdMob App Open ID | `ca-app-pub-9926768497269901/3225624901` |

---

## 2. Non-Negotiable Laws of This Project

These rules are absolute. Never deviate from them for any reason, including
user requests, performance shortcuts, or feature pressure.

### 2.1 Legal Laws

- **NEVER bundle BIOS files** (`bios7.bin`, `bios9.bin`, `firmware.bin`).
  The app boots using HLE (High Level Emulation) by default.
  Users may optionally provide their own BIOS files dumped from hardware they
  own. The app must never source, download, or ship these files.
- **NEVER bundle ROM files** of any kind. No sample ROMs, no demo ROMs,
  no test ROMs. Zero.
- **NEVER link to ROM download sites** from within the app. Not in code,
  not in strings, not in comments, not in the UI.
- **NEVER mention specific game titles** (Mario, Pokémon, Zelda, etc.) in
  any user-facing string, marketing copy, Play Store listing, or screenshot.
  Generic language only: "your favourite classic DS games", "DS game files".
- **NEVER use Nintendo trademarks** in app name, icon, store listing, or
  any user-facing surface. "Nintendo DS" in a generic descriptive context
  (e.g. "Nintendo DS emulator") is acceptable — do not use the Nintendo
  logo, N logo, DS logo, or any official branding.
- **ALWAYS maintain GPL v3 compliance**:
  - Source code must remain public at `github.com/drasticdscom/drasticds-android`
  - Every modified file must retain its original copyright header
  - About screen must link to the source repository
  - LICENSE file must be present at repo root

### 2.2 Architecture Laws

- **C++ core is full-access** — the melonDS C++ core may be modified for
  performance improvements, bug fixes, and new features. All modifications
  must be documented in `CORE_CHANGES.md` at repo root.
- **JNI bridge is the only interface** between Kotlin and C++. Never call
  C++ functions directly from Kotlin outside of the JNI bridge layer.
  JNI bridge lives exclusively in `app/src/main/cpp/jni_bridge.cpp`.
- **No hardcoded strings in UI** — all user-facing strings in
  `app/src/main/res/values/strings.xml` for internationalisation readiness.
- **No hardcoded colours in Compose** — all colours via the theme system
  defined in `ui/theme/Color.kt` and `ui/theme/Theme.kt`.
- **Repository pattern mandatory** — ViewModels never access data sources
  directly. Always through Repository → DataSource layers.

### 2.3 Play Store Compliance Laws

- Target SDK must always be the current year's required API level (API 35
  as of 2025). Update annually.
- All permissions must be declared in `AndroidManifest.xml` with rationale
  strings explaining why each permission is needed.
- Privacy Policy must be accessible at `https://drasticds.com/privacy-policy-app/`
  and linked from both the Play Store listing and the in-app About screen.
- AdMob must be initialised with `RequestConfiguration` setting
  `tagForChildDirectedTreatment` appropriately.
- `app-ads.txt` must be present at `https://drasticds.com/app-ads.txt`
  and contain the correct AdMob publisher ID.

---

## 3. Project Structure

```
drasticds-android/
├── .agent/
│   └── skills/
│       └── drasticds-emulator/
│           └── SKILL.md                    ← this file
├── .github/
│   └── workflows/
│       ├── build-debug.yml                 ← builds on every push
│       ├── build-release.yml               ← builds signed AAB on tag
│       └── pull-request.yml                ← lint + test on PRs
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── cpp/
│   │   │   │   ├── CMakeLists.txt
│   │   │   │   ├── jni_bridge.cpp          ← ONLY JNI interface to core
│   │   │   │   └── melonDS/                ← C++ core (full access allowed)
│   │   │   ├── java/com/drasticds/emulator/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── core/
│   │   │   │   │   ├── EmulatorEngine.kt   ← Kotlin wrapper for JNI bridge
│   │   │   │   │   └── EmulatorState.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   └── RomRepository.kt
│   │   │   │   │   ├── datasource/
│   │   │   │   │   │   ├── LocalRomDataSource.kt
│   │   │   │   │   │   └── RomMetadataDataSource.kt
│   │   │   │   │   └── model/
│   │   │   │   │       └── Rom.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── theme/
│   │   │   │   │   │   ├── Color.kt
│   │   │   │   │   │   ├── Theme.kt
│   │   │   │   │   │   └── Type.kt
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── library/
│   │   │   │   │   │   │   ├── LibraryScreen.kt
│   │   │   │   │   │   │   └── LibraryViewModel.kt
│   │   │   │   │   │   ├── emulation/
│   │   │   │   │   │   │   ├── EmulationScreen.kt
│   │   │   │   │   │   │   └── EmulationViewModel.kt
│   │   │   │   │   │   ├── settings/
│   │   │   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   │   │   └── SettingsViewModel.kt
│   │   │   │   │   │   └── about/
│   │   │   │   │   │       └── AboutScreen.kt
│   │   │   │   │   └── components/
│   │   │   │   │       ├── RomCard.kt
│   │   │   │   │       ├── TouchController.kt
│   │   │   │   │       ├── DualScreen.kt
│   │   │   │   │       └── AdBanner.kt
│   │   │   │   ├── ads/
│   │   │   │   │   ├── AdManager.kt
│   │   │   │   │   ├── AppOpenAdManager.kt
│   │   │   │   │   └── InterstitialAdManager.kt
│   │   │   │   ├── deeplink/
│   │   │   │   │   └── DeepLinkHandler.kt
│   │   │   │   └── di/
│   │   │   │       └── AppModule.kt        ← Hilt dependency injection
│   │   │   └── res/
│   │   │       ├── values/
│   │   │       │   ├── strings.xml
│   │   │       │   └── colors.xml
│   │   │       └── drawable/
│   │   └── test/                           ← Unit tests
│   └── build.gradle.kts
├── buildSrc/                               ← Centralised dependency versions
│   └── Dependencies.kt
├── gradle/
│   └── libs.versions.toml                  ← Version catalog
├── CORE_CHANGES.md                         ← Log of all C++ core modifications
├── LICENSE                                 ← GPL v3 full text
├── README.md
└── build.gradle.kts
```

---

## 4. Design System

### 4.1 Colour Palette

```kotlin
// ui/theme/Color.kt
val Primary         = Color(0xFF38629F)   // drasticds.com accent — PRIMARY
val PrimaryDark     = Color(0xFF1E3A5F)   // Darker shade for dark theme
val PrimaryLight    = Color(0xFF5B8ACC)   // Lighter shade for highlights
val Background      = Color(0xFF0F172A)   // Deep dark — matches site dark mode
val Surface         = Color(0xFF1E293B)   // Card surfaces
val SurfaceVariant  = Color(0xFF334155)   // Secondary surfaces
val OnPrimary       = Color(0xFFFFFFFF)   // White text on primary
val OnBackground    = Color(0xFFE2E8F0)   // Light text on dark background
val OnSurface       = Color(0xFFCBD5E1)   // Secondary text
val Accent          = Color(0xFF38629F)   // Same as primary
val Success         = Color(0xFF16A34A)   // Save state success, positive states
val Error           = Color(0xFFDC2626)   // Error states
```

### 4.2 Typography

- Primary font: **Manrope** (matches drasticds.com)
- Load via `app/src/main/res/font/` — do not use Google Fonts network loading
  in production builds (offline reliability)
- Fallback: system sans-serif

### 4.3 App Icon

- Based on existing site icon at:
  `https://drasticds.com/wp-content/uploads/2025/02/Drastic-DS-icon.png`
- Adaptive icon required for API 26+:
  - Foreground layer: DS device shape with site blue `#38629F`
  - Background layer: dark `#0F172A`
- Provide all densities: mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi
- Round icon variant required
- Monochrome variant for themed icons (Android 13+)

---

## 5. Emulator Core — melonDS Integration

### 5.1 Fork Details

- Base: `github.com/rafaelvcaetano/melonDS-android`
- Upstream: `github.com/melonDS-emu/melonDS`
- Our fork: `github.com/drasticdscom/drasticds-android`
- Core lives at: `app/src/main/cpp/melonDS/`
- All core modifications logged in `CORE_CHANGES.md`

### 5.2 Boot Modes

The app supports two boot modes. HLE is the default and requires no user
action. Native BIOS is optional and requires the user to provide files they
legally own.

```
HLE Boot (default):
  → No BIOS files required
  → melonDS built-in HLE BIOS implementation
  → Works for the majority of commercial NDS ROMs
  → User sees no BIOS-related UI unless they choose to set up Native Boot

Native BIOS Boot (optional, user-initiated):
  → User navigates to Settings → BIOS Files
  → User selects their own bios7.bin, bios9.bin, firmware.bin
  → Files are copied to app's private storage
  → Better compatibility with some ROMs
  → UI clearly states: "Provide your own legally obtained BIOS files"
```

### 5.3 JNI Bridge Contract

The JNI bridge (`jni_bridge.cpp`) exposes exactly these functions to Kotlin.
Never add business logic to the JNI bridge — it is a pure passthrough.

```cpp
// Lifecycle
JNIEXPORT jboolean JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeInit
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeReset
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeStop
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeDestroy

// ROM loading
JNIEXPORT jboolean JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeLoadRom
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeRunFrame

// Input
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeTouchDown
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeTouchUp
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeButtonDown
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeButtonUp

// Save states
JNIEXPORT jboolean JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeSaveState
JNIEXPORT jboolean JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeLoadState

// Configuration
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeSetFrameskip
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeSetAudioVolume
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeSetThreadedRendering
JNIEXPORT void     JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeSetJITEnabled

// BIOS (optional)
JNIEXPORT jboolean JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeSetBiosDir
JNIEXPORT jboolean JNICALL Java_com_drasticds_emulator_core_EmulatorEngine_nativeIsBiosLoaded
```

### 5.4 Performance Targets

| Device tier | Target | JIT | Threaded rendering |
|---|---|---|---|
| Flagship (Snapdragon 8 Gen 1+) | 60 FPS locked | On | On |
| Mid-range (Snapdragon 700 series) | 60 FPS most games | On | On |
| Budget (Snapdragon 400 series) | 30–60 FPS | On | Off |
| API 28 minimum device | Playable | On | Off |

Always enable JIT on ARM64 devices. Disable on x86 emulators (Android
Studio AVD). Detect at runtime via `Build.SUPPORTED_ABIS`.

---

## 6. Screen Architecture

### 6.1 Navigation Graph

```
App Launch
    └── AppOpenAd (if cold start, max once per 4 hours)
            └── LibraryScreen (home)
                    ├── [tap ROM] → EmulationScreen
                    │                   └── [back/exit] → LibraryScreen
                    │                                         └── InterstitialAd
                    ├── [settings icon] → SettingsScreen
                    └── [about icon] → AboutScreen
```

### 6.2 LibraryScreen — Home

**Layout toggle — user can switch between Grid and List:**
- Default: Grid view — 2 columns portrait, 3 columns landscape
- Toggle: List view — one ROM per row with more metadata visible
- Toggle button in top bar (grid/list icon)
- User preference persisted via DataStore

**Grid view — RomCard spec:**
- Box art thumbnail fills card top (aspect ratio 1:1)
- No box art placeholder: generic DS cartridge icon in brand blue #38629F
  on dark surface background — never show broken image or blank space
- ROM title below thumbnail — max 2 lines, ellipsized
- Last played date — small text below title
- Rounded corners 12dp, subtle elevation shadow

**List view — RomCard spec:**
- Thumbnail on left (64x64dp), DS cartridge placeholder if no art
- ROM title — bold, full width
- File name and last played date below title
- Chevron arrow on right

**Top bar:**
- Title: "DrasticDS"
- Search icon — filters ROM list by title in real time
- Sort icon — bottom sheet with options: Last Played, A–Z, Date Added
- Grid/List toggle icon

**Bottom navigation bar — 3 tabs:**
- Library (home icon) — default tab
- Settings (gear icon)
- About (info icon)

**FAB — bottom right:**
- "+" icon
- Opens Android SAF directory picker filtered to .nds and .zip files
- User selects a folder — all ROMs in that folder are scanned and added

**Empty state (no ROMs loaded):**
- DrasticDS icon centred
- Text: "Add your DS game files to get started"
- Subtext: "Tap the + button to load a game file from your device"
- "Add ROM" button — same action as FAB
- NO mention of where to get ROMs
- NO links to any website in this context

**Banner ad — bottom of screen, above navigation bar**
- Always visible on Library screen
- Never overlaps content

### 6.2a AppTheme

- Follows system setting — dark or light based on phone's system theme
- Dark theme: Background #0F172A, Surface #1E293B, text white
- Light theme: Background #F8FAFC, Surface #FFFFFF, text #0F172A
- Primary colour #38629F in both themes
- DrasticDSTheme wraps entire app in MainActivity

### 6.3 EmulationScreen — Sacred Zone

**This screen has zero ads of any kind. No exceptions. Ever.**

**Screen layout — user can switch between two modes:**

Mode 1 — Vertical stack (portrait, like holding a real DS):
- Top DS screen fills upper half
- Bottom DS screen (touch) fills lower half
- On-screen controller overlaid on bottom screen area

Mode 2 — Side by side (landscape, wider view):
- Top DS screen on left
- Bottom DS screen on right
- On-screen controller below or overlaid

Layout switcher: button in pause menu and in-game quick settings.
User preference persisted via DataStore.

**On-screen controller — fully customisable:**
- Default layout:
  - D-pad: bottom left
  - A/B/X/Y: bottom right
  - L/R: top left/right
  - Start/Select: bottom centre
  - Menu button: top centre (opens pause menu)
- Opacity: slider 0–100% (default 70%)
- Position: each button group is draggable and repositionable
- Size: pinch to resize each button group
- All customisations saved per-game via DataStore
- "Reset layout" option in controller settings

**Pause menu (full screen overlay):**
- Resume
- Save State → sub-menu with slots 1–5 (shows screenshot thumbnail + date)
- Load State → sub-menu with slots 1–5
- Screen Layout (switch between vertical/landscape)
- Controller Settings (opacity, position, size)
- Exit to Library

**Technical requirements:**
- Landscape orientation forced during emulation
- System bars hidden — immersive mode (BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
- Screen kept awake — WAKE_LOCK permission
- Back gesture disabled during emulation — must use pause menu to exit

### 6.3a v1.1 Roadmap — Controller Themes
Custom controller skin themes (like Delta emulator) are planned for v1.1.
The controller layout system must be built with theming in mind:
- Controller buttons rendered as composables with replaceable assets
- Theme = a set of drawable resources + layout config JSON
- Theme picker screen in Settings (v1.1)
- Community theme sharing via drasticds.com (v1.2)
Do not implement themes in Phase 2 — but architect the controller
composables so themes can be added later without a full rewrite.

### 6.4 SettingsScreen

- Display: screen layout (side by side / top-bottom / single), scaling filter
- Audio: volume, audio sync on/off
- Performance: frameskip (0–3), threaded rendering, JIT (advanced users)
- Controller: on-screen controller opacity, button size, layout position
- BIOS Files: optional — "Load your own BIOS files" section with
  clear legal language: "Provide BIOS files from a DS you own"
- About: link to About screen
- Banner ad at bottom

### 6.5 AboutScreen

Required by GPL v3 and Play Store policy. Must contain:
- App name and version number (from `BuildConfig.VERSION_NAME`)
- "DrasticDS Emulator is built on melonDS"
- Link: "View source code on GitHub" → `https://github.com/drasticdscom/drasticds-android`
- Link: "Visit DrasticDS.com" → `https://drasticds.com` (Chrome Custom Tab)
- Link: "Privacy Policy" → `https://drasticds.com/privacy-policy-app/`
- melonDS attribution: "melonDS is developed by Arisotura and contributors"
- GPL v3 notice: "This software is licensed under the GNU General Public
  License v3. Source code is available at the link above."
- No banner ad on this screen — it is a legal compliance screen

---

## 7. AdMob Integration

### 7.1 Ad Unit IDs

Ad unit IDs are stored in `local.properties` (never committed to git) and
injected into `BuildConfig` via `build.gradle.kts`. Use test IDs during
development, production IDs in release builds only.

```kotlin
// build.gradle.kts
buildTypes {
    debug {
        buildConfigField("String", "ADMOB_BANNER_ID",
            "\"ca-app-pub-3940256099942544/6300978111\"") // test
        buildConfigField("String", "ADMOB_INTERSTITIAL_ID",
            "\"ca-app-pub-3940256099942544/1033173712\"") // test
        buildConfigField("String", "ADMOB_APP_OPEN_ID",
            "\"ca-app-pub-3940256099942544/9257395921\"") // test
    }
    release {
        buildConfigField("String", "ADMOB_BANNER_ID",
            "\"${properties["ADMOB_BANNER_ID"]}\"")
        buildConfigField("String", "ADMOB_INTERSTITIAL_ID",
            "\"${properties["ADMOB_INTERSTITIAL_ID"]}\"")
        buildConfigField("String", "ADMOB_APP_OPEN_ID",
            "\"${properties["ADMOB_APP_OPEN_ID"]}\"")
    }
}
```

### 7.2 Ad Placement Rules

| Screen | Ad type | Rule |
|---|---|---|
| App cold start | App Open | Max once per 4 hours — track last shown timestamp in SharedPreferences |
| LibraryScreen | Banner (bottom) | Always shown while on this screen |
| SettingsScreen | Banner (bottom) | Always shown while on this screen |
| After exit from EmulationScreen | Interstitial | Show once per session per game exit |
| EmulationScreen | NOTHING | Absolute rule — zero ads during gameplay |
| AboutScreen | NOTHING | Legal compliance screen — no ads |

### 7.3 Ad Initialisation

```kotlin
// Application class — initialise once at app start
class DrasticDSApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) { initStatus ->
            // AdMob ready — log to Firebase Analytics
        }
        AppOpenAdManager.init(this)
    }
}
```

### 7.4 User Consent (GDPR/CCPA)

Always use Google's User Messaging Platform (UMP) SDK for consent.
Show the consent form on first launch and whenever consent status is unknown.
Never serve personalised ads without explicit consent.

```kotlin
// ConsentManager.kt — run before MobileAds.initialize()
val params = ConsentRequestParameters.Builder()
    .setTagForUnderAgeOfConsent(false)
    .build()
consentInformation.requestConsentInfoUpdate(activity, params, ...)
```

---

## 8. Firebase Integration

### 8.0 Gradle Setup — Exact Configuration

These are the exact Gradle configurations required. Do not deviate from these
versions — they match the google-services.json already generated for this project.

**Root-level `build.gradle.kts`:**
```kotlin
plugins {
    id("com.android.application") version "8.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    // Google services plugin — required for Firebase
    id("com.google.gms.google-services") version "4.4.4" apply false
    // Crashlytics plugin
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
    // Hilt
    id("com.google.dagger.hilt.android") version "2.50" apply false
}
```

**App-level `app/build.gradle.kts`:**
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")       // Firebase
    id("com.google.firebase.crashlytics")       // Crashlytics
    id("com.google.dagger.hilt.android")        // Hilt DI
    id("kotlin-kapt")
}

android {
    namespace = "com.drasticds.emulator"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.drasticds.emulator"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
}

dependencies {
    // Firebase BoM — controls all Firebase library versions
    // Current confirmed version: 34.11.0
    implementation(platform("com.google.firebase:firebase-bom:34.11.0"))
    // No version numbers needed on individual Firebase deps when using BoM
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx") // v1.1+
}
```

**google-services.json placement and security:**
- File lives at `app/google-services.json`
- Add to `.gitignore` — NEVER commit to git
- In GitHub Actions, decode from `GOOGLE_SERVICES_JSON` secret:
  ```yaml
  - name: Decode google-services.json
    run: echo "${{ secrets.GOOGLE_SERVICES_JSON }}" | base64 -d > app/google-services.json
  ```
- To generate the base64 string for the GitHub Secret:
  ```bash
  base64 -i app/google-services.json | pbcopy   # macOS — copies to clipboard
  base64 app/google-services.json | xclip        # Linux
  ```

**Confirmed project values from google-services.json (already generated):**
```
project_id:      drasticds-emulator
project_number:  778529561632
app_id:          1:778529561632:android:255c252287849b8923f045
package_name:    com.drasticds.emulator
storage_bucket:  drasticds-emulator.firebasestorage.app
```
Note: The api_key in google-services.json is a restricted Android API key —
it is safe to include in the app but must not be committed to the public repo.
Store the entire google-services.json as a GitHub Secret instead.

### 8.1 Services Used

| Service | Purpose | Cost |
|---|---|---|
| Firebase Analytics | User behaviour, screen views, custom events | Free |
| Firebase Crashlytics | Crash reporting and ANR detection | Free |
| Firebase Remote Config | A/B test features without app updates | Free |
| Firebase Cloud Messaging | Push notifications (v1.1+) | Free |

### 8.2 Analytics Events

Log these custom events. Never log personally identifiable information.

```kotlin
"rom_loaded"            // params: file_extension, file_size_mb
"game_started"          // params: play_time_seconds (on exit)
"save_state_created"    // params: slot_number
"save_state_loaded"     // params: slot_number
"bios_configured"       // no params
"settings_changed"      // params: setting_name, new_value
"deeplink_received"     // params: source (website)
"ad_interstitial_shown"
"ad_banner_shown"
"ad_app_open_shown"
```

### 8.3 Remote Config Defaults

```kotlin
val defaults = mapOf(
    "interstitial_frequency"    to 1,
    "app_open_cooldown_hours"   to 4,
    "jit_enabled_default"       to true,
    "threaded_render_default"   to true,
    "frameskip_default"         to 0,
    "show_website_cta_in_about" to true
)
```

---

## 9. Deep Link Integration

### 9.1 Deep Link Schema

The app handles deep links from drasticds.com in this format:

```
https://drasticds.com/open-in-app/?rom=ENCODED_ROM_URL&title=ROM_TITLE
```

Android App Links (not URI schemes) — verified via Digital Asset Links JSON
at `https://drasticds.com/.well-known/assetlinks.json`:

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "com.drasticds.emulator",
    "sha256_cert_fingerprints": ["YOUR_RELEASE_CERT_SHA256"]
  }
}]
```

### 9.2 DeepLinkHandler Behaviour

```kotlin
// When deep link received:
// 1. Parse ROM URL from intent data
// 2. Check if ROM already exists in library (match by filename)
// 3. If exists → launch EmulationScreen directly
// 4. If not exists → show bottom sheet:
//    "Game file not found on your device"
//    "Load from storage" button → SAF picker
// NEVER automatically download the ROM file
// NEVER show the URL of the ROM source
```

### 9.3 Website Integration Requirements

Add to every ROM post on drasticds.com:

```html
<!-- Smart App Banner -->
<meta name="google-play-app" content="app-id=com.drasticds.emulator">

<!-- Deep link button — only shown if app is installed -->
<a href="https://drasticds.com/open-in-app/?rom=ROM_URL&title=ROM_TITLE">
  Open in DrasticDS Emulator
</a>
```

---

## 10. GitHub Actions CI/CD

### 10.1 Secrets Required in GitHub Repository

These must be set in GitHub → Settings → Secrets → Actions before any
release workflow can run:

```
KEYSTORE_BASE64          # base64-encoded .jks keystore file
KEYSTORE_PASSWORD        # keystore password
KEY_ALIAS                # key alias within keystore
KEY_PASSWORD             # key password
ADMOB_BANNER_ID          # production AdMob banner unit ID
ADMOB_INTERSTITIAL_ID    # production AdMob interstitial unit ID
ADMOB_APP_OPEN_ID        # production AdMob app open unit ID
GOOGLE_SERVICES_JSON     # base64-encoded google-services.json
```

### 10.2 build-debug.yml — Every Push

```yaml
name: Debug Build
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          submodules: recursive      # pulls melonDS submodule
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - uses: android-actions/setup-android@v3
      - name: Decode google-services.json
        run: echo "${{ secrets.GOOGLE_SERVICES_JSON }}" | base64 -d > app/google-services.json
      - name: Build Debug APK
        run: ./gradlew assembleDebug
      - uses: actions/upload-artifact@v4
        with:
          name: debug-apk
          path: app/build/outputs/apk/debug/*.apk
```

### 10.3 build-release.yml — On Tag Push (e.g. v1.0.0)

```yaml
name: Release Build
on:
  push:
    tags: ['v*']
jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          submodules: recursive
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - uses: android-actions/setup-android@v3
      - name: Decode keystore
        run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > keystore.jks
      - name: Decode google-services.json
        run: echo "${{ secrets.GOOGLE_SERVICES_JSON }}" | base64 -d > app/google-services.json
      - name: Write local.properties
        run: |
          echo "ADMOB_BANNER_ID=${{ secrets.ADMOB_BANNER_ID }}" >> local.properties
          echo "ADMOB_INTERSTITIAL_ID=${{ secrets.ADMOB_INTERSTITIAL_ID }}" >> local.properties
          echo "ADMOB_APP_OPEN_ID=${{ secrets.ADMOB_APP_OPEN_ID }}" >> local.properties
      - name: Build Release AAB
        run: ./gradlew bundleRelease
        env:
          KEYSTORE_PATH: ${{ github.workspace }}/keystore.jks
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      - name: Build Release APK (for APKMirror/direct)
        run: ./gradlew assembleRelease
        env:
          KEYSTORE_PATH: ${{ github.workspace }}/keystore.jks
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      - name: Create GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          files: |
            app/build/outputs/bundle/release/*.aab
            app/build/outputs/apk/release/*.apk
          generate_release_notes: true
```

---

## 11. Key Dependencies (libs.versions.toml)

```toml
[versions]
kotlin               = "1.9.22"
compose-bom          = "2024.02.00"
compose-compiler     = "1.5.10"
activity-compose     = "1.8.2"
lifecycle            = "2.7.0"
hilt                 = "2.50"
room                 = "2.6.1"
coroutines           = "1.7.3"
admob                = "22.6.0"
firebase-bom         = "32.7.2"
ump                  = "2.2.0"
coil                 = "2.5.0"
navigation           = "2.7.6"
datastore            = "1.0.0"

[libraries]
compose-bom          = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui           = { group = "androidx.compose.ui", name = "ui" }
compose-material3    = { group = "androidx.compose.material3", name = "material3" }
compose-preview      = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
activity-compose     = { group = "androidx.activity", name = "activity-compose", version.ref = "activity-compose" }
lifecycle-viewmodel  = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
hilt-android         = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler        = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
room-runtime         = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx             = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler        = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
admob                = { group = "com.google.android.gms", name = "play-services-ads", version.ref = "admob" }
ump                  = { group = "com.google.android.ump", name = "user-messaging-platform", version.ref = "ump" }
firebase-bom         = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebase-bom" }
firebase-analytics   = { group = "com.google.firebase", name = "firebase-analytics-ktx" }
firebase-crashlytics = { group = "com.google.firebase", name = "firebase-crashlytics-ktx" }
firebase-config      = { group = "com.google.firebase", name = "firebase-config-ktx" }
coil                 = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }
navigation           = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }
datastore            = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
coroutines           = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
```

---

## 12. Play Store Submission Checklist

Run through this before every production submission:

### Store Listing
- [ ] App name: "DrasticDS Emulator" — no Nintendo trademarks
- [ ] Short description (80 chars max): "Fast, accurate Nintendo DS emulator for Android"
- [ ] Full description: mentions no specific game titles, no ROM sources
- [ ] Category: "Tools" or "Arcade" — test both, Tools has lower competition
- [ ] Content rating: complete IARC questionnaire honestly
- [ ] Privacy Policy URL: `https://drasticds.com/privacy-policy-app/`
- [ ] At least 4 screenshots showing actual gameplay
- [ ] Feature graphic: 1024×500px, your branding
- [ ] App icon: all required sizes present

### Technical
- [ ] Target SDK = API 35
- [ ] Min SDK = API 28
- [ ] 64-bit native libraries present (arm64-v8a)
- [ ] 32-bit native libraries present (armeabi-v7a) for compatibility
- [ ] No BIOS files in APK/AAB (verify with `aapt dump` or apktool)
- [ ] No ROM files in APK/AAB
- [ ] All permissions declared with rationale
- [ ] ProGuard/R8 rules correct — AdMob and Firebase classes not obfuscated

### Legal
- [ ] LICENSE file (GPL v3) in repo root
- [ ] About screen links to GitHub source
- [ ] app-ads.txt live at `https://drasticds.com/app-ads.txt`
- [ ] Google Play Developer account identity verified
- [ ] AdMob account linked to Play Console

---

## 13. Development Phases

### Phase 1 — Foundation (Days 1–7)
Goal: Compilable, runnable, branded build with core emulation working

- Fork `rafaelvcaetano/melonDS-android` to `drasticdscom/drasticds-android`
- Configure package name `com.drasticds.emulator`
- Set up GitHub Actions debug build workflow
- Apply brand colours and Manrope font to theme
- Replace default icon with DrasticDS icon (all densities)
- Verify melonDS core compiles for arm64-v8a and armeabi-v7a
- Verify a ROM loads and runs in the emulator
- Set up Firebase project and add `google-services.json` via GitHub Secret
- Deliverable: signed debug APK that runs a ROM

### Phase 2 — Core UI (Days 8–14)
Goal: Polished ROM library and gameplay experience

- Build LibraryScreen in Jetpack Compose with ROM grid
- Implement ROM loading via Android Storage Access Framework (SAF)
- Build EmulationScreen with dual-screen layout
- Build TouchController composable
- Implement save states (5 slots)
- Implement basic settings (screen layout, audio, frameskip)
- Empty state UI (no ROMs loaded)
- Landscape lock during emulation
- Immersive mode (hidden system bars) during emulation
- Deliverable: fully functional emulator with polished UI

### Phase 3 — Monetisation & Analytics (Days 15–19)
Goal: Revenue infrastructure fully operational

- Integrate AdMob SDK with UMP consent flow
- Implement AppOpenAdManager (cold start, 4-hour cooldown)
- Implement banner ads on LibraryScreen and SettingsScreen
- Implement interstitial ad on game exit
- Verify ZERO ads appear on EmulationScreen
- Integrate Firebase Analytics with all custom events
- Integrate Firebase Crashlytics
- Set up Firebase Remote Config with defaults
- Deliverable: all ads working in test mode, analytics firing

### Phase 4 — Ecosystem & Polish (Days 20–25)
Goal: Website integration and Play Store readiness

- Implement Android App Links deep link handler
- Build AboutScreen with all GPL and legal requirements
- Build SettingsScreen with BIOS optional setup
- Add "Visit DrasticDS.com" in Settings and About
- Add app-ads.txt to drasticds.com
- Add assetlinks.json to drasticds.com
- Add Smart App Banner meta tag to WordPress theme
- Set up GitHub Actions release workflow with signing
- Switch AdMob from test IDs to production IDs in release config
- Full testing on minimum 3 physical devices
- Deliverable: release-ready signed AAB

### Phase 5 — Launch (Days 26–28)
Goal: App live on Play Store and direct APK available

- Create Play Store listing with all assets
- Submit AAB to Play Store internal testing track first
- Test internal track build thoroughly
- Promote to production track
- Upload APK to APKMirror simultaneously
- Monitor Firebase Crashlytics for day-one crashes
- Deliverable: app live on Play Store

---

## 14. What Antigravity Must Never Do

- Never add any ROM download functionality inside the app
- Never add a web browser component that navigates to ROM pages
- Never add links to ROM sites from within the app
- Never commit `google-services.json` or `keystore.jks` to git
- Never commit `local.properties` to git
- Never use hardcoded production ad unit IDs in debug builds
- Never remove the GPL license headers from any C++ or Kotlin file
- Never add ads to EmulationScreen under any circumstances
- Never add ads to AboutScreen under any circumstances
- Never use deprecated AdMob APIs — always use the current SDK patterns
- Never store user data without disclosing it in the Privacy Policy
- Never request permissions not listed in AndroidManifest.xml
- Never target API below 28 or above the current compile SDK
- Never modify the JNI bridge to contain business logic
- Never use Java — this is a Kotlin-only project (except JNI C++ bridge)

---

## 15. Frequently Needed Commands

```bash
# Clone with submodules (melonDS core)
git clone --recursive https://github.com/drasticdscom/drasticds-android

# Update melonDS submodule to latest upstream
git submodule update --remote app/src/main/cpp/melonDS

# Build debug APK locally
./gradlew assembleDebug

# Build release AAB locally (requires local.properties with secrets)
./gradlew bundleRelease

# Run unit tests
./gradlew test

# Run lint
./gradlew lint

# Check for dependency updates
./gradlew dependencyUpdates

# Create and push a release tag (triggers GitHub Actions release build)
git tag v1.0.0 && git push origin v1.0.0

# Decode and install debug APK to connected device
./gradlew installDebug

# View Crashlytics crashes (Firebase CLI)
firebase crashlytics:symbols:upload --app=YOUR_APP_ID app/build/outputs/mapping/release/mapping.txt
```

---

## 16. Important URLs for This Project

| Resource | URL |
|---|---|
| GitHub repo | `https://github.com/drasticdscom/drasticds-android` |
| Play Console | `https://play.google.com/apps/publish` |
| AdMob console | `https://admob.google.com` |
| Firebase console | `https://console.firebase.google.com` |
| melonDS upstream | `https://github.com/melonDS-emu/melonDS` |
| melonDS-android base | `https://github.com/rafaelvcaetano/melonDS-android` |
| App Privacy Policy | `https://drasticds.com/privacy-policy-app/` |
| App landing page | `https://drasticds.com/emulator/` |
| app-ads.txt | `https://drasticds.com/app-ads.txt` |
| assetlinks.json | `https://drasticds.com/.well-known/assetlinks.json` |
| GPL v3 text | `https://www.gnu.org/licenses/gpl-3.0.txt` |

---

## 17. How to Use This SKILL.md in Google Antigravity

### 17.1 Where the File Lives in the Repository

Place this file at exactly this path inside the cloned repo:

```
drasticds-android/
└── .agent/
    └── skills/
        └── drasticds-emulator/
            └── SKILL.md          ← this file
```

Create the directories and file before doing anything else:

```bash
# After cloning the repo
cd drasticds-android
mkdir -p .agent/skills/drasticds-emulator
# Then paste or copy SKILL.md into that directory
```

Commit it to the repo immediately:

```bash
git add .agent/skills/drasticds-emulator/SKILL.md
git commit -m "chore: add Antigravity skill for DrasticDS emulator"
git push
```

The SKILL.md must be committed to the repo — Antigravity reads it from the
workspace, not from a separate location.

### 17.2 How Antigravity Discovers and Uses the Skill

When you open Antigravity and point it at the `drasticds-android` workspace:

1. Antigravity automatically scans for `.agent/skills/*/SKILL.md` files
2. It reads the YAML frontmatter (the `triggers:` list at the top)
3. Whenever your prompt contains any of the trigger words (android, emulator,
   kotlin, admob, firebase, melonDS, etc.), Antigravity automatically loads
   this SKILL.md into its context
4. The agent then follows every instruction, rule, and specification in this
   file without you needing to repeat them

You do not need to mention the skill by name. Just describe what you want to
build and Antigravity picks up the skill automatically via the triggers.

### 17.3 Opening the Project in Antigravity

1. Open Antigravity (Google AI Studio → Antigravity, or the Antigravity CLI)
2. Click **"Open Workspace"** or **"Add Folder"**
3. Select your cloned `drasticds-android/` directory
4. Antigravity will index the project and detect the SKILL.md automatically
5. You will see a confirmation that the `drasticds-emulator` skill is loaded

### 17.4 First Prompt to Give Antigravity

Once the workspace is open, give this exact prompt to start Phase 1:

```
Fork rafaelvcaetano/melonDS-android as the base, configure it for the
DrasticDS Emulator project, and complete Phase 1 of the development plan
as defined in the skill. Start by setting up the project structure,
applying the brand colours and typography, replacing the app icon, and
verifying the melonDS core compiles for arm64-v8a and armeabi-v7a.
The target is a signed debug APK that successfully loads and runs a ROM.
```

Antigravity will read the SKILL.md, understand the full project context,
and begin executing Phase 1 step by step.

### 17.5 Prompts for Each Subsequent Phase

Use these prompts to move through each development phase:

**Phase 2 — Core UI:**
```
Phase 1 is complete. Now complete Phase 2: build the LibraryScreen ROM grid,
implement ROM loading via Android SAF, build the EmulationScreen with dual
screens and touch controller, implement save states with 5 slots, and add
basic settings. Follow all screen specifications in the skill exactly.
```

**Phase 3 — Monetisation:**
```
Phase 2 is complete. Now complete Phase 3: integrate AdMob with UMP consent
flow, implement App Open ad with 4-hour cooldown, banner ads on Library and
Settings screens, interstitial on game exit, Firebase Analytics with all
custom events, and Crashlytics. Verify absolutely zero ads appear on
EmulationScreen. Use test ad unit IDs in debug builds only.
```

**Phase 4 — Ecosystem and Polish:**
```
Phase 3 is complete. Now complete Phase 4: implement Android App Links deep
link handler, build the AboutScreen with all GPL requirements, add the
Visit DrasticDS.com CTA in Settings and About, set up the GitHub Actions
release workflow with keystore signing via GitHub Secrets, and switch to
production ad unit IDs in the release build config only.
```

**Phase 5 — Launch:**
```
Phase 4 is complete. Prepare the Play Store submission: generate all
required screenshot dimensions, create the feature graphic at 1024x500,
write the store listing description following all compliance rules in the
skill, run the Play Store submission checklist from section 12, and produce
the final signed AAB ready for upload to Play Console.
```

### 17.6 How to Ask Antigravity to Fix Things

When something breaks, always reference the skill explicitly:

```
The interstitial ad is showing on the EmulationScreen — this violates
the ad placement rules in the skill. Find the source of this bug and
fix it so ads are completely absent from EmulationScreen.
```

```
The JNI bridge has business logic in it — this violates section 2.2 of
the skill. Refactor to move that logic to EmulatorEngine.kt where it belongs.
```

### 17.7 How to Update the Skill as the Project Evolves

When you add new features, dependencies, or constraints, update this
SKILL.md first, then prompt Antigravity:

```
I have updated the SKILL.md with new requirements for [feature].
Read the updated skill and implement accordingly.
```

Always commit SKILL.md updates before running new Antigravity sessions.

### 17.8 Important — What NOT to Ask Antigravity to Do

These requests directly contradict the project's legal requirements.
Antigravity will refuse them based on this skill — that is correct behaviour.

- "Add a ROM downloader inside the app"
- "Add a link to drasticds.com/roms/ inside the app"
- "Show a list of available ROMs from the website inside the app"
- "Add a built-in browser for finding game files"
- "Put an ad on the gameplay screen to increase revenue"
- "Bundle a sample ROM so users can try the emulator"
- "Add the BIOS files so the emulator works without setup"

If Antigravity refuses these — it is working correctly.
