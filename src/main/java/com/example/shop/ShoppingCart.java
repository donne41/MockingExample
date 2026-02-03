package com.example.shop;

import java.util.ArrayList;

public class ShoppingCart {

    ArrayList<String> basket = new ArrayList<>();


    public void addProduct(String productName) {
        basket.add(productName);
    }

    public void removeProduct(String productName) {
        basket.remove(productName);
    }

    public ArrayList<String> getProducts() {
        return basket;
    }
}
