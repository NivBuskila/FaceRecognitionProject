# Face Recognition Project

[![](https://jitpack.io/v/NivBuskila/FaceRecognitionProject.svg)](https://jitpack.io/#NivBuskila/FaceRecognitionProject)

An Android library providing face recognition capabilities with a demonstration application. The project facilitates face detection, user registration, verification, and comparison functionalities through a clean, modular architecture.

## Components

The project consists of two main modules:

1. Face Recognition Library (`facerecognition`): A standalone Android library that provides face recognition capabilities through ML Kit integration and a REST API interface
2. Demo Application (`app`): An Android application showcasing the library's features and implementation

## Features

The face recognition library provides several core functionalities:

- User authentication and session management
- Face detection using ML Kit
- Face registration and verification
- Face comparison between two images
- User management (create, update, delete)
- Secure credential storage using EncryptedSharedPreferences

## Requirements

- Android SDK 23 (Android 6.0) or higher
- Camera-enabled Android device
- Internet connection for API communication

## Installation

Add JitPack repository to your project-level `settings.gradle`:

```gradle
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your module-level `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.NivBuskila:FaceRecognitionProject:1.0.0'
}
```

## Usage

Here's a basic example of using the library:

```java
// Initialize FaceRecognition
FaceRecognition faceRecognition = new FaceRecognition(context);

// Authenticate
faceRecognition.authenticate(username, password);

// Register a user
faceRecognition.registerUser(faceImage, userId);

// Verify a user
faceRecognition.verifyUser(faceImage, userId);

// Compare two faces
faceRecognition.compareFaces(face1Image, face2Image);
```

## Permissions

The library requires the following permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

## Dependencies

The project utilizes several key Android libraries:

- ML Kit Face Detection for face detection capabilities
- CameraX for camera implementation
- Retrofit for network operations
- Security Crypto for secure credential storage
- Navigation Components for app navigation
- Material Design Components for UI elements

## License

This project is available under the MIT License.