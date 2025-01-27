package com.nivbuskila.facerecognition.network;

public class VerifyRequest {
    private final String faceData;

    public VerifyRequest(String imageBase64) {
        this.faceData = imageBase64;
    }

    public String getFaceData() {
        return faceData;
    }
}