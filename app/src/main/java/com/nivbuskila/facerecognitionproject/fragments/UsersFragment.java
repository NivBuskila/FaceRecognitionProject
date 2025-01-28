package com.nivbuskila.facerecognitionproject.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nivbuskila.facerecognition.network.ApiResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nivbuskila.facerecognition.FaceRecognition;
import com.nivbuskila.facerecognition.models.FaceRecognitionResult;
import com.nivbuskila.facerecognitionproject.R;
import com.nivbuskila.facerecognitionproject.adapters.UsersAdapter;
import com.nivbuskila.facerecognitionproject.databinding.FragmentUsersBinding;
import com.nivbuskila.facerecognitionproject.models.User;



public class UsersFragment extends Fragment implements UsersAdapter.OnUserActionListener {
    private FragmentUsersBinding binding;
    private UsersAdapter adapter;
    private FaceRecognition faceRecognition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        faceRecognition = new FaceRecognition(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupSwipeRefresh();
        loadUsers();
    }

    private void setupRecyclerView() {
        adapter = new UsersAdapter(this);
        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.usersRecyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadUsers);
    }

    private void loadUsers() {
        showLoading(true);
        new Thread(() -> {
            try {
                FaceRecognitionResult result = faceRecognition.getUsers();
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    if (result.isSuccess() && result.getUsers() != null) {
                        List<User> users = new ArrayList<>();
                        for (ApiResponse response : result.getUsers()) {
                            User user = new User();
                            user.setUserId(response.getUserId());
                            user.setCreatedAt(response.getCreatedAt());
                            users.add(user);
                        }
                        adapter.updateUsers(users);
                        showEmptyState(users.isEmpty());
                    } else {
                        showError("Failed to load users", result.getError());
                        showEmptyState(true);
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    showError("Error", e.getMessage());
                    showEmptyState(true);
                });
            }
        }).start();
    }

    @Override
    public void onDeleteClick(User user) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUser(User user) {
        showLoading(true);
        new Thread(() -> {
            try {
                FaceRecognitionResult result = faceRecognition.deleteUser(user.getUserId());
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    if (result.isSuccess()) {
                        showMessage("User deleted successfully");
                        loadUsers(); // Reload the list
                    } else {
                        showError("Failed to delete user", result.getError());
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    showError("Error", e.getMessage());
                });
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

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.swipeRefresh.setRefreshing(false);
    }
    @Override
    public void onViewImageClick(User user) {
        showLoading(true);
        new Thread(() -> {
            try {
                FaceRecognitionResult result = faceRecognition.getUserImage(user.getUserId());
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    if (result.isSuccess() && result.getFaceData() != null) {
                        showUserImage(result.getFaceData());
                    } else {
                        showError("Error", result.getError());
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    showError("Error", e.getMessage());
                });
            }
        }).start();
    }

    private void showUserImage(String base64Image) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_user_image, null);
        ImageView imageView = view.findViewById(R.id.userImageView);

        // Convert base64 to Bitmap
        String base64Data = base64Image.split(",")[1];
        byte[] decodedString = Base64.decode(base64Data, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(bitmap);

        builder.setView(view)
                .setTitle("User Image")
                .setPositiveButton("Close", null)
                .show();
    }

    private void showEmptyState(boolean show) {
        binding.emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.usersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}