package com.nivbuskila.facerecognition.models;

import com.nivbuskila.facerecognition.network.ApiResponse;

import java.util.List;

public class FaceRecognitionResult {
    private final boolean success;
    private final String userId;
    private final String error;
    private final float confidence;
    private final List<ApiResponse> users;
    private final String faceData;

    public FaceRecognitionResult(boolean success, String userId, float confidence,
                                 String error, List<ApiResponse> users, String faceData) {
        this.success = success;
        this.userId = userId;
        this.confidence = confidence;
        this.error = error;
        this.users = users;
        this.faceData = faceData;
    }


    public boolean isSuccess() { return success; }
    public String getUserId() { return userId; }
    public String getError() { return error; }
    public float getConfidence() { return confidence; }

    public List<ApiResponse> getUsers() { return users; }
    public String getFaceData() { return faceData; }

    public static class Builder {
        private boolean success;
        private String userId;
        private String error;
        private float confidence;
        private List<ApiResponse> users;
        private String faceData;

        public Builder setSuccess(boolean success) {
            this.success = success;
            return this;
        }


        public Builder setFaceData(String faceData) {
            this.faceData = faceData;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setError(String error) {
            this.error = error;
            return this;
        }

        public Builder setConfidence(float confidence) {
            this.confidence = confidence;
            return this;
        }


        public Builder setUsers(List<ApiResponse> users) {
            this.users = users;
            return this;
        }

        public FaceRecognitionResult build() {
            return new FaceRecognitionResult(success, userId, confidence, error, users, faceData);
        }
    }
}