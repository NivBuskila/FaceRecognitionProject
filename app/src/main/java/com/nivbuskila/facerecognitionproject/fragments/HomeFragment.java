package com.nivbuskila.facerecognitionproject.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nivbuskila.facerecognition.FaceRecognition;
import com.nivbuskila.facerecognition.models.FaceRecognitionResult;
import com.nivbuskila.facerecognitionproject.R;
import com.nivbuskila.facerecognitionproject.databinding.FragmentHomeBinding;

import java.io.IOException;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int GALLERY_PERMISSION_CODE = 101;

    private FragmentHomeBinding binding;
    private FaceRecognition faceRecognition;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Bitmap capturedImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        faceRecognition = new FaceRecognition(requireContext());
        setupLaunchers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupClickListeners();
        clearResults();
        Log.d(TAG, "Fragment created and initialized");
    }

    private void setupLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        handleImageResult(result.getData().getExtras().get("data"));
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        try {
                            Uri imageUri = result.getData().getData();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    requireContext().getContentResolver(), imageUri);
                            handleImageResult(bitmap);
                        } catch (IOException e) {
                            showError("Failed to load image", e.getMessage());
                        }
                    }
                });
    }

    private void setupClickListeners() {
        binding.captureButton.setOnClickListener(v -> showImagePickerDialog());
        binding.registerButton.setOnClickListener(v -> registerUser());
        binding.verifyButton.setOnClickListener(v -> verifyUser());
        binding.updateButton.setOnClickListener(v -> updateExistingUser());
        binding.getUserImageButton.setOnClickListener(v -> getUserImage());
        binding.clearImageButton.setOnClickListener(v -> clearImage());
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermission();
                    } else {
                        checkGalleryPermission();
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void clearImage() {
        binding.previewImage.setImageResource(android.R.color.transparent);  // או R.color.grey_background
        capturedImage = null;
        clearResults();
    }

    private void checkGalleryPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        GALLERY_PERMISSION_CODE);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        GALLERY_PERMISSION_CODE);
            } else {
                openGallery();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void handleImageResult(Object data) {
        if (data instanceof Bitmap) {
            capturedImage = (Bitmap) data;
            binding.previewImage.setImageBitmap(capturedImage);
            clearResults();
        }
    }

    private void registerUser() {
        if (!validateInput()) return;

        showProgress(true);
        String userId = binding.userIdInput.getText().toString().trim();

        new Thread(() -> {
            try {
                FaceRecognitionResult result = faceRecognition.registerUser(capturedImage, userId);
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    handleRegistrationResult(result);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    showError("Registration Error", e.getMessage());
                });
            }
        }).start();
    }

    private void getUserImage() {
        if (binding.userIdInput.getText().toString().trim().isEmpty()) {
            binding.userIdLayout.setError("Please enter a user ID");
            return;
        }

        showProgress(true);
        String userId = binding.userIdInput.getText().toString().trim();

        new Thread(() -> {
            try {
                FaceRecognitionResult result = faceRecognition.getUserImage(userId);
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    if (result.isSuccess() && result.getFaceData() != null) {
                        String base64Data = result.getFaceData().contains(",")
                                ? result.getFaceData().split(",")[1]
                                : result.getFaceData();
                        byte[] decodedString = Base64.decode(base64Data, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        binding.previewImage.setImageBitmap(bitmap);
                    } else {
                        showError("Error", result.getError());
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    showError("Error", e.getMessage());
                });
            }
        }).start();
    }

    private void updateExistingUser() {
        if (!validateInput()) return;

        showProgress(true);
        String userId = binding.userIdInput.getText().toString().trim();

        new Thread(() -> {
            try {
                FaceRecognitionResult result = faceRecognition.updateUser(capturedImage, userId);
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    handleUpdateResult(result);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    showError("Update Error", e.getMessage());
                });
            }
        }).start();
    }

    private void verifyUser() {
        if (!validateInput()) return;

        showProgress(true);
        String userId = binding.userIdInput.getText().toString().trim();

        new Thread(() -> {
            try {
                FaceRecognitionResult result = faceRecognition.verifyUser(capturedImage, userId);
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    handleVerificationResult(result);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    showError("Verification Error", e.getMessage());
                });
            }
        }).start();
    }

    private boolean validateInput() {
        if (capturedImage == null) {
            showError("Input Error", "Please capture an image first");
            return false;
        }

        String userId = binding.userIdInput.getText().toString().trim();
        if (userId.isEmpty()) {
            binding.userIdLayout.setError("Please enter a user ID");
            return false;
        }

        binding.userIdLayout.setError(null);
        return true;
    }

    private void handleRegistrationResult(FaceRecognitionResult result) {
        if (result.isSuccess()) {
            binding.resultText.setText("Registration successful!");
            binding.resultText.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.design_default_color_primary));
            binding.errorText.setText("");
        } else {
            showError("Registration Failed", result.getError());
        }
    }

    private void handleUpdateResult(FaceRecognitionResult result) {
        if (result.isSuccess()) {
            binding.resultText.setText("User updated successfully!");
            binding.resultText.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.design_default_color_primary));
            binding.errorText.setText("");
        } else {
            showError("Update Failed", result.getError());
        }
    }

    private void handleVerificationResult(FaceRecognitionResult result) {
        if (result.isSuccess()) {
            String message = String.format("Verification successful!\nConfidence: %.2f%%",
                    result.getConfidence() * 100);
            binding.resultText.setText(message);
            binding.resultText.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.design_default_color_primary));
            binding.errorText.setText("");
        } else {
            showError("Verification Failed", result.getError());
        }
    }

    private void showError(String title, String message) {
        binding.resultText.setText(title);
        binding.resultText.setTextColor(ContextCompat.getColor(requireContext(),
                R.color.design_default_color_error));
        binding.errorText.setText(message);
    }

    private void showProgress(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.registerButton.setEnabled(!show);
        binding.verifyButton.setEnabled(!show);
        binding.updateButton.setEnabled(!show);
        binding.captureButton.setEnabled(!show);
        binding.getUserImageButton.setEnabled(!show);
        binding.clearImageButton.setEnabled(!show);
    }

    private void clearResults() {
        binding.resultText.setText("");
        binding.errorText.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == CAMERA_PERMISSION_CODE) {
                openCamera();
            } else if (requestCode == GALLERY_PERMISSION_CODE) {
                openGallery();
            }
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}