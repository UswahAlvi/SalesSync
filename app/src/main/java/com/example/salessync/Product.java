package com.example.salessync;

public class Product {
    String productName;
    int quantity;
    Double price;

    public Product() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Product(String productName, int quantity, Double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }
}
