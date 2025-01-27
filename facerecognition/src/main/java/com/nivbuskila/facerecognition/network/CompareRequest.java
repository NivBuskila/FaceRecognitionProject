package com.nivbuskila.facerecognition.network;

public class CompareRequest {
    private final String faceData1;
    private final String faceData2;

    public CompareRequest(String image1Base64, String image2Base64) {
        this.faceData1 = image1Base64;
        this.faceData2 = image2Base64;
    }

    public String getFaceData1() {
        return faceData1;
    }

    public String getFaceData2() {
        return faceData2;
    }
}