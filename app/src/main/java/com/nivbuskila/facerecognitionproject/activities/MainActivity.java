package com.nivbuskila.facerecognitionproject.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nivbuskila.facerecognition.FaceRecognition;
import com.nivbuskila.facerecognition.models.FaceRecognitionResult;
import com.nivbuskila.facerecognitionproject.R;
import com.nivbuskila.facerecognitionproject.databinding.ActivityMainBinding;
import com.nivbuskila.facerecognitionproject.utils.PreferencesManager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private PreferencesManager preferencesManager;
    private FaceRecognition faceRecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferencesManager = new PreferencesManager(this);
        faceRecognition = new FaceRecognition(this);

        setupNavigation();
        authenticateUser();
    }

    private void setupNavigation() {
        // Get NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // Get NavController
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            // Setup Bottom Navigation
            NavigationUI.setupWithNavController(binding.navView, navController);
        }
    }

    private void authenticateUser() {
        String username = preferencesManager.getUsername();
        String password = preferencesManager.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            // Navigate to settings if no credentials
            navController.navigate(R.id.navigation_settings);
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                FaceRecognitionResult result = faceRecognition.authenticate(username, password);
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if (!result.isSuccess()) {
                        // Navigate to settings if authentication fails
                        navController.navigate(R.id.navigation_settings);
                    } else {
                        // Navigate to home if authentication succeeds
                        navController.navigate(R.id.navigation_home);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    navController.navigate(R.id.navigation_settings);
                });
            }
        }).start();
    }
}