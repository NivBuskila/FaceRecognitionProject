# Face Recognition System Documentation

## Overview

The Face Recognition System is a comprehensive solution that provides face detection, recognition, and verification capabilities for Android applications. The system consists of three main components:

1. **Backend API Service**: A Flask-based REST API that handles face recognition operations using AWS Rekognition
2. **Android SDK**: A library that provides easy integration of face recognition features into Android applications
3. **Example Application**: A demonstration app showcasing the SDK's capabilities

## Quick Start Guide

### 1. Add the SDK to Your Project

Add JitPack repository to your root build.gradle:

```gradle
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency:

```gradle
dependencies {
    implementation("com.github.NivBuskila:face-recognition:1.0.0")
}
```

### 2. Initialize the SDK

```java
FaceRecognition faceRecognition = new FaceRecognition(context);
```

### 3. Authenticate

```java
FaceRecognitionResult result = faceRecognition.authenticate(username, password);
if (result.isSuccess()) {
    // Authentication successful
}
```

## Core Features

### User Registration

Register a new user with their face image:

```java
Bitmap faceImage = // ... capture or load face image
String userId = "user123";

FaceRecognitionResult result = faceRecognition.registerUser(faceImage, userId);
if (result.isSuccess()) {
    // User registered successfully
}
```

### Face Verification

Verify a user's identity using their face:

```java
Bitmap faceImage = // ... capture face image
String userId = "user123";

FaceRecognitionResult result = faceRecognition.verifyUser(faceImage, userId);
if (result.isSuccess()) {
    float confidence = result.getConfidence();
    // User verified successfully
}
```

### Face Comparison

Compare two face images:

```java
Bitmap face1 = // ... first face image
Bitmap face2 = // ... second face image

FaceRecognitionResult result = faceRecognition.compareFaces(face1, face2);
if (result.isSuccess()) {
    float similarity = result.getConfidence();
    // Faces compared successfully
}
```

## Backend API Documentation

The backend service provides RESTful APIs for face recognition operations. Base URL: `https://face-recognition-api-orcin.vercel.app/`

### Authentication

#### Login
- **POST** `/api/auth/login`
- **Body**:
  ```json
  {
    "username": "admin",
    "password": "password"
  }
  ```
- **Response**:
  ```json
  {
    "token": "jwt_token_here"
  }
  ```

### User Management

#### Register User
- **POST** `/api/users`
- **Headers**: `Authorization: Bearer <token>`
- **Body**:
  ```json
  {
    "userId": "user123",
    "faceData": "base64_encoded_image"
  }
  ```

#### Verify User
- **POST** `/api/users/{userId}/verify`
- **Headers**: `Authorization: Bearer <token>`
- **Body**:
  ```json
  {
    "faceData": "base64_encoded_image"
  }
  ```

#### Get User Image
- **GET** `/api/users/{userId}/image`
- **Headers**: `Authorization: Bearer <token>`

#### Update User
- **PUT** `/api/users/{userId}`
- **Headers**: `Authorization: Bearer <token>`
- **Body**:
  ```json
  {
    "faceData": "base64_encoded_image"
  }
  ```

#### Delete User
- **DELETE** `/api/users/{userId}`
- **Headers**: `Authorization: Bearer <token>`

### Face Operations

#### Compare Faces
- **POST** `/api/faces/compare`
- **Headers**: `Authorization: Bearer <token>`
- **Body**:
  ```json
  {
    "faceData1": "base64_encoded_image1",
    "faceData2": "base64_encoded_image2"
  }
  ```

## SDK Architecture

The SDK follows a clean architecture pattern with these main components:

1. **FaceRecognitionService**: Main interface defining all operations
2. **FaceRecognition**: Implementation of the service interface
3. **Models**: Data classes for results and responses
4. **Network**: API communication handling

### Key Components

#### FaceRecognitionService Interface
```java
public interface FaceRecognitionService {
    FaceRecognitionResult authenticate(String username, String password);
    FaceRecognitionResult registerUser(Bitmap faceImage, String userId);
    FaceRecognitionResult verifyUser(Bitmap faceImage, String userId);
    FaceRecognitionResult updateUser(Bitmap faceImage, String userId);
    FaceRecognitionResult getUserImage(String userId);
    FaceRecognitionResult compareFaces(Bitmap face1, Bitmap face2);
    void startFaceDetection(Context context, String userId);
}
```

#### FaceRecognitionResult
```java
public class FaceRecognitionResult {
    private final boolean success;
    private final String userId;
    private final String error;
    private final float confidence;
    private final String faceData;
    // ... getters and builder pattern implementation
}
```

## Security Considerations

1. **API Authentication**: JWT-based authentication
2. **Data Encryption**: All face data is transmitted using base64 encoding
3. **AWS Security**: AWS Rekognition access controlled via IAM roles
4. **Secure Storage**: Encrypted SharedPreferences for credential storage

## Example Application

The example application demonstrates all SDK features:

1. User Management
    - Registration
    - Verification
    - Profile updates
2. Face Comparison
3. Real-time Face Detection
4. User Administration

### Screenshots

[Add screenshots here]

## Requirements

### Android SDK
- Minimum SDK: API 23 (Android 6.0)
- CameraX support
- Internet permission
- Camera permission

### Backend Service
- Python 3.8+
- MongoDB 4.4+
- AWS Rekognition access
- Redis 6.0+ (optional, for caching)

## Error Handling

The SDK provides detailed error information through the FaceRecognitionResult class:

```java
if (!result.isSuccess()) {
    String errorMessage = result.getError();
    // Handle error appropriately
}
```

Common error codes:
- 401: Authentication failed
- 400: Invalid input
- 404: User not found
- 409: User already exists

## Best Practices

1. **Image Quality**
    - Use well-lit, clear face images
    - Ensure face is centered and visible
    - Recommended image size: 640x480 pixels minimum

2. **Performance**
    - Process images in background threads
    - Cache authentication tokens
    - Implement proper error handling

3. **User Experience**
    - Provide clear feedback during operations
    - Implement proper loading states
    - Handle permission requests gracefully

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Support

For issues and feature requests, please visit our [GitHub repository](https://github.com/NivBuskila/FaceRecognitionProject).

---

© 2025 Face Recognition System. All rights reserved.