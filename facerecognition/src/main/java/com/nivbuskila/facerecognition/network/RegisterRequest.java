package com.nivbuskila.facerecognition.network;

public class RegisterRequest {
    private final String userId;
    private final String faceData;

    public RegisterRequest(String userId, String imageBase64) {
        this.userId = userId;
        this.faceData = imageBase64;
    }

    public String getUserId() {
        return userId;
    }

    public String getFaceData() {
        return faceData;
    }
}