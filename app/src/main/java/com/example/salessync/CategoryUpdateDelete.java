package com.example.salessync;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CategoryUpdateDelete extends AppCompatActivity {
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private AlertDialog dialog;
    TextView tvName;
    RecyclerView rvProducts;
    FloatingActionButton fabAddNewProduct;
    String categoryName, productName,productQuantity,productPrice;
    EditText etProductName, etProductQuantity, etProductPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Intent intent=getIntent();
        categoryName = intent.getStringExtra("name");
        tvName.setText(categoryName);

        fabAddNewProduct.setOnClickListener(view1 -> showAddProductDialog());

    }

    private void showAddProductDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.add_new_product_design, null);
        etProductName=v.findViewById(R.id.etProductName);
        etProductQuantity=v.findViewById(R.id.etProductQuantity);
        etProductPrice=v.findViewById(R.id.etProductPrice);
        new AlertDialog.Builder(this)
                .setView(v)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveProduct();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    private void saveProduct() {
        productName=etProductName.getText().toString().trim();
        productQuantity=etProductQuantity.getText().toString();
        productPrice=etProductPrice.getText().toString();

        if(validateInput()){
            saveProductToDatabase();
        }
    }

    private void saveProductToDatabase() {
        dialog.show();
        HashMap<String, Object> productData=new HashMap<>();
        productData.put("categoryName",categoryName);
        productData.put("productName",productName);
        productData.put("quantity",productQuantity);
        productData.put("price",productPrice);

        DatabaseReference productsRef = database.getReference("products");
        String categoryId = productsRef.push().getKey();
        assert categoryId != null;
        productsRef.child(categoryId).setValue(productData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
                        Toast.makeText(CategoryUpdateDelete.this, "Product saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(CategoryUpdateDelete.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput() {
        if(productName.isEmpty()){
            etProductName.setText("Name cant be empty");
            return false;
        }
        else if(productQuantity.isEmpty()){
            etProductQuantity.setError("Quantity cant be empty");
            return false;
        }
        else if(productPrice.isEmpty()){
            etProductPrice.setError("Price cant be empty");
            return false;
        }
        return true;
    }

    private void init(){
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_update_delete);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database = FirebaseDatabase.getInstance();
        tvName = findViewById(R.id.tvCategoryName);
        rvProducts=findViewById(R.id.rvProducts);
        fabAddNewProduct=findViewById(R.id.fabAddNewProduct);

        dialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.progress_bar, null))
                .create();

    }


}