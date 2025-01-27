package com.nivbuskila.facerecognition.network;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("verified")
    private boolean verified;

    @SerializedName("confidence")
    private float confidence;

    @SerializedName("userId")
    private String userId;

    @SerializedName("faceData")
    private String faceData;

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    @SerializedName("_id")
    private String id;

    public boolean isVerified() {
        return verified;
    }

    public float getConfidence() {
        return confidence;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
    public String getFaceData() {
        return faceData;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return error == null;
    }
    public String getId() {
        return id;
    }
    @SerializedName("created_at")
    private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }
}