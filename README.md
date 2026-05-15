<div align="center">

<img src="https://img.shields.io/badge/NK-Namma%20Kelsa-FF6B35?style=for-the-badge&labelColor=1A1A1A" alt="Namma Kelsa"/>

# Namma Kelsa 🔧

### *Dignity-First Labor Marketplace for Karnataka*

**A Android app that connects skilled daily wage workers directly with homeowners — powered by Firebase and Google Gemini AI.**
<img width="245" height="569" alt="image" src="https://github.com/user-attachments/assets/b8bbe426-dc9c-46a1-b154-e16fc71c5946" />
<img width="254" height="564" alt="image" src="https://github.com/user-attachments/assets/e95510fe-f9c3-4f8e-8e76-62a3600c4d93" />
<img width="252" height="564" alt="image" src="https://github.com/user-attachments/assets/ec6db2d2-329d-495c-b9ab-48c090265180" />
<img width="245" height="569" alt="image" src="https://github.com/user-attachments/assets/a77aaaea-c590-4129-a75d-72d8a106d567" />
<img width="255" height="567" alt="image" src="https://github.com/user-attachments/assets/af1fc066-bc28-4335-8689-8304b891feed" />


<br/>

[📥 Download APK](https://github.com/paanchjanya/NammaKelsa/releases/latest) · [🐛 Report Bug](https://github.com/paanchjanya/NammaKelsa/issues) · [💡 Request Feature](https://github.com/paanchjanya/NammaKelsa/issues)

</div>

---

## 📖 Table of Contents

- [About the Project](#-about-the-project)
- [The Problem We Solve](#-the-problem-we-solve)
- [Features](#-features)
- [Screenshots](#-screenshots)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Firebase Setup](#firebase-setup)
  - [Gemini AI Setup](#gemini-ai-setup)
- [How to Use](#-how-to-use)
  - [As a Worker](#as-a-worker)
  - [As a Customer](#as-a-customer)
- [GenAI Features](#-genai-features)
- [Data Model](#-data-model)
- [Contributing](#-contributing)
- [Author](#-author)

---

## 🌟 About the Project

**Namma Kelsa** (meaning *"Our Work"* in Kannada) is a mobile-first Android application that digitally empowers India's informal skilled labor workforce.

Daily wage workers — Painters, Plumbers, Electricians, Tilers, Carpenters, Gardeners, and Masons — are the backbone of household maintenance across India. Yet they remain **digitally invisible**, dependent on exploitative contractor networks, and unable to build a professional reputation.

Namma Kelsa changes that by giving every worker a **verified digital identity**, a **real-time availability signal**, and a **direct line to customers** — with zero middlemen.

> *"Skill Has Always Been There. Now, So Is the Spotlight."*

---

## 🎯 The Problem We Solve

| Problem | Impact | Namma Kelsa Solution |
|---|---|---|
| No digital presence | Workers undetectable beyond 1-2km | GPS-based searchable profiles |
| Middlemen exploitation | 30-50% commission lost | Direct Call button — zero commission |
| Unsteady employment | 2-4 hrs/day searching for work | Real-time availability toggle |
| No skill verification | Cannot justify fair wages | Work gallery + AI-generated bio |
| Language barriers | English-only platforms exclude workers | AI bio in English + Kannada |

---

## ✨ Features

### 👷 For Workers
- **Digital Profile** — Photo, skill type, daily rate, and location
- **Work Gallery** — Upload up to 3 photos of recent work
- **Real-Time Availability Toggle** — Go online/offline instantly
- **AI Worker Bio** — Auto-generate professional bio in English + Kannada using Gemini 2.0
- **AI Skill Suggester** — Describe your work in plain words; AI selects the right skill category
- **Logout** — Secure session management

### 🏠 For Customers
- **Find Workers** — Browse all available workers by skill and location
- **Skill Filter** — Filter by Painter, Plumber, Electrician, Tiler, Carpenter, Gardener, Mason
- **Distance Filter** — Any / 2km / 5km / 10km radius using GPS
- **Worker Detail Screen** — Profile photo, skill badge, rate, gallery, AI bio, and distance
- **Direct Call** — One tap to call a worker — no middlemen, no commission
- **Real-Time Results** — Search updates live as workers toggle availability

### 🤖 AI-Powered (Google Gemini 2.0 Flash)
- Bilingual professional bio generation (English + Kannada)
- Natural language skill categorisation
- Fallback copy when AI is unavailable

---

## 📱 Screenshots

> Add screenshots of your app here after taking them from your device.

| Role Selection | Login | Worker Profile |
|---|---|---|
| *(screenshot)* | *(screenshot)* | *(screenshot)* |

| Customer Search | Worker Detail | AI Suggester |
|---|---|---|
| *(screenshot)* | *(screenshot)* | *(screenshot)* |

---

## 🛠 Tech Stack

### Core
| Technology | Version | Purpose |
|---|---|---|
| **Kotlin** | 1.9+ | Primary development language |
| **Android SDK** | Min 24 / Target 35 | Platform |
| **XML + ViewBinding** | — | UI layouts |
| **Material Design 3** | 1.12.0 | UI components |

### Firebase
| Service | Purpose |
|---|---|
| **Firebase Authentication** | Email/password worker login & registration |
| **Cloud Firestore** | Real-time worker profiles & availability (Native Mode, asia-south1) |
| **Firebase Storage** | Profile photos & work gallery images |

### AI / Location
| Technology | Purpose |
|---|---|
| **Google Gemini 2.0 Flash** | Worker bio generation + skill categorisation |
| **Fused Location Provider API** | GPS coordinate capture |
| **Haversine Formula** | Great-circle distance calculation between worker and customer |

### UI Libraries
| Library | Version | Purpose |
|---|---|---|
| **Glide** | 4.16.0 | Efficient image loading with circleCrop |
| **ShapeableImageView** | Material | Circular profile photos |
| **ChipGroup** | Material | Skill selection and distance filters |
| **SwitchMaterial** | Material | Real-time availability toggle |
| **BottomNavigationView** | Material | Customer screen navigation |

### Build
| Tool | Detail |
|---|---|
| **Gradle Kotlin DSL** | Build configuration |
| **BuildConfig** | Secure API key injection |
| **ViewBinding** | Type-safe view access |

---

## 📁 Project Structure

```
app/src/main/
├── java/com/example/nammakelsa/
│   ├── model/
│   │   └── Worker.kt                  # Data class — Firestore document model
│   ├── ui/
│   │   ├── RoleSelectionActivity.kt   # Entry screen — Worker or Customer
│   │   ├── auth/
│   │   │   ├── LoginActivity.kt       # Worker login
│   │   │   └── RegisterActivity.kt    # Worker registration
│   │   ├── worker/
│   │   │   ├── WorkerProfileActivity.kt  # Profile form, AI bio, availability
│   │   │   └── WorkerDetailActivity.kt   # Worker detail view for customers
│   │   └── customer/
│   │       ├── CustomerSearchActivity.kt # Search, filter, distance
│   │       ├── WorkerAdapter.kt          # RecyclerView adapter for worker cards
│   │       └── AiSkillSuggesterActivity.kt # Dedicated AI suggester screen
│   └── utils/
│       ├── GeminiHelper.kt            # Gemini AI — bio + skill suggestion
│       ├── LocationHelper.kt          # GPS + Haversine distance
│       └── Constants.kt               # Skill list, Firestore collection names
├── res/
│   ├── layout/                        # XML screen layouts
│   ├── menu/                          # Bottom navigation menu
│   ├── color/                         # Chip and switch color selectors
│   ├── drawable/                      # Skill badge background, logo shapes
│   └── values/
│       ├── colors.xml                 # Brand color system
│       └── strings.xml                # App strings
└── AndroidManifest.xml                # Permissions and activity declarations
```

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:

- [Android Studio](https://developer.android.com/studio) (Hedgehog or newer)
- JDK 11 or higher
- Android device or emulator running API 24+
- A Google account (for Firebase and Gemini)

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/paanchjanya/NammaKelsa.git
cd NammaKelsa
```

**2. Open in Android Studio**

```
File → Open → Select the NammaKelsa folder → OK
```

**3. Wait for Gradle sync to complete**

---

### Firebase Setup

**Step 1 — Create a Firebase project**

1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Click **Add Project** → Name it `NammaKelsa`
3. Click **Continue** → Disable Google Analytics → **Create Project**

**Step 2 — Add your Android app**

1. Click the Android icon in your Firebase project
2. Enter package name: `com.example.nammakelsa`
3. Download the `google-services.json` file
4. Place it in the `app/` folder of your project

**Step 3 — Enable Firebase services**

| Service | Steps |
|---|---|
| **Authentication** | Build → Authentication → Get Started → Enable Email/Password |
| **Firestore** | Build → Firestore Database → Create Database → **Native Mode** → `asia-south1` |
| **Storage** | Build → Storage → Get Started → Test mode |

**Step 4 — Firestore Security Rules** *(for development)*

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /workers/{workerId} {
      allow read: if true;
      allow write: if request.auth != null
                   && request.auth.uid == workerId;
    }
  }
}
```

**Step 5 — Storage Security Rules** *(for development)*

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

---

### Gemini AI Setup

**Step 1 — Get your API key**

1. Go to [aistudio.google.com](https://aistudio.google.com)
2. Click **Get API Key** → **Create API Key**
3. Copy the key (starts with `AIzaSy...`)

**Step 2 — Add to local.properties**

Open `local.properties` in the project root (create it if it doesn't exist) and add:

```properties
GEMINI_API_KEY=AIzaSy...your_actual_key_here
```

> ⚠️ Never commit `local.properties` to Git — it is already listed in `.gitignore`

**Step 3 — Verify build.gradle.kts**

Confirm these lines exist in `app/build.gradle.kts`:

```kotlin
import java.util.Properties
val properties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

android {
    defaultConfig {
        buildConfigField("String", "GEMINI_API_KEY",
            "\"${properties["GEMINI_API_KEY"]}\"")
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}
```

**Step 4 — Build and Run**

```
Build → Make Project (Ctrl+F9) → Run (Shift+F10)
```

---

## 📲 How to Use

### As a Worker

```
1. Open app → tap "I'm a Worker"
2. New worker? → tap "Create account" → fill name, phone, email, password
3. Existing worker? → enter email and password → tap "Sign In"
4. Fill your profile:
   ├── Pick a profile photo
   ├── Enter full name, phone, daily rate (₹), and your area
   ├── Select your skill (Painter / Plumber / Electrician / etc.)
   ├── Upload up to 3 work gallery photos
   └── Tap "Generate" for an AI bio in English + Kannada
5. Tap "Save Profile" → allow location permission
6. Toggle "Available for Work Today" → you are now visible to customers!
7. Customers in your area will see your card and can call you directly
```

### As a Customer

```
1. Open app → tap "I'm a Customer" (no account needed)
2. Allow location permission when prompted
3. Browse available workers in your area
4. Filter by skill:
   └── Tap chips → All / Painter / Plumber / Electrician / Tiler / Carpenter / Gardener / Mason
5. Filter by distance:
   └── Any / 2 km / 5 km / 10 km
6. Tap a worker card → view full profile, work gallery, and AI bio
7. Tap "📞 Call Worker" → phone dialer opens with worker's number
8. Hire directly — no middlemen, no commission
```

---

## 🤖 GenAI Features

### AI Worker Bio

Generates a 2-3 sentence professional biography in **both English and Kannada**
based on the worker's name, skill, daily rate, and location.

**Example output:**

```
[English]
Raju is a trusted and skilled Painter based in Koramangala,
bringing years of hands-on experience to every home project.
Known for quality work and punctuality, he offers his services
at just ₹450 for a full day's work.

[ಕನ್ನಡ]
ರಾಜು ಅವರು ಕೊರಮಂಗಲದಲ್ಲಿ ಕಾರ್ಯನಿರ್ವಹಿಸುವ ನಂಬಕಾರ್ಹ Painter
ಆಗಿದ್ದಾರೆ. ಗುಣಮಟ್ಟ ಮತ್ತು ಸಮಯಪಾಲನೆಗೆ ಬದ್ಧರಾಗಿರುವ ಇವರು
ದಿನಕ್ಕೆ ₹450 ರಲ್ಲಿ ಸೇವೆ ನೀಡುತ್ತಾರೆ.
```

### AI Skill Suggester

Worker describes their work in plain language → Gemini analyses → returns the correct skill category → auto-selects the matching chip.

**Example:**

```
Input:  "I fix water pipes, taps, and drainage in homes"
Output: ✅ Suggested Skill: Plumber
```

**Supported skill categories:**
`Painter` · `Plumber` · `Electrician` · `Tiler` · `Carpenter` · `Gardener` · `Mason`

---

## 🗄 Data Model

### Worker Document (Firestore)

```kotlin
data class Worker(
    val uid: String = "",           // Firebase Auth UID (document key)
    val name: String = "",          // Full name
    val phone: String = "",         // 10-digit mobile number
    val skillType: String = "",     // One of the 7 skill categories
    val dailyRate: Int = 0,         // Rate in Indian Rupees per day
    val locationName: String = "",  // Locality / area name
    val latitude: Double = 0.0,     // GPS latitude (India range: 8–37)
    val longitude: Double = 0.0,    // GPS longitude (India range: 68–97)
    val profilePhotoUrl: String = "",       // Firebase Storage URL
    val workPhotoUrls: List<String> = emptyList(), // Up to 3 Storage URLs
    val isAvailable: Boolean = false        // Real-time availability flag
)
```

### Firebase Storage Structure

```
profile_photos/
└── {uid}.jpg                    ← Single profile photo per worker

work_photos/
└── {uid}/
    ├── 0.jpg                    ← Work gallery photo 1
    ├── 1.jpg                    ← Work gallery photo 2
    └── 2.jpg                    ← Work gallery photo 3
```

---

## 🔐 Permissions Required

| Permission | When Requested | Purpose |
|---|---|---|
| `ACCESS_FINE_LOCATION` | On profile save (worker) / search open (customer) | GPS coordinates for proximity search |
| `ACCESS_COARSE_LOCATION` | Same as above | Fallback location |
| `READ_MEDIA_IMAGES` | When picking photos | Profile and work gallery uploads |
| `INTERNET` | Always | Firebase and Gemini API communication |
| `CALL_PHONE` | On Call button tap | Initiating direct call to worker |

---

## 📊 Success Criteria

All three criteria from the original project brief are met:

- ✅ The **Availability Switch** updates search results instantly (Firestore real-time listener)
- ✅ The worker profile has a **Call button** that works with the phone's dialer
- ✅ The UI is **simple enough** for a worker to manage their own profile

**Bonus features delivered beyond the brief:**
- ✅ GPS + Haversine distance filtering (2km / 5km / 10km)
- ✅ Gemini 2.0 AI bio in English **and Kannada**
- ✅ AI Skill Suggester from natural language description
- ✅ Dedicated AI Suggester screen with "Why use AI?" section
- ✅ Premium minimalist UI inspired by Apple's Human Interface Guidelines

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the project
2. Create your feature branch: `git checkout -b feature/AmazingFeature`
3. Commit your changes: `git commit -m 'Add some AmazingFeature'`
4. Push to the branch: `git push origin feature/AmazingFeature`
5. Open a Pull Request

---

## 🗺 Roadmap

- [ ] Connect customer search text field to live filtering
- [ ] Implement Firebase password reset email
- [ ] Production Firestore security rules
- [ ] Save AI bio to Firestore worker document
- [ ] Worker ratings and reviews
- [ ] Full Kannada UI localisation
- [ ] Push notifications for nearby job requests
- [ ] Admin moderation dashboard
- [ ] Google Play Store release

---

## 👤 Author

**Praveen Desai**

- USN: 2BL22CS128
- BLDEA's V P Dr P G Halakatti College of Engineering and Technology, Vijayapura Karnataka - 586103
- Internship: Mindmatrix

<img width="616" height="609" alt="image" src="https://github.com/user-attachments/assets/856bbe07-db27-4362-be9a-cd0d8790c1bb" />



---



<div align="center">

**Namma Kelsa** — Connecting Karnataka's Workforce, One Tap at a Time.

*Dignity · Work · Pride*

⭐ Star this repo if you found it useful!

</div>
