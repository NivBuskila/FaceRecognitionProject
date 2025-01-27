package com.nivbuskila.facerecognition.network;

import com.nivbuskila.facerecognition.FaceRecognition;
import com.nivbuskila.facerecognition.auth.AuthRequest;
import com.nivbuskila.facerecognition.auth.AuthResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/auth/login")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<AuthResponse> login(@Body AuthRequest request);

    @GET("api/users")
    Call<List<ApiResponse>> getUsers(@Header("Authorization") String authHeader);

    @POST("api/users")
    Call<ApiResponse> registerUser(@Header("Authorization") String authHeader,
                                   @Body RegisterRequest request);


    @GET("api/users/{userId}/image")
    Call<ApiResponse> getUserImage(@Header("Authorization") String authHeader,
                                   @Path("userId") String userId);

    @POST("api/users/{userId}/verify")
    Call<ApiResponse> verifyUser(@Header("Authorization") String authHeader,
                                 @Path("userId") String userId,
                                 @Body VerifyRequest request);

    @POST("api/faces/compare")
    Call<ApiResponse> compareFaces(@Header("Authorization") String authHeader,
                                   @Body CompareRequest request);

    @DELETE("api/users/{userId}")
    Call<ApiResponse> deleteUser(@Header("Authorization") String authHeader,
                                 @Path("userId") String userId);


    @PUT("api/users/{userId}")
    Call<ApiResponse> updateUser(@Header("Authorization") String authHeader,
                                 @Path("userId") String userId,
                                 @Body FaceRecognition.UpdateRequest request);
}