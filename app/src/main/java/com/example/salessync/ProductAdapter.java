package com.example.salessync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ProductAdapter extends FirebaseRecyclerAdapter {

    public ProductAdapter(@NonNull FirebaseRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i, @NonNull Object o) {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_product_item_design , parent, false);
        return new CategoryAdapter.CategoryViewHolder(view);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView tvProductName,tvProductQuantity,tvProductPrice;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName=itemView.findViewById(R.id.tvProductName);
            tvProductQuantity=itemView.findViewById(R.id.tvProductQuantity);
            tvProductPrice=itemView.findViewById(R.id.tvProductPrice);
        }
    }

}
