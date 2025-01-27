package com.nivbuskila.facerecognitionproject.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nivbuskila.facerecognition.FaceRecognition;
import com.nivbuskila.facerecognition.models.FaceRecognitionResult;
import com.nivbuskila.facerecognitionproject.R;
import com.nivbuskila.facerecognitionproject.databinding.FragmentSettingsBinding;
import com.nivbuskila.facerecognitionproject.utils.PreferencesManager;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private PreferencesManager preferencesManager;
    private FaceRecognition faceRecognition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesManager = new PreferencesManager(requireContext());
        faceRecognition = new FaceRecognition(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupVersionInfo();
        loadSavedCredentials();
        setupSaveButton();
    }

    private void setupVersionInfo() {
        try {
            PackageInfo pInfo = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0);
            binding.versionText.setText("Version: " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            binding.versionText.setText("Version: Unknown");
        }

        // SDK version would come from the SDK's BuildConfig
        binding.sdkVersionText.setText("SDK Version: 1.0.0");
    }

    private void loadSavedCredentials() {
        binding.usernameInput.setText(preferencesManager.getUsername());
        binding.passwordInput.setText(preferencesManager.getPassword());
    }

    private void setupSaveButton() {
        binding.saveButton.setOnClickListener(v -> saveCredentials());
    }

    private void saveCredentials() {
        String username = binding.usernameInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Invalid Input", "Please enter both username and password");
            return;
        }

        // Verify credentials before saving
        new Thread(() -> {
            try {
                FaceRecognitionResult result = faceRecognition.authenticate(username, password);
                requireActivity().runOnUiThread(() -> {
                    if (result.isSuccess()) {
                        preferencesManager.saveCredentials(username, password);
                        showMessage("Credentials saved successfully");

                        // Navigate back to home screen after successful save
                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.navigation_home);

                        // Make bottom navigation visible again if it was hidden
                        if (getActivity() != null) {
                            BottomNavigationView bottomNav = getActivity().findViewById(R.id.navView);
                            if (bottomNav != null) {
                                bottomNav.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        showError("Authentication Failed", "Invalid username or password");
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        showError("Error", "Failed to verify credentials: " + e.getMessage())
                );
            }
        }).start();
    }

    private void showError(String title, String message) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}