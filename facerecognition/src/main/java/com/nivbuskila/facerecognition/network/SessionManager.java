package com.nivbuskila.facerecognition.network;

import android.util.Log;

public class SessionManager {
    private static final String TAG = "SessionManager";
    private static SessionManager instance;
    private String token;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setToken(String token) {
        this.token = token;
        Log.d(TAG, "Token set: " + (token != null ? token.substring(0, 10) + "..." : "null"));
    }

    public String getToken() {
        return token;
    }

    public boolean hasToken() {
        return token != null && !token.isEmpty();
    }
}