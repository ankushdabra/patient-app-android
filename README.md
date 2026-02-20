# 🏥 VitalSync

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-1.5+-4285F4?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Material_3-Modern-006A6A?logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-FF8C00)](https://developer.android.com/topic/architecture)

**VitalSync** is a premium, high-performance Android application designed to bridge the gap between patients and healthcare providers. Engineered with a focus on user experience and robust architecture, it provides a seamless end-to-end journey from discovering specialist doctors to managing medical prescriptions.

---

## 📸 App Walkthrough

Explore the intuitive and polished user interface of VitalSync.

### 🔐 Secure Onboarding
Seamless authentication and registration flow.
| Login | Registration |
| :---: | :---: |
| <img src="images/Login_VitalSync.jpg" width="300"> | <img src="images/Signup_VitalSync.jpg" width="300"> |

### 🩺 Find & Book Specialists
Discovery engine with real-time availability.
| Doctors Discovery | Book Appointment |
| :---: | :---: |
| <img src="images/Doctors_VitalSync.jpg" width="300"> | <img src="images/Book_Appointment_VitalSync.jpg" width="300"> |

### 📅 Manage Your Consultations
Stay updated with your healthcare schedule.
| My Appointments | Appointment Detail |
| :---: | :---: |
| <img src="images/My_Appointments_VitalSync.jpg" width="300"> | <img src="images/Appointment_Detail_VitalSync.jpg" width="300"> |

### 📜 Digital Health Records
Quick access to prescriptions and profile management.
| Prescriptions | Prescription Detail | Profile |
| :---: | :---: | :---: |
| <img src="images/Prescriptions_VitalSync.jpg" width="250"> | <img src="images/Prescription_Detail_VitalSync.jpg" width="250"> | <img src="images/Profile_VitalSync.jpg" width="250"> |

---

## ✨ Key Features

- **👨‍⚕️ Intelligent Doctor Discovery**: Multi-parameter search for specialists with detailed insights into their qualifications, clinic locations, and patient ratings.
- **📅 Dynamic Slot Booking**: A sophisticated calendar-based booking system that maps day codes to actual dates, ensuring error-free scheduling.
- **🗓️ Comprehensive Management**: End-to-end tracking of appointment lifecycles—from initial booking to "Arrived" and "Completed" statuses.
- **📄 Smart Prescriptions**: Digital repository for all doctor recommendations, enabling patients to track their treatment history effortlessly.
- **🔐 Enterprise-Grade Security**: Secured with JWT-based token management and encrypted local storage for sensitive patient data.
- **🌓 Adaptive Theming**: A beautiful Material 3 design system that natively supports Light and Dark modes with custom medical-grade color palettes.

---

## 🚀 Technical Excellence

### Architecture & Patterns
- **MVVM Pattern**: Separation of concerns between UI, Business Logic, and Data.
- **Clean Architecture Principles**: Modularized package structure for high testability and maintainability.
- **Unidirectional Data Flow**: Leveraging `StateFlow` and `collectAsStateWithLifecycle` for predictable UI states.

### Core Tech Stack
- **UI Framework**: Jetpack Compose (Declarative UI)
- **Networking**: Retrofit 2 + OkHttp 4 (REST API integration)
- **Asynchrony**: Kotlin Coroutines (Structured concurrency)
- **Dependency Management**: Version Catalogs (TOML) for centralized dependency control.
- **Design System**: Material 3 with custom-built healthcare components.

---

## 📂 Modular Structure

```text
com.patient.app
├── 🔑 login/auth      # State-of-the-art authentication & registration logic
├── 🏠 dashboard       # Main application scaffold & Bottom navigation
├── 🩺 doctors         # Advanced doctor listing & real-time detail lookup
├── 📅 appointments    # Core booking engine & consultation tracking
├── 📜 prescriptions   # Prescription visualization & history management
├── 🧭 navigation      # Centralized Type-Safe routing system
├── 🌐 network         # Scalable Retrofit services & Data Transfer Objects
└── 🛠️ core            # Foundation: Theming, Shared Components, & Utils
```

---

## 🛠️ Setup & Installation

### 1. Prerequisites
- **Android Studio**: Ladybug (2024.2.1) or newer.
- **JDK**: Java 17+
- **Gradle**: 8.5+

### 2. Configuration
The app targets a backend server. By default, it uses the Android Emulator loopback:
- Update `Constants.kt` or your environment config to point to: `http://10.0.2.2:8080` (Local) or your production URL.

### 3. Build
1. Clone the repository.
2. Run `Sync Project with Gradle Files` in Android Studio.
3. Deploy to an emulator (API 34+) or physical device.

---

Developed with ❤️ by the **VitalSync Team**.
