package com.example.salessync;

import android.net.Uri;

import java.net.URI;

public class Category {
    String name;
    String imageUrl;
    Product products;

    public String getName() {
        return name;
    }

    public void setCategoryName(String categoryName) {
        this.name = categoryName;
    }

    public Category(String categoryName, String getCategoryUrl) {
        this.name = categoryName;
        this.imageUrl = getCategoryUrl;
    }
    public Category(String categoryName, String getCategoryUrl, Product products) {
        this.name = categoryName;
        this.imageUrl = getCategoryUrl;
        this.products = products;
    }

    public Product getProducts() {
        return products;
    }


    public void setGetCategoryUrl(String getCategoryUrl) {
        this.imageUrl = getCategoryUrl;
    }

    public void setProducts(Product products) {
        this.products = products;
    }

    public Category() {
    }


    public String getImageUrl() {
        return imageUrl;
    }
}
