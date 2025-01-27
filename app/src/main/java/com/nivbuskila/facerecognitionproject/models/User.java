package com.nivbuskila.facerecognitionproject.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("userId")
    private String userId;

    @SerializedName("created_at")
    private String createdAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}