# Oura Ring App

Kotlin Multiplatform companion app for [Oura Ring](https://ouraring.com) data — sleep, readiness, activity, stress, and body metrics.

Native offline-first alternative to the [oura-dashboard](https://github.com/MaximeMichaud/oura-dashboard) web project, built with Compose Multiplatform for Android, Desktop, and iOS.

## Stack

- **Kotlin 2.3.0** + **Compose Multiplatform 1.10.1**
- **Ktor 3.4.0** — HTTP client with retry logic
- **SQLDelight 2.2.1** — Typed local database (12 tables)
- **Koin 4.1.1** — Dependency injection
- **Vico 2.1.4** — Multiplatform charts

## Platforms

| Platform | Sync | Token Storage | Build |
|----------|------|---------------|-------|
| **Android** (API 26+) | WorkManager (30 min) | SharedPreferences | APK/AAB |
| **Desktop** (JVM) | Coroutine scheduler | Java Preferences | DMG/MSI/Deb |
| **iOS** | Coroutine scheduler | NSUserDefaults | Framework |

## Features

- 6 screens: Overview, Sleep, Readiness, Activity, Body, Settings
- All 11 Oura API v2 endpoints synced with incremental updates
- Offline-first: data stored locally in SQLite
- Responsive layout: bottom nav on phones, side rail on tablets/desktop
- Background sync with error tracking

## Quick Start

### Prerequisites

- JDK 17+
- Android SDK (for Android builds)

### Run Desktop

```bash
./gradlew :composeApp:run
```

### Build Android APK

```bash
./gradlew :composeApp:assembleDebug
```

### Package Desktop

```bash
./gradlew :composeApp:packageDeb    # Linux
./gradlew :composeApp:packageDmg    # macOS
./gradlew :composeApp:packageMsi    # Windows
```

## Configuration

1. Get a personal access token from [cloud.ouraring.com](https://cloud.ouraring.com/personal-access-tokens)
2. Open the app -> Settings screen
3. Enter your token
4. Data syncs automatically (history from 2020-01-01, 30-min interval)

## API Endpoints

| Endpoint | Description |
|----------|-------------|
| `sleep` | Detailed sleep sessions (phases, HR, HRV) |
| `daily_sleep` | Daily sleep scores and contributors |
| `daily_readiness` | Readiness scores and drivers |
| `daily_activity` | Steps, calories, MET, movement |
| `daily_spo2` | Blood oxygen saturation |
| `daily_stress` | Stress summary and levels |
| `daily_resilience` | Resilience index |
| `daily_cardiovascular_age` | Vascular age |
| `vO2_max` | Cardio fitness |
| `workout` | Exercise sessions |
| `sleep_time` | Optimal bedtime recommendations |
