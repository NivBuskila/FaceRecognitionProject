package com.nivbuskila.facerecognition.auth;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }
}