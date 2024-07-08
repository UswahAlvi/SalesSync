package com.example.salessync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CategoryAdapter extends FirebaseRecyclerAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model) {

        Glide.with(holder.itemView.getContext())
                .load(model.getImageUrl())
                .into(holder.ivCategoryPic);
        holder.tvCategoryName.setText(model.getName());
        String key = getRef(position).getKey();
        holder.itemView.setOnLongClickListener(v -> {
            showOptionsDialog(v.getContext(), model, key);
            return false;
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryUpdateDelete.class);
                intent.putExtra("CATEGORY_KEY", key);
                v.getContext().startActivity(intent);
            }
        });
    }

    private void showOptionsDialog(Context context, Category category, String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Action")
                .setItems(new CharSequence[]{"Update", "Delete"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Update category action
                            showEditDialog(context, category, key); 
                            break;
                        case 1:
                            // Delete category action
                            deleteCategory(context, key);
                            break;
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showEditDialog(Context context, Category category, String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.edit_category_dialogg, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etName);
        ImageView ivSelectedImage = view.findViewById(R.id.ivSelectedImage);

        // Populate dialog fields if category is not null (for edit)
        if (category != null) {
            etName.setText(category.getName());
            Glide.with(context)
                    .load(category.getImageUrl())
                    .into(ivSelectedImage);
        }


        builder
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        if (category != null) {
                            updateCategory(context, key, newName, category.getImageUrl());
                        } else {
                            Toast.makeText(context, "Category not found for editing", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        etName.setError("Required");
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void updateCategory(Context context, String key, String newName, String imageUrl) {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories").child(key);
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        categoriesRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    notifyDataSetChanged();
                    Toast.makeText(context, "Category updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update category: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteCategory(Context context, String key) {
        // Implement deletion logic here (e.g., using Firebase Database)
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories").child(key);
        categoryRef.removeValue()
                .addOnSuccessListener(unused -> {
                    notifyDataSetChanged();
                    Toast.makeText(context, "Category deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete category: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_view_design , parent, false);
        return new CategoryViewHolder(view);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        ImageView ivCategoryPic;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            ivCategoryPic = itemView.findViewById(R.id.ivCategoryPic);
        }
    }


}
