package com.example.salessync;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class InventoryFragment extends Fragment {
    private FloatingActionButton btnAddNewCategory;
    private CategoryAdapter categoryAdapter;
    private RecyclerView rvCategories;
    private DatabaseReference reference;
    private Uri selectedImageUri;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private String categoryName;
    private TextInputEditText etName;
    private TextView tvUrl;
    private AlertDialog dialog;

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (categoryAdapter != null) {
            categoryAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (categoryAdapter != null) {
            categoryAdapter.stopListening();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        init(view);

        btnAddNewCategory.setOnClickListener(view1 -> showAddCategoryDialog());

        return view;
    }

    private void init(View view) {
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        btnAddNewCategory = view.findViewById(R.id.btnAddNewCategory);
        reference = database.getReference();

        setupRecyclerView(view);

        dialog = new AlertDialog.Builder(requireContext())
                .setView(LayoutInflater.from(getContext()).inflate(R.layout.progress_bar, null))
                .create();
    }

    private void setupRecyclerView(View view) {
        rvCategories = view.findViewById(R.id.rvCategories);
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(reference.child("categories"), Category.class)
                .build();

        categoryAdapter = new CategoryAdapter(options);
        rvCategories.setHasFixedSize(true);
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvCategories.setAdapter(categoryAdapter);
    }

    private void showAddCategoryDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.add_new_category_design, null);
        etName = v.findViewById(R.id.etName);
        Button btnSelectImage = v.findViewById(R.id.btnSelectImage);
        tvUrl = v.findViewById(R.id.tvUrl);
        btnSelectImage.setOnClickListener(v1 -> openImagePicker());

        new AlertDialog.Builder(requireContext())
                .setTitle("Creating new Category")
                .setView(v)
                .setPositiveButton("Save", (dialogInterface, i) -> saveCategory())
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    // No action needed
                })
                .create()
                .show();
        selectedImageUri = null;
    }

    private void saveCategory() {
        categoryName = Objects.requireNonNull(etName.getText()).toString().trim();
        if (validateInput()) {
            uploadCategoryImage();
        }
    }

    private boolean validateInput() {
        if (categoryName.isEmpty()) {
            etName.setError("required");
            Toast.makeText(getContext(), "Name was not specified, no category formed", Toast.LENGTH_SHORT).show();
            return false;
        } else if (selectedImageUri == null) {
            tvUrl.setText("No image selected");
            Toast.makeText(getContext(), "Image was not specified, no category formed", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadCategoryImage() {
        dialog.show();
        String imageFileName = System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = storage.getReference().child("category_images").child(imageFileName);
        storageRef.putFile(selectedImageUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                dialog.dismiss();
                throw Objects.requireNonNull(task.getException());
            }
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveCategoryToDatabase(task.getResult().toString());
            } else {
                dialog.dismiss();
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCategoryToDatabase(String downloadUrl) {
        HashMap<String, Object> categoryData = new HashMap<>();
        categoryData.put("name", categoryName);
        categoryData.put("imageUrl", downloadUrl);

        DatabaseReference categoriesRef = database.getReference("categories");
        String categoryId = categoriesRef.push().getKey();
        assert categoryId != null;
        categoriesRef.child(categoryId).setValue(categoryData)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Category saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            tvUrl.setText(selectedImageUri != null ? selectedImageUri.toString() : "No image selected");
        }
    }
}
