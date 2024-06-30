package com.example.salessync;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private Uri selectedImageUri;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private String categoryName;
    private ProgressBar progressBar;
    private TextInputEditText etName;
    private TextView tvUrl;
    private AlertDialog dialog;
    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        init(view);

        btnAddNewCategory.setOnClickListener(view1 -> {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.add_new_category_design, null);

            etName = v.findViewById(R.id.etName);
            Button btnSelectImage = v.findViewById(R.id.btnSelectImage);
            tvUrl = v.findViewById(R.id.tvUrl);
            btnSelectImage.setOnClickListener(v1 -> openImagePicker());

            AlertDialog.Builder addNewCategory = new AlertDialog.Builder(requireContext());

            addNewCategory.setTitle("Creating new Category")
                    .setView(v);
            addNewCategory.setPositiveButton("Save", (dialogInterface, i) -> {
                        categoryName = Objects.requireNonNull(etName.getText()).toString().trim();
                        String imageFileName = System.currentTimeMillis() + ".jpg";
                        if (categoryName.isEmpty()) {
                            etName.setError("required");
                            Toast.makeText(getContext(),"Name was not specified, no category formed",Toast.LENGTH_SHORT).show();
                        }
                        else if (selectedImageUri == null) {
                            tvUrl.setText("No image selected");
                            Toast.makeText(getContext(),"Image was not specified, no category formed",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            dialog.show();
                            // Upload image to Firebase Storage
                            StorageReference storageRef = storage.getReference().child("category_images").child(imageFileName);
                            UploadTask uploadTask = storageRef.putFile(selectedImageUri);
                            uploadTask.continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(requireContext(),"Some unknown error occurred",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    throw Objects.requireNonNull(task.getException());
                                }

                                // Continue with the task to get the download URL
                                return storageRef.getDownloadUrl();
                            }).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    // Create a HashMap to store category data
                                    HashMap<String, Object> categoryData = new HashMap<>();
                                    categoryData.put("name", categoryName);
                                    categoryData.put("imageUrl", downloadUri.toString());

                                    // Save category data to Realtime Database
                                    DatabaseReference categoriesRef = database.getReference("categories");
                                    String categoryId = categoriesRef.push().getKey();
                                    assert categoryId != null;
                                    categoriesRef.child(categoryId).setValue(categoryData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dialog.dismiss();
                                                    Toast.makeText(requireContext(), "Category saved successfully", Toast.LENGTH_SHORT).show();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialog.dismiss();
                                                    Toast.makeText(requireContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                } else {
                                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }



                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


            addNewCategory.create().show();
            selectedImageUri=null;

        });

        return view;
    }

    private void init(View view) {
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        btnAddNewCategory = view.findViewById(R.id.btnAddNewCategory);
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(requireContext());
        View v = LayoutInflater.from(getContext()).inflate(R.layout.progress_bar, null);
        dialogBuilder.setView(v);
        dialog=dialogBuilder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            assert selectedImageUri != null;
            tvUrl.setText(selectedImageUri.toString());
            if(Objects.requireNonNull(etName.getText()).toString().isEmpty()){
                etName.setError("Required");
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }
}
