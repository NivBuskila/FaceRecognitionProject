# Face Recognition Project

Face Recognition library for Android applications with a demo application showcasing its usage.

## GitHub Pages

Live documentation available at [https://nivbuskila.github.io/FaceRecognitionProject/](https://nivbuskila.github.io/FaceRecognitionProject/)

## Overview

This project consists of three main components:
1. An Android library (`facerecognition`) that provides face recognition capabilities
2. A backend service powered by AWS Rekognition
3. A sample Android application (`app`) demonstrating the library's usage

## Features

### Face Recognition Library
- Real-time face detection and recognition
- Face template management
- User verification capabilities
- Support for multiple faces in a single frame
- Secure storage of biometric data

### Sample Application
- Camera integration for face capture
- Real-time face detection demo
- User registration and verification
- Example of secure data handling

## Getting Started

### Prerequisites

#### Android Requirements
- Android Studio Arctic Fox or newer
- Android SDK 21 or higher
- Camera-enabled Android device/emulator

#### Backend Requirements
- Python 3.8+
- MongoDB 4.4+
- AWS account with Rekognition access
- Redis 6.0+ (optional, for caching)

### Installation

1. Add JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:
```gradle
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

2. Add the dependency:
```gradle
dependencies {
    implementation 'com.github.NivBuskila:FaceRecognitionProject:1.0.0'
}
```

### Usage

Basic usage example:

```kotlin
// Initialize the service
val faceRecognitionService = FaceRecognitionService.getInstance(context)

// Register a user
faceRecognitionService.registerUser(bitmap, userId) { result ->
    when (result) {
        is Success -> // Handle success
        is Error -> // Handle error
    }
}

// Verify a user
faceRecognitionService.verifyUser(bitmap, userId) { result ->
    when (result) {
        is Success -> // Handle success
        is Error -> // Handle error
    }
}
```

For more detailed examples, please check the sample application in the `app` module.

## Documentation

For detailed documentation, including API reference and implementation guides, please visit our [GitHub Pages](https://NivBuskila.github.io/FaceRecognitionProject/).

## Security

This project implements several security measures:
- JWT-based authentication
- Encrypted data transmission
- Secure credential storage
- AWS IAM role-based access control

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License

## Acknowledgments

- Thanks to Google ML Kit for face detection capabilities
- OpenCV for image processing functionalities
- CameraX for camera implementation
- AWS Rekognition for face recognition services
- MongoDB for secure data storage