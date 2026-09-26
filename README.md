# SettleX

> Premium fintech app for splitting bills, tracking group expenses, and settling debts with UPI integration and AI-powered receipt scanning.

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-100%25-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-3DDC84?style=flat-square)](https://developer.android.com/reference/android/os/Build.VERSION_CODES)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-3DDC84?style=flat-square)](https://developer.android.com/reference/android/os/Build.VERSION_CODES)
[![License](https://img.shields.io/badge/License-Not%20Specified-lightgrey?style=flat-square)](#license)

SettleX simplifies expense management for groups by enabling users to split bills, track shared expenses, and settle debts with smart calculations. Powered by Gemini AI for intelligent receipt scanning and ML Kit for text recognition, SettleX provides a seamless, secure experience with Firebase backend integration and UPI payment support.

## Contents

- [Features](#features)
- [Technical Stack](#technical-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Building and Running](#building-and-running)
- [Key Components](#key-components)
- [Database Schema](#database-schema)
- [API Integration](#api-integration)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Features

### Core Functionality

- **Bill Splitting** — Create groups and split expenses equally or by custom amounts
- **Expense Tracking** — Manage shared expenses with detailed transaction history
- **Smart Debt Settlement** — Automatic debt simplification algorithm to minimize transaction count
- **Receipt Scanning** — Dual OCR approach using Gemini AI and ML Kit for accurate expense extraction
- **UPI Integration** — Direct payment settlement through UPI (Under development)
- **Multi-user Sync** — Real-time synchronization across devices via Firebase

### AI & Recognition

- **Gemini-Powered OCR** — Advanced receipt analysis with structured JSON extraction
- **ML Kit Text Recognition** — Fallback OCR for offline reliability
- **Intelligent Parsing** — Automatic item detection, amounts, and tax calculation
- **Context-Aware Processing** — Handles various receipt formats and languages

### Security & Persistence

- **Firebase Authentication** — Secure user identity management
- **Cloud Firestore** — Real-time database with offline support
- **Local Room Database** — Encrypted local storage for sensitive data
- **Access Control** — User-based permissions and group privacy

## Technical Stack

### Frontend

- **Language**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose with Material Design 3
- **Navigation**: Compose Navigation
- **State Management**: ViewModel + Compose State

### Backend & Services

- **Real-time Sync**: Firebase Cloud Firestore
- **Authentication**: Firebase Authentication
- **Cloud Messaging**: Firebase Cloud Messaging (FCM)
- **AI/ML**: Gemini AI API, Google ML Kit for Text Recognition
- **Storage**: Firebase Cloud Storage

### Local Database

- **Database**: Android Room (SQLite)
- **ORM**: Room with Kotlin Coroutines
- **Serialization**: Moshi for JSON handling

### Network & APIs

- **HTTP Client**: OkHttp with logging interceptor
- **REST Framework**: Retrofit
- **JSON Processing**: Moshi Kotlin Codegen (via KSP)

### Build & Development

- **Build System**: Gradle with Kotlin DSL
- **Code Generation**: Kotlin Symbol Processing (KSP)
- **Testing**: JUnit 4, Robolectric, Espresso
- **Screenshot Testing**: Roborazzi

### Camera & Media

- **Camera Access**: Jetpack Camera (Camera2, Camera Core, Lifecycle)
- **Permissions**: Accompanist Permissions

## Architecture

SettleX follows **MVVM** (Model-View-ViewModel) architecture with clean separation of concerns:

```
┌─────────────────────────────────────────────┐
│          UI Layer (Compose)                 │
│    • Screens • Composables • Themes        │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│      ViewModel Layer                        │
│    • State Management • Business Logic      │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│      Repository Layer                       │
│    • Data Aggregation • Sync Logic          │
└────────────────┬────────────────────────────┘
                 │
        ┌────────┴─────────┐
        │                  │
   ┌────▼──────┐    ┌──────▼────┐
   │   Local   │    │   Remote  │
   │   (Room)  │    │(Firestore)│
   └───────────┘    └───────────┘
```

## Getting Started

### Prerequisites

- **Android Studio** (Hedgehog or newer recommended)
- **JDK 11** or newer
- **Android SDK** with API level 36
- **Gemini API Key** for AI-powered receipt scanning
- **Firebase Project** (optional for local development)

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/Penguin-boss/SettleX.git
   cd SettleX
   ```

2. **Open in Android Studio**

   - Launch Android Studio
   - Select **File** → **Open**
   - Navigate to the cloned directory and select it
   - Allow Android Studio to sync Gradle and resolve dependencies

3. **Configure the Gemini API Key**

   - Create a `.env` file in the project root directory
   - Add your Gemini API key:

     ```env
     GEMINI_API_KEY=your_gemini_api_key_here
     ```

   - Reference the `.env.example` file for additional configuration options

4. **(Optional) Update Signing Configuration**

   For local debugging, you may remove the debug signing configuration:

   - Open `app/build.gradle.kts`
   - Locate and comment out or remove:

     ```kotlin
     debug {
       signingConfig = signingConfigs.getByName("debugConfig")
     }
     ```

5. **Sync Project with Gradle Files**

   - Click **File** → **Sync Now**
   - Wait for the build to complete

6. **Run the App**

   - Connect an Android device (API 24+) or start an emulator
   - Click **Run** → **Run 'app'** or press `Shift + F10`

## Project Structure

```
SettleX/
├── app/
│   ├── build.gradle.kts                 # App-level build configuration
│   ├── proguard-rules.pro              # ProGuard configuration
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml     # App manifest
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt      # Entry point
│       │   │   ├── data/
│       │   │   │   ├── local/           # Room database layer
│       │   │   │   │   ├── SplitFlowDao.kt
│       │   │   │   │   └── SplitFlowDatabase.kt
│       │   │   │   ├── model/           # Data entities
│       │   │   │   │   └── Entities.kt
│       │   │   │   ├── remote/          # Network & Firebase
│       │   │   │   │   ├── FirebaseSyncManager.kt
│       │   │   │   │   ├── GeminiOcr.kt
│       │   │   │   │   ├── MlKitOcr.kt
│       │   │   │   │   └── MyFirebaseMessagingService.kt
│       │   │   │   └── repository/      # Data aggregation
│       │   │   │       ├── SplitFlowRepository.kt
│       │   │   │       └── PdfExporter.kt
│       │   │   ├── domain/              # Business logic
│       │   │   │   └── DebtSimplifier.kt
│       │   │   ├── ui/
│       │   │   │   ├── screens/         # Compose UI screens
│       │   │   │   │   ├── SplitFlowScreens.kt
│       │   │   │   │   └── CameraScreen.kt
│       │   │   │   ├── theme/           # Design theme
│       │   │   │   │   ├── Color.kt
│       │   │   │   │   ├── Theme.kt
│       │   │   │   │   └── Type.kt
│       │   │   │   └── viewmodel/       # UI state management
│       │   │   │       └── ViewModels.kt
│       │   └── res/                     # Resources
│       │       ├── drawable/
│       │       ├── mipmap-*dpi/
│       │       ├── values/
│       │       └── xml/
│       ├── test/                        # Unit tests
│       └── androidTest/                 # Instrumented tests
├── build.gradle.kts                     # Root build configuration
├── settings.gradle.kts                  # Gradle settings
├── gradle.properties                    # Gradle properties
├── gradle/libs.versions.toml           # Dependency management
├── .env.example                         # Environment variable template
├── metadata.json                        # Project metadata
└── README.md                            # This file
```

## Configuration

### Environment Variables

Create a `.env` file in the project root with the following variables:

| Variable | Required | Description |
|----------|----------|-------------|
| `GEMINI_API_KEY` | Yes | API key for Gemini AI receipt scanning |

### Firebase Setup (Optional)

To enable Firebase features (Firestore sync, Authentication, Cloud Messaging):

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Add an Android app to your project
3. Download `google-services.json` and place it in `app/src/main/` directory
4. Firebase will be automatically initialized at app startup

### Gradle Build Properties

Key properties in `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

## Building and Running

### Debug Build

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

```bash
./gradlew assembleRelease
```

**Prerequisites:**
- Set environment variables: `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`
- Or manually configure signing in `app/build.gradle.kts`

### Run Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Screenshot tests
./gradlew verifyRoborazziDebug
```

### Build APK in Android Studio

1. Select **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Wait for the build to complete
3. APK will be located in `app/build/outputs/apk/debug/`

## Key Components

### Data Layer

#### `SplitFlowDatabase` (Room)

SQLite database for local offline storage of expenses, participants, and transactions.

#### `FirebaseSyncManager`

Handles real-time synchronization with Firestore, including:
- User profile sync
- Group and expense synchronization
- Conflict resolution

### AI/ML Layer

#### `GeminiOcr`

- Sends receipt images to Gemini API
- Parses structured JSON responses
- Extracts items, amounts, and tax information
- Handles errors and retries

#### `MlKitOcr`

- Local text recognition using ML Kit
- Fallback mechanism for offline processing
- No API calls required

### Domain Logic

#### `DebtSimplifier`

- Implements debt simplification algorithm
- Minimizes transaction count while maintaining balance
- Optimizes payment chains
- Handles multi-way settlements

### UI Components

#### `SplitFlowScreens`

Main composable screens including:
- Dashboard / Group list
- Expense management
- Settlement view
- User profiles

#### `CameraScreen`

- Camera integration for receipt capture
- Real-time preview
- Image processing pipeline
- Gallery selection fallback

## Database Schema

### Core Entities

#### `User`

```kotlin
@Entity(tableName = "users")
data class User(
  @PrimaryKey val id: String,
  val name: String,
  val email: String,
  val profileImage: String? = null
)
```

#### `Group`

```kotlin
@Entity(tableName = "groups")
data class Group(
  @PrimaryKey val id: String,
  val name: String,
  val createdBy: String,
  val createdAt: Long,
  val currency: String = "INR"
)
```

#### `Expense`

```kotlin
@Entity(tableName = "expenses")
data class Expense(
  @PrimaryKey val id: String,
  val groupId: String,
  val paidBy: String,
  val amount: Double,
  val description: String,
  val category: String,
  val createdAt: Long,
  val receiptUrl: String? = null
)
```

#### `Split`

```kotlin
@Entity(tableName = "splits")
data class Split(
  @PrimaryKey val id: String,
  val expenseId: String,
  val userId: String,
  val amount: Double,
  val percentage: Double? = null
)
```

## API Integration

### Gemini AI API

**Endpoint**: Google Generative AI REST API

**Usage**:
```kotlin
val geminiOcr = GeminiOcr(apiKey = "YOUR_GEMINI_API_KEY")
val result = geminiOcr.scanReceipt(imageUri)
```

**Response Format**:
```json
{
  "items": [
    { "name": "Item", "amount": 100, "quantity": 1 }
  ],
  "total": 100,
  "tax": 0,
  "date": "2026-09-26"
}
```

### Firebase Firestore

**Collections**:
- `users/{userId}` — User profiles
- `groups/{groupId}` — Group metadata
- `groups/{groupId}/expenses/{expenseId}` — Expenses
- `groups/{groupId}/splits/{splitId}` — Split information

**Real-time Listeners**:
```kotlin
db.collection("groups/${groupId}/expenses")
  .addSnapshotListener { snapshot, error ->
    // Handle real-time updates
  }
```

## Testing

SettleX includes comprehensive test coverage:

### Unit Tests

Located in `app/src/test/`, covering:
- `DebtSimplifier` algorithm
- ViewModel business logic
- Repository operations
- Entity models

Run with:
```bash
./gradlew test
```

### Instrumented Tests

Located in `app/src/androidTest/`, covering:
- UI interactions
- Database operations
- Firebase integration
- Permission handling

Run with:
```bash
./gradlew connectedAndroidTest
```

### Screenshot Tests (Roborazzi)

Visual regression testing for Compose UI:

```bash
./gradlew verifyRoborazziDebug
```

## Deployment

### Pre-Deployment Checklist

- [ ] All tests pass: `./gradlew test connectedAndroidTest`
- [ ] ProGuard/R8 minification tested: `./gradlew assembleRelease`
- [ ] Firebase configuration validated
- [ ] Gemini API key restrictions configured
- [ ] Privacy policy and terms of service prepared
- [ ] App signing key secured and backed up

### Release Build Steps

1. **Increment version**
   ```kotlin
   // in app/build.gradle.kts
   versionCode = 2
   versionName = "1.1"
   ```

2. **Build release APK/AAB**
   ```bash
   ./gradlew bundleRelease
   ```

3. **Sign the artifact**
   - Use your app signing key
   - Ensure keystore is secure

4. **Upload to Play Store**
   - Go to [Google Play Console](https://play.google.com/console)
   - Create a new release
   - Upload AAB file
   - Review and publish

### Firebase Deployment

1. Configure Firestore security rules
2. Set up authentication providers
3. Configure Firebase Storage buckets
4. Enable Firebase Cloud Messaging
5. Set up backup and recovery

## Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/SettleX.git
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Follow Kotlin style guide
   - Write tests for new features
   - Update documentation

4. **Run quality checks**
   ```bash
   ./gradlew lint test connectedAndroidTest
   ```

5. **Commit and push**
   ```bash
   git commit -m "feat: add your feature description"
   git push origin feature/your-feature-name
   ```

6. **Open a Pull Request**
   - Describe changes clearly
   - Link related issues
   - Request review from maintainers

### Code Style

- **Kotlin**: Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Compose**: Follow [Jetpack Compose best practices](https://developer.android.com/jetpack/compose/best-practices)
- **Naming**: Use clear, descriptive names for classes, functions, and variables
- **Comments**: Document complex logic and public APIs

## Troubleshooting

### Common Issues

#### 1. **"Could not connect to Kotlin compile daemon" Error**

**Solution**: The kotlin compiler execution strategy is set to in-process in `gradle.properties`:

```properties
kotlin.compiler.execution.strategy=in-process
```

This is already configured. If issues persist, clean and rebuild:

```bash
./gradlew clean && ./gradlew build
```

#### 2. **Gradle Sync Failures**

**Solution**: 
- Invalidate Gradle cache: `./gradlew --stop`
- Update Gradle: `./gradlew wrapper --gradle-version=LATEST`
- Restart Android Studio

#### 3. **Firebase google-services.json Not Found**

**Solution**: The build is configured to warn instead of fail. Either:
- Download `google-services.json` from Firebase Console, or
- Continue with `googleServices.missing.passthrough=true` in `gradle.properties`

#### 4. **Gemini API Errors**

**Symptoms**: "Invalid API key" or "Quota exceeded"

**Solution**:
- Verify `GEMINI_API_KEY` in `.env`
- Check API quotas in Google Cloud Console
- Ensure API is enabled for your project
- Check rate limiting and implement exponential backoff

#### 5. **Camera Permission Denied**

**Solution**: 
- Grant camera permission at runtime
- Check `AndroidManifest.xml` for required permissions
- Use Accompanist Permissions for modern permission handling

### Debug Commands

```bash
# Print dependency tree
./gradlew app:dependencies

# Run with verbose logging
./gradlew build -v

# Check for lint warnings
./gradlew lint

# Analyze performance
./gradlew build --profile
```

## License

No license has been specified for this repository yet. All rights are reserved by the repository owner. If you plan to fork, modify, or distribute this project, please obtain explicit permission from the owner.

---

**SettleX** — Settle expenses, simplify debts, split with confidence. 💰
