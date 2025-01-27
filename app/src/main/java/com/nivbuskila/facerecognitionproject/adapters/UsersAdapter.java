package com.nivbuskila.facerecognitionproject.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nivbuskila.facerecognitionproject.databinding.ItemUserBinding;
import com.nivbuskila.facerecognitionproject.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private final List<User> users = new ArrayList<>();
    private final OnUserActionListener listener;
    private static final String[] DATE_FORMATS = {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",  // ISO format
            "EEE, dd MMM yyyy HH:mm:ss z"     // RFC format
    };

    public interface OnUserActionListener {
        void onDeleteClick(User user);
        void onViewImageClick(User user);
        void onStartDetectionClick(User user);
    }

    public UsersAdapter(OnUserActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<User> newUsers) {
        users.clear();
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(User user) {
            binding.userIdText.setText(user.getUserId());

            // Try to parse the date with different formats
            String dateStr = user.getCreatedAt();
            if (dateStr != null) {
                for (String format : DATE_FORMATS) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                        Date date = sdf.parse(dateStr);
                        if (date != null) {
                            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
                            binding.createdAtText.setText(outputFormat.format(date));
                            break;
                        }
                    } catch (ParseException e) {
                        // Try next format
                        continue;
                    }
                }
            } else {
                binding.createdAtText.setText("Date not available");
            }

            binding.deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(user);
                }
            });

            binding.viewImageButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewImageClick(user);
                }
            });

            binding.startDetectionButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStartDetectionClick(user);
                }
            });
        }
    }
}