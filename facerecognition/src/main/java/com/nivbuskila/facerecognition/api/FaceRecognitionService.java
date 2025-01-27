package com.nivbuskila.facerecognition.api;

import android.content.Context;
import android.graphics.Bitmap;
import com.nivbuskila.facerecognition.models.FaceRecognitionResult;
import java.util.List;


public interface FaceRecognitionService {
    /**
     * Authenticate with the service
     * @param username username for authentication
     * @param password password for authentication
     * @return Result of authentication
     */
    FaceRecognitionResult authenticate(String username, String password);

    /**
     * Register a new user with their face image
     * @param faceImage The face image to register
     * @param userId Unique identifier for the user
     * @return Result of the registration
     */
    FaceRecognitionResult registerUser(Bitmap faceImage, String userId);

    /**
     * Verify a user's face against their registered image
     * @param faceImage The face image to verify
     * @param userId User identifier to verify against
     * @return Result of the verification
     */
    FaceRecognitionResult verifyUser(Bitmap faceImage, String userId);


    /**
     * Update user's face image
     * @param faceImage The new face image
     * @param userId User identifier to update
     * @return Result of the update
     */
    FaceRecognitionResult updateUser(Bitmap faceImage, String userId);

    FaceRecognitionResult getUsers();

    /**
     * Get user's face image
     * @param userId User identifier
     * @return Result containing the user's face image
     */
    FaceRecognitionResult getUserImage(String userId);

    /**
     * Start face detection activity
     * @param context Context to start the activity
     * @param userId User ID for verification
     */
    void startFaceDetection(Context context, String userId);

    FaceRecognitionResult deleteUser(String userId);

    /**
     * Compare two face images for similarity
     * @param face1 First face image
     * @param face2 Second face image
     * @return Result of the comparison
     */
    FaceRecognitionResult compareFaces(Bitmap face1, Bitmap face2);
}



