package com.nivbuskila.facerecognitionproject.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.nivbuskila.facerecognitionproject.databinding.FragmentFaceCompareBinding;

import java.io.IOException;

public class FaceCompareFragment extends Fragment {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int GALLERY_PERMISSION_CODE = 101;

    private FragmentFaceCompareBinding binding;
    private FaceRecognition faceRecognition;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Bitmap face1Image;
    private Bitmap face2Image;
    private boolean isCapturingFirstImage = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        faceRecognition = new FaceRecognition(requireContext());
        setupLaunchers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFaceCompareBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupClickListeners();
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
        binding.captureFace1Button.setOnClickListener(v -> {
            isCapturingFirstImage = true;
            showImagePickerDialog();
        });

        binding.captureFace2Button.setOnClickListener(v -> {
            isCapturingFirstImage = false;
            showImagePickerDialog();
        });

        binding.compareFacesButton.setOnClickListener(v -> compareFaces());
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
            Bitmap bitmap = (Bitmap) data;
            if (isCapturingFirstImage) {
                face1Image = bitmap;
                binding.faceImage1.setImageBitmap(bitmap);
            } else {
                face2Image = bitmap;
                binding.faceImage2.setImageBitmap(bitmap);
            }
            clearResults();
        }
    }

    private void compareFaces() {
        if (!validateInput()) return;

        showProgress(true);
        new Thread(() -> {
            try {
                FaceRecognitionResult result = faceRecognition.compareFaces(face1Image, face2Image);
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    handleComparisonResult(result);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showProgress(false);
                    showError("Comparison Error", e.getMessage());
                });
            }
        }).start();
    }

    private boolean validateInput() {
        if (face1Image == null || face2Image == null) {
            showError("Input Error", "Please capture both faces for comparison");
            return false;
        }
        return true;
    }

    private void handleComparisonResult(FaceRecognitionResult result) {
        if (result.isSuccess()) {
            String message = String.format("Faces match!\nConfidence: %.2f%%",
                    result.getConfidence() * 100);
            binding.resultText.setText(message);
            binding.resultText.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.design_default_color_primary));
            binding.errorText.setText("");
        } else {
            showError("Comparison Failed", result.getError());
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
        binding.compareFacesButton.setEnabled(!show);
        binding.captureFace1Button.setEnabled(!show);
        binding.captureFace2Button.setEnabled(!show);
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