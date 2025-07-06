![Launch journey(Android Studio)](https://github.com/user-attachments/assets/566251d6-b586-4799-ac93-56b9172fd2db)

# Launch Journey - Android Studio Ticket Booking App

**Launch Journey** is a typical, functioning Android application for booking River Cruise / Boat tickets(In Bangladesh these vessels are known as Launch). It provides a fare, secure, and visually engaging experience, supporting user registration, authentication, ticket booking, and profile management, all directly from your Android device.

---

## Technologies & Libraries Used

- **Java**: Complete logic and UI built in Java.
- **Android Studio**: Official IDE for design, development, and debugging.
- **Firebase Firestore**: Cloud-based storage for user and ticket data.
- **Firebase Realtime Database**: Used for some backend operations and fast UID storage.
- **Firebase Authentication**: Secure user sign-up, sign-in, and management.
- **BCrypt (`org.mindrot.jbcrypt.BCrypt`)**: Passwords are securely hashed with BCrypt before storage.
- **Google Services**: Integrated via `google-services.json` for push notifications and cloud features.
- **Glide**: Efficient image loading and caching, especially for profile pictures.
- **Custom Fonts**: For a unique and modern app appearance(MiSans).
- **Material Components**: Modern Android UI using Material Design.
- **XML**: Responsive and interactive layouts.
- **Gradle**: Automated build, dependency, and version management.

---
## Compatibility & Build Configuration

- **Namespace**: `com.example.launchjourney`
- **Compile SDK Version**: 34
- **Target SDK**: 34
- **Minimum SDK**: 24 (Android 7.0+)
- **Java Compatibility**: Source & Target Compatibility set to Java 11
- **View Binding**: Enabled for safer and easier view access
- **Proguard**: Proguard is set up (minification disabled in release build)
- **Test Runner**: AndroidJUnitRunner

### Plugins Used

- `com.android.application`
- `com.google.gms.google-services`

### Key Dependencies

- Material Design: `com.google.android.material:material`
- Glide: `com.github.bumptech.glide:glide` & annotation processor
- Firebase (BOM): Authentication, Firestore, Analytics, Storage
- Google Auth Services: `com.google.android.gms:play-services-auth`
- BCrypt: `org.mindrot:jbcrypt`
- AndroidX: AppCompat, ConstraintLayout, Activity, etc.

---
## Features

- **Secure Registration & Login**: Uses Firebase Auth and BCrypt password hashing (Forget password is not implemented).
- **Ticket Booking**: Search, select, and book tickets for various launch journeys.
- **Booking History**: View and manage past and upcoming bookings.
- **Profile Management**: Update personal info, upload profile image (Couldn't implement Profile Picture user setting, cause Firebase storage service is paid).
- **Modern UI**: Material Design, custom fonts, and smooth animation and premium feel, no sluggish-ness.
- **Input Validation & OTP**: Ensures accurate user input and optional OTP-based verification (It's not implemented yet, cause Firebase stopped the service for FREE OTP verification).

---
## Getting Started

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync Gradle and let dependencies resolve.
4. Build and run on any Android device (Android 7.0+, API 24+).
5. [Download APK](https://www.mediafire.com/file/j4wf3dgkoggefpa/LaunchJourney.apk/file) if you prefer direct install.

---


![IMG_20250625_195741](https://github.com/user-attachments/assets/ef5af08f-d66d-4cd1-9cb0-30c17a0e5ac0)



