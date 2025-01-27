package com.nivbuskila.facerecognition;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.nivbuskila.facerecognition.activities.FaceDetectionActivity;
import com.nivbuskila.facerecognition.api.FaceRecognitionService;
import com.nivbuskila.facerecognition.models.FaceRecognitionResult;
import com.nivbuskila.facerecognition.network.ApiResponse;
import com.nivbuskila.facerecognition.network.ApiService;
import com.nivbuskila.facerecognition.network.CompareRequest;
import com.nivbuskila.facerecognition.network.RegisterRequest;
import com.nivbuskila.facerecognition.network.SessionManager;
import com.nivbuskila.facerecognition.network.VerifyRequest;
import com.nivbuskila.facerecognition.auth.AuthRequest;
import com.nivbuskila.facerecognition.auth.AuthResponse;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FaceRecognition implements FaceRecognitionService {
    private static final String TAG = "FaceRecognition";
    private static final String BASE_URL = "https://face-recognition-api-orcin.vercel.app/";

    private final Context context;
    private final ApiService apiService;
    private final SessionManager sessionManager;

    public FaceRecognition(Context context) {
        this.context = context;
        this.sessionManager = SessionManager.getInstance();

        // Setup logging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message ->
                Log.d(TAG, "API Call: " + message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Setup HTTP client
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(ApiService.class);
    }

    @Override
    public FaceRecognitionResult authenticate(String username, String password) {
        try {
            Log.d(TAG, "Starting authentication for user: " + username);

            AuthRequest request = new AuthRequest(username, password);
            Response<AuthResponse> response = apiService.login(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String token = response.body().getToken();
                if (token != null) {
                    sessionManager.setToken(token);
                    return new FaceRecognitionResult.Builder()
                            .setSuccess(true)
                            .build();
                }
            }
            return handleApiError(response, null);
        } catch (Exception e) {
            Log.e(TAG, "Authentication error", e);
            return new FaceRecognitionResult.Builder()
                    .setSuccess(false)
                    .setError("Authentication error: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public FaceRecognitionResult registerUser(Bitmap faceImage, String userId) {
        try {
            if (!sessionManager.hasToken()) {
                return new FaceRecognitionResult.Builder()
                        .setSuccess(false)
                        .setError("Not authenticated")
                        .build();
            }

            String imageBase64 = bitmapToBase64(faceImage);
            RegisterRequest request = new RegisterRequest(userId, imageBase64);

            String authHeader = "Bearer " + sessionManager.getToken();
            Response<ApiResponse> response = apiService.registerUser(authHeader, request).execute();

            if (response.isSuccessful() && response.body() != null) {
                return new FaceRecognitionResult.Builder()
                        .setSuccess(true)
                        .setUserId(userId)
                        .build();
            }
            return handleApiError(response, userId);
        } catch (Exception e) {
            Log.e(TAG, "Registration error", e);
            return new FaceRecognitionResult.Builder()
                    .setSuccess(false)
                    .setUserId(userId)
                    .setError("Registration error: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public FaceRecognitionResult getUserImage(String userId) {
        try {
            if (!sessionManager.hasToken()) {
                return new FaceRecognitionResult.Builder()
                        .setSuccess(false)
                        .setError("Not authenticated")
                        .build();
            }

            String authHeader = "Bearer " + sessionManager.getToken();
            Response<ApiResponse> response = apiService.getUserImage(authHeader, userId).execute();

            if (response.isSuccessful() && response.body() != null) {
                ApiResponse apiResponse = response.body();
                return new FaceRecognitionResult.Builder()
                        .setSuccess(true)
                        .setUserId(userId)
                        .setFaceData(apiResponse.getFaceData())
                        .build();
            }
            return handleApiError(response, userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user image", e);
            return new FaceRecognitionResult.Builder()
                    .setSuccess(false)
                    .setUserId(userId)
                    .setError("Error getting user image: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public FaceRecognitionResult verifyUser(Bitmap faceImage, String userId) {
        try {
            if (!sessionManager.hasToken()) {
                return new FaceRecognitionResult.Builder()
                        .setSuccess(false)
                        .setError("Not authenticated")
                        .build();
            }

            String imageBase64 = bitmapToBase64(faceImage);
            VerifyRequest request = new VerifyRequest(imageBase64);

            String authHeader = "Bearer " + sessionManager.getToken();
            Response<ApiResponse> response = apiService.verifyUser(authHeader, userId, request).execute();

            if (response.isSuccessful() && response.body() != null) {
                ApiResponse apiResponse = response.body();
                return new FaceRecognitionResult.Builder()
                        .setSuccess(apiResponse.isVerified())
                        .setUserId(userId)
                        .setConfidence(apiResponse.getConfidence())
                        .setError(apiResponse.getError())
                        .build();
            }
            return handleApiError(response, userId);
        } catch (Exception e) {
            Log.e(TAG, "Verification error", e);
            return new FaceRecognitionResult.Builder()
                    .setSuccess(false)
                    .setError("Verification error: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public void startFaceDetection(Context context, String userId) {
        Intent intent = FaceDetectionActivity.createIntent(context, userId);
        context.startActivity(intent);
    }
    @Override
    public FaceRecognitionResult getUsers() {
        try {
            if (!sessionManager.hasToken()) {
                return new FaceRecognitionResult.Builder()
                        .setSuccess(false)
                        .setError("Not authenticated")
                        .build();
            }

            String authHeader = "Bearer " + sessionManager.getToken();
            Response<List<ApiResponse>> response = apiService.getUsers(authHeader).execute();

            if (response.isSuccessful() && response.body() != null) {
                List<ApiResponse> users = response.body();
                return new FaceRecognitionResult.Builder()
                        .setSuccess(true)
                        .setUsers(users)
                        .build();
            }
            return handleApiError(response, null);
        } catch (Exception e) {
            Log.e(TAG, "Error getting users", e);
            return new FaceRecognitionResult.Builder()
                    .setSuccess(false)
                    .setError("Error getting users: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public FaceRecognitionResult compareFaces(Bitmap face1, Bitmap face2) {
        try {
            if (!sessionManager.hasToken()) {
                return new FaceRecognitionResult.Builder()
                        .setSuccess(false)
                        .setError("Not authenticated")
                        .build();
            }

            String face1Base64 = bitmapToBase64(face1);
            String face2Base64 = bitmapToBase64(face2);
            CompareRequest request = new CompareRequest(face1Base64, face2Base64);

            String authHeader = "Bearer " + sessionManager.getToken();
            Response<ApiResponse> response = apiService.compareFaces(authHeader, request).execute();

            if (response.isSuccessful() && response.body() != null) {
                ApiResponse apiResponse = response.body();
                return new FaceRecognitionResult.Builder()
                        .setSuccess(apiResponse.isVerified())
                        .setConfidence(apiResponse.getConfidence())
                        .setError(apiResponse.getError())
                        .build();
            }
            return handleApiError(response, null);
        } catch (Exception e) {
            Log.e(TAG, "Comparison error", e);
            return new FaceRecognitionResult.Builder()
                    .setSuccess(false)
                    .setError("Comparison error: " + e.getMessage())
                    .build();
        }
    }

    public class UpdateRequest {
        private final String faceData;

        public UpdateRequest(String imageBase64) {
            this.faceData = imageBase64;
        }

        public String getFaceData() {
            return faceData;
        }
    }

    @Override
    public FaceRecognitionResult updateUser(Bitmap faceImage, String userId) {
        try {
            if (!sessionManager.hasToken()) {
                return new FaceRecognitionResult.Builder()
                        .setSuccess(false)
                        .setError("Not authenticated")
                        .build();
            }

            String imageBase64 = bitmapToBase64(faceImage);
            UpdateRequest request = new UpdateRequest(imageBase64);

            String authHeader = "Bearer " + sessionManager.getToken();
            Response<ApiResponse> response = apiService.updateUser(authHeader, userId, request).execute();

            if (response.isSuccessful() && response.body() != null) {
                return new FaceRecognitionResult.Builder()
                        .setSuccess(true)
                        .setUserId(userId)
                        .build();
            }
            return handleApiError(response, userId);
        } catch (Exception e) {
            Log.e(TAG, "Update error", e);
            return new FaceRecognitionResult.Builder()
                    .setSuccess(false)
                    .setUserId(userId)
                    .setError("Update error: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public FaceRecognitionResult deleteUser(String userId) {
        try {
            if (!sessionManager.hasToken()) {
                return new FaceRecognitionResult.Builder()
                        .setSuccess(false)
                        .setError("Not authenticated")
                        .build();
            }

            String authHeader = "Bearer " + sessionManager.getToken();
            Response<ApiResponse> response = apiService.deleteUser(authHeader, userId).execute();

            if (response.isSuccessful() && response.body() != null) {
                return new FaceRecognitionResult.Builder()
                        .setSuccess(true)
                        .setUserId(userId)
                        .build();
            }
            return handleApiError(response, userId);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting user", e);
            return new FaceRecognitionResult.Builder()
                    .setSuccess(false)
                    .setUserId(userId)
                    .setError("Error deleting user: " + e.getMessage())
                    .build();
        }
    }

    private FaceRecognitionResult handleApiError(Response<?> response, String userId) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Unknown error";
            Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);

            String errorMessage;
            switch (response.code()) {
                case 401:
                    errorMessage = "Authentication failed";
                    break;
                case 409:
                    errorMessage = "User already exists";
                    break;
                case 400:
                    errorMessage = "Missing required fields";
                    break;
                default:
                    errorMessage = "Server error: " + errorBody;
            }

            return new FaceRecognitionResult.Builder()
                    .setSuccess(false)
                    .setUserId(userId)
                    .setError(errorMessage)
                    .build();
        } catch (Exception e) {
            Log.e(TAG, "Error handling API error", e);
            return new FaceRecognitionResult.Builder()
                    .setSuccess(false)
                    .setError("Unexpected error: " + e.getMessage())
                    .build();
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }
}